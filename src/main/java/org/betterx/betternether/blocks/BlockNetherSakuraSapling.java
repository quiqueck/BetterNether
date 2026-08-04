package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockNetherSakuraSapling extends FeatureSaplingBlock implements BonemealableBlock {
    public BlockNetherSakuraSapling(BlockBehaviour.Properties properties) {
        super(properties, (level, pos, state, rnd) -> NetherTrees.SAKURA_TREE
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd),
                true
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }
//
//	@Override
//	protected Feature<?> getFeature() {
//		return FEATURE;
//	}
}
