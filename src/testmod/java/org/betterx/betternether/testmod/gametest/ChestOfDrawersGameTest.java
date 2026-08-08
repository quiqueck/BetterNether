package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blockentities.BlockEntityChestOfDrawers;
import org.betterx.betternether.blocks.BlockChestOfDrawers;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code chest_of_drawers}'s OPEN blockstate (a watcher-counter animation like vanilla chests,
 * per {@code BlockEntityChestOfDrawers}'s own class notes) and its item storage.
 */
public class ChestOfDrawersGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void openStateTracksWatchersAndItemsRoundTrip(GameTestHelper helper) {
        helper.setBlock(POS, NetherFurnitureBlocks.CHEST_OF_DRAWERS);
        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(POS));
        final List<String> failures = new ArrayList<>();

        if (!(be instanceof BlockEntityChestOfDrawers chest)) {
            throw helper.assertionException(Component.literal(
                    "chest_of_drawers did not create a BlockEntityChestOfDrawers"
            ));
        }

        if (helper.getBlockState(POS).getValue(BlockChestOfDrawers.OPEN)) {
            failures.add("OPEN is true before anyone opened the chest");
        }

        final ServerPlayer player = MockPlayers.survival(helper, POS.above());
        chest.onInvOpen(player);
        if (!helper.getBlockState(POS).getValue(BlockChestOfDrawers.OPEN)) {
            failures.add("OPEN did not become true after onInvOpen");
        }

        chest.stopOpen(player);
        if (helper.getBlockState(POS).getValue(BlockChestOfDrawers.OPEN)) {
            failures.add("OPEN did not return to false after stopOpen");
        }

        chest.setItem(0, new ItemStack(Items.NETHERITE_INGOT, 3));
        final ItemStack roundTripped = chest.getItem(0);
        if (!roundTripped.is(Items.NETHERITE_INGOT) || roundTripped.getCount() != 3) {
            failures.add("item storage did not round-trip, got " + roundTripped);
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "chest_of_drawers regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
