package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.bclib.blocks.FeatureHangingSaplingBlock;
import org.betterx.betternether.interfaces.SurvivesOnNetherrack;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockNetherSakuraSapling extends FeatureHangingSaplingBlock implements BonemealableBlock, SurvivesOnNetherrack, BehaviourSapling {
    public BlockNetherSakuraSapling() {
        super((level, pos, state, rnd) -> NetherTrees.SAKURA_TREE
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd)
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return isSurvivable(blockState);
    }
//
//	@Override
//	protected Feature<?> getFeature() {
//		return FEATURE;
//	}
}
