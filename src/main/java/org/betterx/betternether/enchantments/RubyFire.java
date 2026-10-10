package org.betterx.betternether.enchantments;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherEnchantments;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RubyFire {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private static final Map<Item, RecipeHolder<BlastingRecipe>> FIRE_CONVERSIONS = new HashMap<>();
    public static final ThreadLocal<List<ItemStack>> convertedDrops = ThreadLocal.withInitial(ArrayList::new);

    public static boolean getDrops(
            BlockState brokenBlock,
            ServerLevel level,
            BlockPos blockPos,
            Player player,
            ItemStack breakingItem
    ) {
        final var rubyFire = NetherEnchantments.RUBY_FIRE.getHolder(player.registryAccess());
        if (rubyFire == null) return false;
        final int fireLevel = EnchantmentHelper.getItemEnchantmentLevel(rubyFire, breakingItem);
        // Silk Touch asks for the block itself, so it wins: no smelting, and the vanilla drops (and
        // the vanilla "no XP with Silk Touch") apply unchanged.
        if (fireLevel > 0 && EnchantmentUtils.getItemEnchantmentLevel(level, Enchantments.SILK_TOUCH, breakingItem) == 0) {
            if (FIRE_CONVERSIONS.isEmpty()) buildConversionTable(level);

            boolean didConvert = false;
            float xpDrop = 0;
            try {
                final List<ItemStack> drops = Block.getDrops(brokenBlock, level, blockPos, null, player, breakingItem);
                convertedDrops.get().clear();

                for (ItemStack stack : drops) {
                    RecipeHolder<BlastingRecipe> resultHolder = FIRE_CONVERSIONS.get(stack.getItem());
                    BlastingRecipe result = resultHolder != null ? resultHolder.value() : null;
                    if (result != null) {
                        didConvert = true;
                        final ItemStack resultStack = result.assemble(
                                new SingleRecipeInput(stack),
                                level.registryAccess()
                        );
                        xpDrop += result.experience() * stack.getCount();
                        convertedDrops.get()
                                      .add(new ItemStack(
                                              resultStack.getItem(),
                                              resultStack.getCount() * stack.getCount()
                                      ));
                    } else {
                        convertedDrops.get().add(stack);
                    }
                }

                // Some ores drop something that does not smelt (nether gold ore: nuggets) while the
                // ore block itself does. Smelt the ore then, as if it had been mined with Silk Touch
                // and put into a blast furnace. Ores whose drop already *is* the smelting result
                // (quartz, diamond, ...) keep their own drops, so Fortune still counts for them.
                if (!didConvert && !drops.isEmpty() && brokenBlock.is(CommonBlockTags.ORES)) {
                    final RecipeHolder<BlastingRecipe> oreHolder = FIRE_CONVERSIONS.get(brokenBlock.getBlock().asItem());
                    if (oreHolder != null) {
                        final ItemStack resultStack = oreHolder.value().assemble(
                                new SingleRecipeInput(new ItemStack(brokenBlock.getBlock())),
                                level.registryAccess()
                        );
                        if (!resultStack.isEmpty() && drops.stream().noneMatch(d -> d.is(resultStack.getItem()))) {
                            didConvert = true;
                            xpDrop = oreHolder.value().experience();
                            convertedDrops.get().clear();
                            convertedDrops.get().add(resultStack);
                        }
                    }
                }
            } catch (Exception e) {
                BetterNether.C.log.error("Unable to get Drops for " + breakingItem, e);
            }

            if (didConvert) {
                if (xpDrop > 0) {
                    popExperience(level, blockPos, xpDrop);
                }
                convertedDrops.get().forEach(itemStack -> Block.popResource(level, blockPos, itemStack));
                brokenBlock.spawnAfterBreak(level, blockPos, breakingItem, true);
                convertedDrops.get().clear();
                return true;
            }
            convertedDrops.get().clear();
        }

        return false;
    }

    /**
     * Pays out smelting XP the way a furnace does: recipe XP is fractional (raw iron 0.7, quartz 0.2),
     * so the whole part is awarded and the remainder is a chance at one more point.
     */
    private static void popExperience(ServerLevel level, BlockPos blockPos, float amount) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            int xp = Mth.floor(amount);
            final float fraction = Mth.frac(amount);
            if (fraction != 0.0f && level.getRandom().nextFloat() < fraction) {
                xp++;
            }
            if (xp > 0) {
                ExperienceOrb.award(level, Vec3.atCenterOf(blockPos), xp);
            }
        }
    }

    private static void buildConversionTable(ServerLevel level) {
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof BlastingRecipe blasting)) continue;
            @SuppressWarnings("unchecked")
            final RecipeHolder<BlastingRecipe> r = (RecipeHolder<BlastingRecipe>) holder;
            for (Holder<Item> itemHolder : blasting.input().items().toList()) {
                final Item item = itemHolder.value();
                if (item instanceof BlockItem blitem) {
                    if (blitem.getBlock().defaultBlockState().is(CommonBlockTags.IS_OBSIDIAN)) {
                        continue;
                    }
                }
                FIRE_CONVERSIONS.put(item, r);
            }
        }
    }
}
