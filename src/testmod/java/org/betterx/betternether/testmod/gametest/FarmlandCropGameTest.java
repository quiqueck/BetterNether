package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Vanilla seeds must be plantable on BetterNether's farmland just like on vanilla farmland
 * (quiqueck/BetterNether#38). Vanilla farmland runs through the identical player interaction as the control.
 */
public class FarmlandCropGameTest {
    private static final List<Item> SEEDS = List.of(
            Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.CARROT, Items.POTATO,
            Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD
    );

    @GameTest
    public void vanillaSeedsCanBePlantedOnNetherFarmland(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, new BlockPos(0, 2, 0));
        final List<String> failures = new ArrayList<>();
        for (int i = 0; i < SEEDS.size(); i++) {
            final Item seed = SEEDS.get(i);
            check(helper, player, seed, Blocks.FARMLAND, new BlockPos(1 + (i % 4), 1, 3 + (i / 4) * 4), failures, "control: ");
            check(helper, player, seed, NetherTerrainBlocks.FARMLAND, new BlockPos(1 + (i % 4), 1, 1 + (i / 4) * 4), failures, "");
        }
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal("farmland crop regression:\n - " + String.join("\n - ", failures)));
        }
        helper.succeed();
    }

    private static void check(
            GameTestHelper helper, ServerPlayer player, Item seed, Block soil, BlockPos soilRel,
            List<String> failures, String prefix
    ) {
        final BlockPos abs = helper.absolutePos(soilRel);
        helper.getLevel().setBlock(abs, soil.defaultBlockState(), Block.UPDATE_ALL);
        helper.getLevel().setBlock(abs.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        final ItemStack stack = new ItemStack(seed);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(
                player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.upFromBottomCenterOf(abs, 1.0), Direction.UP, abs, false)
        ));
        if (helper.getLevel().getBlockState(abs.above()).isAir()) {
            failures.add(prefix + BuiltInRegistries.ITEM.getKey(seed) + " could not be planted on "
                    + BuiltInRegistries.BLOCK.getKey(soil));
        }
    }
}
