package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockWillowTorch;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * The willow torch must behave like a vanilla torch when water reaches it: the water flows in, the torch
 * breaks and drops itself (quiqueck/BetterNether#55). The vanilla torch runs through the same setup as the
 * control. Also pins the properties the wover-sets rewrite had dropped (light 15, no collision).
 */
public class WillowTorchFluidGameTest {
    private static final BlockPos WILLOW = new BlockPos(2, 2, 2);
    private static final BlockPos VANILLA = new BlockPos(2, 2, 5);

    @GameTest
    public void willowTorchHasTorchProperties(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            final BlockState state = torch().setValue(BlockWillowTorch.FACING, facing);
            if (state.blocksMotion()) failures.add("facing " + facing + " blocks motion, so fluids cannot flow into it");
            if (!state.canBeReplaced(Fluids.WATER)) failures.add("facing " + facing + " cannot be replaced by water");
            if (state.getLightEmission() != 15) failures.add("facing " + facing + " emits light " + state.getLightEmission() + ", expected 15");
            if (!state.getCollisionShape(helper.getLevel(), helper.absolutePos(WILLOW)).isEmpty()) {
                failures.add("facing " + facing + " has a collision shape");
            }
        }
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal("willow torch regression:\n - " + String.join("\n - ", failures)));
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void flowingWaterBreaksWillowTorch(GameTestHelper helper) {
        // Wall-mounted: those are the full-height states that counted as solid (the standing and hanging
        // shapes are too small to).
        place(helper, WILLOW, torch().setValue(BlockWillowTorch.FACING, Direction.NORTH));
        place(helper, VANILLA, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
        if (!helper.getBlockState(WILLOW).is(NetherWoodBlocks.MAT_WILLOW.getTorch())) {
            throw helper.assertionException(Component.literal("setup: willow torch did not stay on its wall"));
        }
        helper.setBlock(WILLOW.east(), Blocks.WATER.defaultBlockState());
        helper.setBlock(VANILLA.east(), Blocks.WATER.defaultBlockState());

        helper.succeedWhen(() -> {
            if (helper.getBlockState(VANILLA).is(Blocks.WALL_TORCH)) {
                throw helper.assertionException(Component.literal("control: water did not break the vanilla torch"));
            }
            if (helper.getBlockState(WILLOW).is(NetherWoodBlocks.MAT_WILLOW.getTorch())) {
                throw helper.assertionException(Component.literal(
                        "water flowed next to the willow torch but did not break it (now " + helper.getBlockState(WILLOW) + ")"
                ));
            }
            if (!helper.getBlockState(WILLOW).getFluidState().is(Fluids.FLOWING_WATER)) {
                throw helper.assertionException(Component.literal("willow torch is gone but water did not take its place"));
            }
            helper.assertItemEntityPresent(NetherWoodBlocks.MAT_WILLOW.getTorch().asItem(), WILLOW, 3.0);
        });
    }

    private static BlockState torch() {
        return NetherWoodBlocks.MAT_WILLOW.getTorch().defaultBlockState();
    }

    /**
     * Mounts {@code state} on a stone wall to its south, over a stone floor. The floor must cover the whole arena: water only spreads towards
     * the nearest drop-off when one is in reach, so a floor with holes next to the source would lead it away
     * from the torch.
     */
    private static void place(GameTestHelper helper, BlockPos rel, BlockState state) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(new BlockPos(x, rel.getY() - 1, z), Blocks.STONE.defaultBlockState());
            }
        }
        helper.setBlock(rel.south(), Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlock(helper.absolutePos(rel), state, Block.UPDATE_ALL);
    }
}
