package org.betterx.betternether.blocks;

import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * The Rubeus bark (all-sides log) with a bottom/middle/top {@link TripleShape} visual variant.
 * bclib's {@code BaseStripableBarkBlock} was removed in 21.7, so this now extends vanilla
 * {@link RotatedPillarBlock} directly and reuses {@link RubeusLog#SHAPE}.
 */
public class RubeusBark extends RotatedPillarBlock {
    // 26.3 removed block codecs entirely: Block#codec()/BlockBehaviour#simpleCodec no longer
    // exist, because blocks are no longer described by a dispatch codec. The CODEC constant and
    // the codec() override are dropped, not replaced.

    public RubeusBark(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                                      .setValue(AXIS, Direction.Axis.Y)
                                      .setValue(RubeusLog.SHAPE, TripleShape.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(RubeusLog.SHAPE);
    }
}
