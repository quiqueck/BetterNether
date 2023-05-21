package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BlockProperties;
import org.betterx.bclib.blocks.BlockProperties.TripleShape;
import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWillowTrunk extends BlockBaseNotFull implements AddMineableAxe {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final VoxelShape SHAPE_BOTTOM = box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_TOP = box(4, 0, 4, 12, 12, 12);

    public BlockWillowTrunk() {
        super(Materials.makeNetherWood(MapColor.TERRACOTTA_RED).noOcclusion());
        this.setDropItself(false);
        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, TripleShape.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }
}
