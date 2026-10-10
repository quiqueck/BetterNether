package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Cincinnasite shears must take Efficiency like vanilla shears, and Efficiency must actually make them
 * faster (quiqueck/BetterNether#181).
 * <p>
 * Efficiency only adds its {@code mining_efficiency} bonus when the tool's base speed on the block is
 * above 1 ({@code Player#getDestroySpeed}), so being enchantable is not enough: the shears also need
 * vanilla's shear speed rules for leaves, wool, vines and cobweb. Every check runs vanilla shears
 * through the same harness as the control.
 */
public class ShearsEfficiencyGameTest {
    private static final BlockPos PLAYER_POS = new BlockPos(1, 2, 1);
    private static final List<Block> SHEAR_BLOCKS = List.of(
            Blocks.VINE, Blocks.OAK_LEAVES, Blocks.WHITE_WOOL, Blocks.COBWEB, Blocks.GLOW_LICHEN
    );

    private static Item cincinnasiteShears() {
        return NetherEquipmentItems.CINCINNASITE_SET.get(ToolSlot.SHEARS_SLOT);
    }

    private static Holder<Enchantment> efficiency(GameTestHelper helper) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
    }

    @GameTest
    public void efficiencyAppliesViaAnvil(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final Holder<Enchantment> efficiency = efficiency(helper);

        for (Item shears : List.of(Items.SHEARS, cincinnasiteShears())) {
            final String name = BuiltInRegistries.ITEM.getKey(shears).toString();
            if (!efficiency.value().canEnchant(new ItemStack(shears))) {
                failures.add("Efficiency cannot enchant " + name + " (not in #minecraft:enchantable/mining)");
            }

            final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
            player.experienceLevel = 100;
            final AnvilMenu menu = new AnvilMenu(
                    0, player.getInventory(),
                    ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(PLAYER_POS))
            );
            menu.getSlot(AnvilMenu.INPUT_SLOT).set(new ItemStack(shears));
            menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(EnchantmentHelper.createBook(new EnchantmentInstance(efficiency, 5)));
            menu.createResult();
            final ItemStack result = menu.getSlot(AnvilMenu.RESULT_SLOT).getItem();
            if (result.isEmpty() || result.getEnchantments().getLevel(efficiency) != 5) {
                failures.add("anvil did not put Efficiency V on " + name + " (result: " + result + ")");
            }
        }

        failIfAny(helper, "Shears enchanting regression", failures);
        helper.succeed();
    }

    @GameTest
    public void efficiencyMakesShearsFaster(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final Holder<Enchantment> efficiency = efficiency(helper);

        for (Item shears : List.of(Items.SHEARS, cincinnasiteShears())) {
            final String name = BuiltInRegistries.ITEM.getKey(shears).toString();
            final ServerPlayer plain = MockPlayers.survival(helper, PLAYER_POS);
            final ServerPlayer enchanted = MockPlayers.survival(helper, PLAYER_POS);
            final ServerPlayer hand = MockPlayers.survival(helper, PLAYER_POS);

            final ItemStack enchantedStack = new ItemStack(shears);
            enchantedStack.enchant(efficiency, 5);
            equip(plain, new ItemStack(shears));
            equip(enchanted, enchantedStack);

            for (Block block : SHEAR_BLOCKS) {
                final var state = block.defaultBlockState();
                final float handSpeed = hand.getDestroySpeed(state);
                final float plainSpeed = plain.getDestroySpeed(state);
                final float enchantedSpeed = enchanted.getDestroySpeed(state);
                final float vanillaSpeed = new ItemStack(Items.SHEARS).getDestroySpeed(state);
                if (plainSpeed <= handSpeed) {
                    failures.add(name + " mine " + block.getName().getString() + " no faster than a bare hand ("
                            + plainSpeed + " vs " + handSpeed + ")");
                }
                if (new ItemStack(shears).getDestroySpeed(state) < vanillaSpeed) {
                    failures.add(name + " mine " + block.getName().getString() + " slower than vanilla shears");
                }
                if (enchantedSpeed <= plainSpeed) {
                    failures.add("Efficiency V does not speed up " + name + " on " + block.getName().getString()
                            + " (" + enchantedSpeed + " vs " + plainSpeed + ")");
                }
            }
        }

        failIfAny(helper, "Shears efficiency regression", failures);
        helper.succeed();
    }

    /** Mock players never run detectEquipmentUpdates; apply the enchantment attribute effects by hand. */
    private static void equip(ServerPlayer player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            final AttributeInstance instance = player.getAttributes().getInstance(attribute);
            if (instance != null) {
                instance.removeModifier(modifier.id());
                instance.addTransientModifier(modifier);
            }
        });
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
