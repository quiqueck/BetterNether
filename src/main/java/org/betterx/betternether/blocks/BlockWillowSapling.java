package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.interfaces.SurvivesOnNetherGround;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.features.configured.NetherTrees;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;


public class BlockWillowSapling extends FeatureSaplingBlock implements BonemealableBlock, SurvivesOnNetherGround, BehaviourSapling {
    public BlockWillowSapling() {
        super((BlockState state) -> NetherTrees.WILLOW_TREE);
    }

    /**
     * Four willow saplings in a 2x2 grow the giant (old) willow instead of four ordinary ones, unless
     * {@link NetherGameRules#GROW_LARGE_WILLOWS} is off. A 2x2 without room for the giant leaves the
     * saplings standing rather than growing a small willow in the middle of the arrangement.
     */
    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) != 0
                && level.getGameRules().getBoolean(NetherGameRules.GROW_LARGE_WILLOWS)) {
            final GiantSaplingGrowth.Result result = GiantSaplingGrowth.tryGrow(
                    level, pos, state, NetherTrees.OLD_WILLOW_TREE, s -> s.is(this), random
            );
            if (result != GiantSaplingGrowth.Result.NO_QUAD) return;
        }
        super.advanceTree(level, pos, state, random);
    }

    @Override
    public boolean isBonemealSuccess(
            Level world,
            @NotNull RandomSource random,
            BlockPos pos,
            @NotNull BlockState state
    ) {
        return (BlocksHelper.isFertile(world.getBlockState(pos.below()))
                ? (random.nextInt(8) == 0)
                : (random.nextInt(16) == 0));
    }

    @Override
    protected boolean mayPlaceOn(
            @NotNull BlockState blockState,
            @NotNull BlockGetter blockGetter,
            @NotNull BlockPos blockPos
    ) {
        return isSurvivable(blockState);
    }
}