package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. Always dropped itself unconditionally via BlockBase's inherited getDrops() override
// (no loot table json was generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion() on
// the registration.
public class BlockGiantMoldSapling extends Block implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 14, 12);

    public BlockGiantMoldSapling(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, level.getBlockState(pos.below()));
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

    @Override
    public boolean isValidBonemealTarget(
            LevelReader world,
            BlockPos pos,
            BlockState state,
            BonemealSource bonemealSource
    ) {
        return true;
    }

    private boolean canGrowRandomly(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return BlocksHelper.isFertile(world.getBlockState(pos.below()))
                ? (random.nextInt(8) == 0)
                : (random.nextInt(16) == 0);
    }

    /**
     * The chance that one application of bone meal grows the giant mold, the same 40% vanilla mushrooms use.
     */
    public static final float BONEMEAL_SUCCESS_CHANCE = 0.4f;

    @Override
    public boolean isBonemealSuccess(
            Level world,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            BonemealSource bonemealSource
    ) {
        // Vanilla's mushroom odds (MushroomBlock): 40% per application, one step to the grown plant.
        // Random ticks keep their own, much slower roll in canGrowRandomly.
        return random.nextFloat() < BONEMEAL_SUCCESS_CHANCE;
    }

    private void grow(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        NetherTrees.GIANT_MOLD.placeInWorld(WorldState.registryAccess(), world, pos, random);
    }

    @Override
    public void performBonemeal(
            ServerLevel world,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            BonemealSource bonemealSource
    ) {
        grow(world, random, pos, state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (canGrowRandomly(world, random, pos, state))
            grow(world, random, pos, state);
    }
}
