package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code betternether:obsidian_breaker} - the enchantment, the attribute it drives, and the
 * mining speed that attribute is supposed to produce.
 * <p>
 * The chain under test is: the enchantment's {@code EnchantmentAttributeEffect} adds to
 * {@code betternether:player.obsidian_block_break_speed}, {@code PlayerMixin} adds that attribute to
 * every player and wraps {@code Player#getDestroySpeed}, and {@code ObsidianBreaker} multiplies the
 * speed only for blocks in {@code #betternether:obsidian_breaker_mineable}. All of it is player-only,
 * which is exactly why it cannot be checked from a server console.
 */
public class ObsidianBreakerGameTest {
    private static final BlockPos PLAYER_POS = new BlockPos(1, 2, 1);

    /**
     * Puts {@code stack} in the main hand and applies its enchantment attribute effects.
     * <p>
     * A GameTest mock player never runs {@code LivingEntity#detectEquipmentUpdates}, so the
     * {@code EnchantmentAttributeEffect} would never be applied on its own.
     * {@code EnchantmentHelper#runLocationChangedEffects} is the exact call vanilla makes from
     * {@code collectEquipmentChanges}, so driving it by hand skips only vanilla's own change detection
     * and still exercises the enchantment definition end to end.
     */
    private static void equipAndApplyEnchantments(
            GameTestHelper helper,
            ServerPlayer player,
            ItemStack stack
    ) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        // ItemStack#forEachModifier yields the item's own attribute modifiers *and* those contributed by
        // its enchantments' ATTRIBUTES effects, which is where obsidian_breaker's bonus lives. This is
        // the same loop LivingEntity#collectEquipmentChanges runs; only vanilla's change detection is
        // skipped, not the enchantment definition itself.
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            final AttributeInstance instance = player.getAttributes().getInstance(attribute);
            if (instance != null) {
                instance.removeModifier(modifier.id());
                instance.addTransientModifier(modifier);
            }
        });

        EnchantmentHelper.runLocationChangedEffects(
                helper.getLevel(), stack, player, EquipmentSlot.MAINHAND
        );
    }

    /**
     * A stack as the game would hand it out.
     * <p>
     * {@code new ItemStack(item)} alone is bare: the default enchantments live in
     * {@code ItemWithCustomStack#setupItemStack}, which only runs on the creative-tab, {@code /give},
     * crafting and smithing paths. Skipping it silently produces an unenchanted pickaxe and a test that
     * measures nothing.
     */
    private static ItemStack pickaxe(GameTestHelper helper, Item item) {
        final ItemStack stack = new ItemStack(item);
        ItemStackHelper.callItemStackSetupIfPossible(stack, helper.getLevel().registryAccess());
        return stack;
    }


    /**
     * The structural rule behind the tag: "is obsidian" is decided once, by
     * {@code NetherMaterial.obsidian()}, and the mineable tag is just {@code #wover:is_obsidian}. So every
     * obsidian block BetterNether registers has to be in it - if one is not, that block skipped the
     * common material call, which is a bug in the block rather than something to patch into a list.
     * <p>
     * The glass panes are the one deliberate exclusion: they are strength 0.3 rather than obsidian's 50,
     * are declared with {@code NetherMaterial.glass()}, and need no help being mined.
     */
    @GameTest
    public void everyObsidianBlockIsCoveredByTheTag(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        BuiltInRegistries.BLOCK.entrySet().forEach(entry -> {
            final Identifier id = entry.getKey().identifier();
            if (!id.getNamespace().equals("betternether")) return;
            if (!id.getPath().contains("obsidian")) return;
            if (id.getPath().endsWith("glass_pane")) return;

            if (!entry.getValue().defaultBlockState().is(NetherTags.OBSIDIAN_BREAKER_MINEABLE)) {
                failures.add(id + " is an obsidian block but is not in #betternether:obsidian_breaker_mineable"
                        + " - it is probably missing a NetherMaterial.obsidian()/obsidianPortalFrame() trait");
            }
        });

        failIfAny(helper, "Obsidian block coverage regression", failures);
        helper.succeed();
    }

    /** The tag decides which blocks the bonus applies to; a wrong tag is a silent, total regression. */
    @GameTest
    public void mineableTagCoversObsidianButNotStone(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // The obsidian family, plus the derived shapes that keep obsidian's toughness.
        for (Block block : List.of(
                Blocks.OBSIDIAN,
                Blocks.CRYING_OBSIDIAN,
                NetherObsidianBlocks.BLUE_OBSIDIAN,
                NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN,
                NetherObsidianBlocks.WEEPING_OBSIDIAN,
                NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN,
                NetherObsidianBlocks.OBSIDIAN_BRICKS,
                NetherObsidianBlocks.OBSIDIAN_BRICKS_STAIRS,
                NetherObsidianBlocks.OBSIDIAN_BRICKS_SLAB,
                NetherObsidianBlocks.OBSIDIAN_TILE,
                NetherObsidianBlocks.OBSIDIAN_TILE_STAIRS,
                NetherObsidianBlocks.OBSIDIAN_TILE_SLAB,
                NetherObsidianBlocks.OBSIDIAN_ROD_TILES,
                NetherObsidianBlocks.OBSIDIAN_GLASS,
                NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS,
                NetherObsidianBlocks.BLUE_OBSIDIAN_TILE,
                NetherObsidianBlocks.BLUE_OBSIDIAN_GLASS
        )) {
            if (!block.defaultBlockState().is(NetherTags.OBSIDIAN_BREAKER_MINEABLE)) {
                failures.add(block.getName().getString()
                        + " is not in #betternether:obsidian_breaker_mineable");
            }
        }

        // Everything that is not obsidian must stay out. Netherrack, nether ores and the glass panes
        // were all in the tag until it was narrowed; keep them out.
        for (Block block : List.of(
                Blocks.STONE,
                Blocks.NETHERRACK,
                Blocks.NETHER_QUARTZ_ORE,
                Blocks.BASALT,
                Blocks.BLACKSTONE,
                NetherObsidianBlocks.OBSIDIAN_GLASS_PANE,
                NetherObsidianBlocks.BLUE_OBSIDIAN_GLASS_PANE
        )) {
            if (block.defaultBlockState().is(NetherTags.OBSIDIAN_BREAKER_MINEABLE)) {
                failures.add(block.getName().getString()
                        + " is in #betternether:obsidian_breaker_mineable - the tag is too wide");
            }
        }

        failIfAny(helper, "Obsidian Breaker tag regression", failures);
        helper.succeed();
    }

    /**
     * The headline behaviour: a Nether Ruby pickaxe (which ships with Obsidian Breaker) must break
     * obsidian faster than a plain diamond pickaxe of comparable tier, and must not speed up a block
     * outside the tag.
     */
    @GameTest
    public void obsidianBreakerSpeedsUpObsidianOnly(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ServerPlayer enchanted = MockPlayers.survival(helper, PLAYER_POS);
        final ServerPlayer plain = MockPlayers.survival(helper, PLAYER_POS);

        equipAndApplyEnchantments(helper, enchanted,
                pickaxe(helper, NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)));
        equipAndApplyEnchantments(helper, plain, pickaxe(helper, Items.DIAMOND_PICKAXE));

        final double attribute = enchanted.getAttributeValue(NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED);
        if (attribute <= 1.0) {
            failures.add("obsidian_block_break_speed was " + attribute
                    + " while holding a nether ruby pickaxe - the enchantment's attribute effect did not apply");
        }

        final BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        final BlockState untagged = Blocks.STONE.defaultBlockState();

        final float enchantedOnObsidian = enchanted.getDestroySpeed(obsidian);
        final float plainOnObsidian = plain.getDestroySpeed(obsidian);
        final float enchantedOnStone = enchanted.getDestroySpeed(untagged);
        final float plainOnStone = plain.getDestroySpeed(untagged);

        if (enchantedOnObsidian <= plainOnObsidian) {
            failures.add("obsidian destroy speed was " + enchantedOnObsidian
                    + " with Obsidian Breaker against " + plainOnObsidian + " without it - no bonus applied");
        }
        // The bonus must be scoped to the tag. Compare each pickaxe against itself so the tiers'
        // different base speeds do not enter into it.
        if (enchantedOnStone / plainOnStone > enchantedOnObsidian / plainOnObsidian) {
            failures.add("the bonus applied to stone at least as strongly as to obsidian"
                    + " (stone ratio " + (enchantedOnStone / plainOnStone)
                    + ", obsidian ratio " + (enchantedOnObsidian / plainOnObsidian) + ")");
        }

        failIfAny(helper, "Obsidian Breaker regression", failures);
        helper.succeed();
    }

    /**
     * Flaming Ruby ships Obsidian Breaker III against Nether Ruby's I, so its obsidian speed must be
     * strictly higher. This is what catches a broken {@code LevelBasedValue.Lookup} in the enchantment
     * definition, which a single-level test would sail straight past.
     */
    @GameTest
    public void higherObsidianBreakerLevelsMineFaster(GameTestHelper helper) {
        final ServerPlayer level1 = MockPlayers.survival(helper, PLAYER_POS);
        final ServerPlayer level3 = MockPlayers.survival(helper, PLAYER_POS);

        equipAndApplyEnchantments(helper, level1,
                pickaxe(helper, NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)));
        equipAndApplyEnchantments(helper, level3,
                pickaxe(helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)));

        final double a1 = level1.getAttributeValue(NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED);
        final double a3 = level3.getAttributeValue(NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED);

        if (a3 <= a1) {
            helper.fail(Component.literal(
                    "Obsidian Breaker III gave obsidian_block_break_speed " + a3
                            + ", no more than level I's " + a1
            ));
            return;
        }
        helper.succeed();
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
