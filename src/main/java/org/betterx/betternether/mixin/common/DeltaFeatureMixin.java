package org.betterx.betternether.mixin.common;

import org.betterx.betternether.blocks.BlockRubeusLeaves;
import org.betterx.betternether.blocks.BlockWillowLeaves;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.3 collapsed {@code ConfiguredFeature} into {@code Feature}: {@link DeltaFeature} is now a record that
 * carries its own configuration, so {@code isClear} lost its {@code DeltaFeatureConfiguration} parameter
 * <em>and</em> stopped being static. Verified with
 * {@code javap -p net.minecraft.world.level.levelgen.feature.DeltaFeature}:
 * {@code private boolean isClear(LevelAccessor, BlockPos)}. The injector therefore has to be an instance
 * method with the matching two-argument signature - this build has no refmap and no mixin annotation
 * processor, so a wrong descriptor would compile and silently fail to apply.
 */
@Mixin(DeltaFeature.class)
public class DeltaFeatureMixin {
    @Inject(method = "isClear", at = @At("HEAD"), cancellable = true)
    private void checkBlock(
            LevelAccessor worldAccess,
            BlockPos blockPos,
            CallbackInfoReturnable<Boolean> info
    ) {
        BlockState blockState = worldAccess.getBlockState(blockPos);
        if (!bn_isValidBlock(blockState)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Unique
    private static boolean bn_isValidBlock(BlockState state) {
        return !(state.getBlock() instanceof BlockRubeusLeaves)
                && !(state.getBlock() instanceof BlockWillowLeaves)
                && !(state.getFluidState().isEmpty()
                && state.canBeReplaced());
    }
}
