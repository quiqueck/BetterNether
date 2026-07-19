package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BNLeaves extends TintedParticleLeavesBlock {
    public BNLeaves(Block sapling, MapColor color) {
        this(sapling, Materials.staticLeaves(color, false).noOcclusion());
    }


    public BNLeaves(Block sapling, BlockBehaviour.Properties properties) {
        // sapling no longer tracked by the vanilla leaves block in 1.21.6
        super(0.01F, properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return false;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
    }
}
