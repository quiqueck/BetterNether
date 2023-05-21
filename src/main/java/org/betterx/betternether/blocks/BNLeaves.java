package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseLeavesBlock;
import org.betterx.bclib.complexmaterials.BehaviourBuilders;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BNLeaves extends BaseLeavesBlock {
    public BNLeaves(Block sapling, MapColor color) {
        this(sapling, BehaviourBuilders.createLeaves(color, false).noOcclusion());
    }


    public BNLeaves(Block sapling, BlockBehaviour.Properties properties) {
        super(sapling, properties);
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
