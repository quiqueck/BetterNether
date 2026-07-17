package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCincinnasitePedestal extends BlockBaseNotFull {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);

    public BlockCincinnasitePedestal(Properties settings) {
        super(settings.noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }
}
