package org.betterx.betternether.blocks;

import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import com.mojang.serialization.MapCodec;
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
    public static final MapCodec<RubeusLog> CODEC = simpleCodec(RubeusLog::new);
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public RubeusLog(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                                      .setValue(AXIS, Direction.Axis.Y)
                                      .setValue(SHAPE, TripleShape.BOTTOM));
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(SHAPE);
    }
}
