package org.betterx.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;

public class BlockSoulSandstone extends Block {
    public static final BooleanProperty UP = BooleanProperty.create("up");

    public BlockSoulSandstone(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(UP);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        return state.setValue(UP, world.getBlockState(pos.above()).getBlock() != this);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                   .setValue(UP, ctx.getLevel().getBlockState(ctx.getClickedPos().above()).getBlock() != this);
    }
}