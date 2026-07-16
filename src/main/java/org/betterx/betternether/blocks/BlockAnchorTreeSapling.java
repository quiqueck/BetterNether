package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.bclib.blocks.FeatureHangingSaplingBlock;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAnchorTreeSapling extends FeatureHangingSaplingBlock implements BonemealableBlock, BehaviourSapling {
    public BlockAnchorTreeSapling() {
        super((level, pos, state, rnd) -> NetherTrees.ANCHOR_TREE_BRANCH
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd)
        );
    }

    public BlockAnchorTreeSapling(BlockBehaviour.Properties properties) {
        super(properties, (level, pos, state, rnd) -> NetherTrees.ANCHOR_TREE_BRANCH
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd)
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }
}
