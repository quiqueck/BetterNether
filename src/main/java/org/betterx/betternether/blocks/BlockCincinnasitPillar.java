package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.betternether.blocks.BNBlockProperties.CincinnasitPillarShape;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;

public class BlockCincinnasitPillar extends BlockBase implements BehaviourMetal {
    public static final EnumProperty<CincinnasitPillarShape> SHAPE = BNBlockProperties.PILLAR_SHAPE;

    public BlockCincinnasitPillar() {
        super(Properties.ofFullCopy(NetherBlocks.CINCINNASITE_BLOCK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
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
        boolean top = world.getBlockState(pos.above()).getBlock() == this;
        boolean bottom = world.getBlockState(pos.below()).getBlock() == this;
        if (top && bottom)
            return state.setValue(SHAPE, CincinnasitPillarShape.MIDDLE);
        else if (top)
            return state.setValue(SHAPE, CincinnasitPillarShape.BOTTOM);
        else if (bottom)
            return state.setValue(SHAPE, CincinnasitPillarShape.TOP);
        else
            return state.setValue(SHAPE, CincinnasitPillarShape.SMALL);
    }
}