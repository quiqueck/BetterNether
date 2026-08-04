package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.registry.features.configured.NetherVines;
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
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. Always dropped itself unconditionally via BlockBase's inherited getDrops() override
// (no loot table json was generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion() on
// the registration.
public class BlockEyeSeed extends Block implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(4, 6, 4, 12, 16, 12);

    public BlockEyeSeed(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextInt(4) == 0 && world.getBlockState(pos.below()).getBlock() == Blocks.AIR;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        NetherVines.EYE_VINE.placeInWorld(WorldState.registryAccess(), level, pos, random);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, level.getBlockState(pos.above()));
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
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (isBonemealSuccess(world, random, pos, state)) {
            performBonemeal(world, random, pos, state);
        }
    }
}
