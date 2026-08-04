package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.features.configured.NetherTrees;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;

import com.google.common.collect.Maps;

import java.util.EnumMap;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. The block always dropped itself unconditionally via BlockBase's inherited getDrops()
// override, which shadowed the already-wired-up BlockTraits.LOOT_TABLE.dropSelf() trait (AbstractSeed's
// addSlotSpecificDefinitions) that produced the committed wart_seed.json - that trait now becomes live and
// reproduces the same table, so no loot change is needed here.
public class BlockWartSeed extends Block implements BonemealableBlock {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    static {
        BOUNDING_SHAPES.put(Direction.UP, Shapes.box(0.25, 0.0, 0.25, 0.75, 0.5, 0.75));
        BOUNDING_SHAPES.put(Direction.DOWN, Shapes.box(0.25, 0.5, 0.25, 0.75, 1.0, 0.75));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.25, 0.25, 0.5, 0.75, 0.75, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.25, 0.25, 0.0, 0.75, 0.75, 0.5));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.5, 0.25, 0.25, 1.0, 0.75, 0.75));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.25, 0.25, 0.5, 0.75, 0.75));
    }

    public BlockWartSeed(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.UP));
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState();
        LevelReader worldView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction[] directions = ctx.getNearestLookingDirections();
        for (int i = 0; i < directions.length; ++i) {
            Direction direction = directions[i];
            Direction direction2 = direction.getOpposite();
            blockState = blockState.setValue(FACING, direction2);
            if (blockState.canSurvive(worldView, blockPos)) {
                return blockState;
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return canSupportCenter(world, blockPos, direction) || SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        return direction == Direction.UP && BlocksHelper.isSoulSand(world.getBlockState(pos.below()));
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextInt(8) == 0;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        NetherTrees.WART_TREE.placeInWorld(WorldState.registryAccess(), world, pos, random);
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
        if (!canSurvive(state, world, pos))
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }
}