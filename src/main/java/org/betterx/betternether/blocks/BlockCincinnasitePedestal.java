package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCincinnasitePedestal extends BlockBaseNotFull implements BehaviourMetal {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);

    public BlockCincinnasitePedestal() {
        super(Properties.ofFullCopy(NetherBlocks.CINCINNASITE_BLOCK).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }
}
