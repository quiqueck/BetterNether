package org.betterx.betternether.mixin.common;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MushroomBlock.class)
public abstract class MushroomMixin {

    @Inject(method = "canSurvive", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void canStay(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (BlocksHelper.isNetherMycelium(world.getBlockState(pos.below())))
            info.setReturnValue(true);
    }

    /**
     * 26.3 added a trailing {@link BonemealSource} to all three {@code BonemealableBlock} methods, so the
     * injector has to carry it too. Verified with {@code javap -p net.minecraft.world.level.block.MushroomBlock}:
     * {@code public void performBonemeal(ServerLevel, RandomSource, BlockPos, BlockState, BonemealSource)}.
     * This build has no refmap and no mixin annotation processor, and {@code performBonemeal} is not
     * overloaded, so the old four-argument descriptor compiled but would have failed to apply at
     * mixin-apply time (single target => {@code CallbackInjector} throws instead of skipping).
     */
    @Inject(method = "performBonemeal", at = @At(value = "HEAD"), cancellable = true)
    private void growStructure(
            ServerLevel world,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            BonemealSource bonemealSource,
            CallbackInfo info
    ) {
        if (BlocksHelper.isNetherMycelium(world.getBlockState(pos.below()))) {
            if (state.getBlock() == Blocks.RED_MUSHROOM) {
                NetherTrees.PATCH_BIG_RED_MUSHROOM.placeInWorld(WorldState.registryAccess(), world, pos, random);
                info.cancel();
            } else if (state.getBlock() == Blocks.BROWN_MUSHROOM) {
                NetherTrees.PATCH_BIG_BROWN_MUSHROOM.placeInWorld(WorldState.registryAccess(), world, pos, random);
                info.cancel();
            }
        }
    }
}
