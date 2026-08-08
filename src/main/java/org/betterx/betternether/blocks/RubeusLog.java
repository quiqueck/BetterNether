package org.betterx.betternether.blocks;

import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * The Rubeus log: a rotated pillar with a bottom/middle/top {@link TripleShape} visual variant.
 * Stripping is provided by the {@code LOG_BLOCK} trait of the slot that registers it (bclib's
 * {@code BaseStripableLogBlock} was removed in 21.7).
 */
public class RubeusLog extends RotatedPillarBlock {
    // 26.3 removed block codecs entirely: Block#codec()/BlockBehaviour#simpleCodec no longer
    // exist, because blocks are no longer described by a dispatch codec. The CODEC constant and
    // the codec() override are dropped, not replaced.

    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public RubeusLog(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                                      .setValue(AXIS, Direction.Axis.Y)
                                      .setValue(SHAPE, TripleShape.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(SHAPE);
    }
}
