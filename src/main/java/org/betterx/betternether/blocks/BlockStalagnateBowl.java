package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.BNBlockProperties.FoodShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import java.util.Collections;
import java.util.List;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone; noOcclusion() moves to the registration site (StalagnateMaterial's BOWL slot), which
// is also where NetherLoot.stalagnateBowl() now says what this drops - one pool per FOOD value, replacing
// the getDrops override this class used to carry.
public class BlockStalagnateBowl extends Block {
    private static final VoxelShape SHAPE = box(5, 0, 5, 11, 3, 11);
    public static final EnumProperty<FoodShape> FOOD = BNBlockProperties.FOOD;

    public BlockStalagnateBowl(Block source) {
        super(BlockBehaviour.Properties.ofFullCopy(source));
        this.registerDefaultState(getStateDefinition().any().setValue(FOOD, FoodShape.NONE));
    }

    public BlockStalagnateBowl(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(FOOD, FoodShape.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FOOD);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos down = pos.below();
        return world.getBlockState(down).isFaceSturdy(world, down, Direction.UP);
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
