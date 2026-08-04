package org.betterx.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. Always dropped itself unconditionally via BlockBase's inherited getDrops() override
// (no loot table json was generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion() on
// its RubeusMaterial CONE slot.
public class BlockRubeusCone extends Block {
    private static final VoxelShape SHAPE = box(3, 3, 3, 13, 16, 13);

    // The no-arg overload (building a fresh, id-less BlockBehaviour.Properties.of() via
    // Materials.makeNetherWood(...)) was dead code - nothing in src called it, only the (Properties)
    // overload below is ever used at registration (see complex/RubeusMaterial.java). Removed rather than
    // ported (WP3.8).
    public BlockRubeusCone(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.above()).isFaceSturdy(world, pos.above(), Direction.DOWN);
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
        if (canSurvive(state, world, pos))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }
}