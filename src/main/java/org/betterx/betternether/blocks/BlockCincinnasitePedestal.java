package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Reparented off BlockBaseNotFull (WP6.12): its canSuffocate/isSimpleFullBlock/allowsSpawning overrides
// were dead code (no such Block/BlockState methods exist in current mappings - verified via javap against
// the mapped Block/BlockBehaviour.Properties/BlockState classes). noOcclusion() moves to the registration
// site (NetherFunctionalBlocks.CINCINNASITE_PEDESTAL); the block always dropped itself unconditionally via
// BlockBase's inherited getDrops() override (no loot table json was generated for it), reproduced
// explicitly as NetherLoot.dropSelfNoExplosion().
public class BlockCincinnasitePedestal extends Block {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);

    public BlockCincinnasitePedestal(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }
}
