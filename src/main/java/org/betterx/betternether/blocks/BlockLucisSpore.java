package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.EnumMap;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. Always dropped itself unconditionally via BlockBase's inherited getDrops() override
// (no loot table json was generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion() on
// the registration.
public class BlockLucisSpore extends Block implements BonemealableBlock {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, box(4, 4, 8, 12, 12, 16),
            Direction.SOUTH, box(4, 4, 0, 12, 12, 8),
            Direction.WEST, box(8, 4, 4, 16, 12, 12),
            Direction.EAST, box(0, 4, 4, 8, 12, 12)
    ));
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public BlockLucisSpore(Properties settings) {
        super(settings);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextInt(16) == 0;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        NetherVegetation.WALL_LUCIS.placeInWorld(WorldState.registryAccess(), world, pos, random);
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

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (isBonemealSuccess(world, random, pos, state)) {
            performBonemeal(world, random, pos, state);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return BlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return BlocksHelper.mirrorHorizontal(state, mirror, FACING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return BlocksHelper.isNetherrack(blockState) || NetherWoodBlocks.MAT_ANCHOR_TREE.isTreeLog(blockState.getBlock());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState();
        LevelReader worldView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction[] directions = ctx.getNearestLookingDirections();
        for (int i = 0; i < directions.length; ++i) {
            Direction direction = directions[i];
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.setValue(FACING, direction2);
                if (blockState.canSurvive(worldView, blockPos)) {
                    return blockState;
                }
            }
        }
        return null;
    }
}