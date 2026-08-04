package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BlockProperties;
import org.betterx.betternether.BlocksHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase. Its eight subclasses (BlockEggPlant, BlockFeatherFern, BlockInkBush, BlockMagmaFlower,
// BlockOrangeMushroom, BlockBarrelCactus, BlockBlackApple, BlockAgave) inherit the new parent unchanged.
public abstract class BlockCommonPlant extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockProperties.AGE;

    public BlockCommonPlant(Properties settings) {
        super(settings);
    }

    public int getMaxAge() {
        return 3;
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(AGE);
    }

    @Override
    public abstract boolean canSurvive(BlockState state, LevelReader world, BlockPos pos);

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

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3)
            return BlocksHelper.isFertile(world.getBlockState(pos.below()))
                    ? (random.nextBoolean())
                    : (random.nextInt(4) == 0);
        else
            return false;
    }

    protected boolean canGrowTerrain(Level world, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3)
            return BlocksHelper.isFertile(world.getBlockState(pos.below()))
                    ? (random.nextInt(8) == 0)
                    : (random.nextInt(16) == 0);
        else
            return false;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (canGrowTerrain(world, random, pos, state))
            performBonemeal(world, random, pos, state);
    }
}