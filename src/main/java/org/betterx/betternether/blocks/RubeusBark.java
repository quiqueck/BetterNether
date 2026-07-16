package org.betterx.betternether.blocks;

import org.betterx.wover.block.api.BlockProperties.TripleShape;

import com.mojang.serialization.MapCodec;
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
    public static final MapCodec<RubeusBark> CODEC = simpleCodec(RubeusBark::new);

    public RubeusBark(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                                      .setValue(AXIS, Direction.Axis.Y)
                                      .setValue(RubeusLog.SHAPE, TripleShape.BOTTOM));
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(RubeusLog.SHAPE);
    }
}
