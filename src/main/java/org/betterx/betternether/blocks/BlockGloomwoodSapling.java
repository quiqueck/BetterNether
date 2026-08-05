package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.betternether.world.features.GloomwoodTreeFeature;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockGloomwoodSapling extends FeatureSaplingBlock implements BonemealableBlock {
    public BlockGloomwoodSapling(BlockBehaviour.Properties properties) {
        super(properties, (level, pos, state, rnd) -> NetherTrees.GLOOMWOOD_TREE
                .placeInWorld(WorldState.registryAccess(), level, pos, rnd)
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }

    /**
     * Refuses bone meal where no gloomwood could stand.
     * <p>
     * {@link FeatureSaplingBlock} accepts it at any server position: it grows a feature rather than a
     * vanilla tree, so it has no {@code TreeGrower} to ask for a minimum height and the check 26.1's
     * {@code SaplingBlock} makes had to go. The gloomwood does have an answer - the smallest of its
     * variants still needs {@link GloomwoodTreeFeature#MIN_CLEARANCE} blocks overhead - and without
     * asking it, a sapling under a low sculk ceiling swallows bone meal indefinitely and never grows.
     * That is most of its own biome, so this is the common case rather than a corner of it.
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return super.isValidBonemealTarget(level, pos, state)
                && GloomwoodTreeFeature.hasRoomToGrow(level, pos);
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return BlocksHelper.isFertile(world.getBlockState(pos.below()))
                ? (random.nextInt(8) == 0)
                : (random.nextInt(16) == 0);
    }
}
