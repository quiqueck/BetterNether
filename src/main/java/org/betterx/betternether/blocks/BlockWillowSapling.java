package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;


public class BlockWillowSapling extends FeatureSaplingBlock implements BonemealableBlock {
    public BlockWillowSapling(BlockBehaviour.Properties properties) {
        super(properties,
                (level, pos, state, rnd) -> NetherTrees.WILLOW_TREE
                        .placeInWorld(WorldState.registryAccess(), level, pos, rnd),
                (level, pos, state, rnd) -> NetherTrees.OLD_WILLOW_TREE
                        .placeInWorld(WorldState.registryAccess(), level, pos, rnd),
                false
        );
    }

    @Override
    protected boolean megaFeatureEnabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return level.getGameRules().get(NetherGameRules.GROW_LARGE_WILLOWS);
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
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }
}