package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class BlockMushroomFirSapling extends FeatureSaplingBlock implements BonemealableBlock {

    public BlockMushroomFirSapling(BlockBehaviour.Properties properties) {
        super(properties, (level, pos, state, rnd) -> NetherTrees.MUSHROOM_FIR
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd)
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }

    /**
     * Grows faster on its own on fertile soil. Bone meal is not affected; it takes at the vanilla sapling's
     * rate inherited from {@link FeatureSaplingBlock#isBonemealSuccess}.
     */
    @Override
    protected boolean isRandomGrowthSuccess(
            @NotNull Level world,
            @NotNull RandomSource random,
            @NotNull BlockPos pos,
            @NotNull BlockState state
    ) {
        return BlocksHelper.isFertile(world.getBlockState(pos.below()))
                ? (random.nextInt(8) == 0)
                : (random.nextInt(16) == 0);
    }
}