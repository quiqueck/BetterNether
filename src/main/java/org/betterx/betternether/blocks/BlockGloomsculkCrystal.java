package org.betterx.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;

/**
 * A clutch of crystal shards grown out of a geode - sculk at the root, lava at the tip.
 * <p>
 * Attaches to any face, so it studs the biome's floor and ceiling alike. Extends {@link Block} and
 * borrows {@link DirectionalBlock#FACING} rather than extending {@code DirectionalBlock} itself, which
 * would drag in an abstract {@code codec()} for no gain - the same shortcut {@link BlockLucisSpore}
 * takes.
 */
public class BlockGloomsculkCrystal extends Block {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    /** Shards stand off their surface by 5 on each side and reach 12 out of it. */
    private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.UP, box(5, 0, 5, 11, 12, 11));
        SHAPES.put(Direction.DOWN, box(5, 4, 5, 11, 16, 11));
        SHAPES.put(Direction.NORTH, box(5, 5, 4, 11, 11, 16));
        SHAPES.put(Direction.SOUTH, box(5, 5, 0, 11, 11, 12));
        SHAPES.put(Direction.WEST, box(4, 5, 5, 16, 11, 11));
        SHAPES.put(Direction.EAST, box(0, 5, 5, 12, 11, 11));
    }

    public BlockGloomsculkCrystal(Properties settings) {
        super(settings);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getClickedFace());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        final Direction attached = state.getValue(FACING).getOpposite();
        final BlockPos anchor = pos.relative(attached);
        return world.getBlockState(anchor).isFaceSturdy(world, anchor, attached.getOpposite());
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
            RandomSource random
    ) {
        return canSurvive(state, world, pos) ? state : Blocks.AIR.defaultBlockState();
    }
}
