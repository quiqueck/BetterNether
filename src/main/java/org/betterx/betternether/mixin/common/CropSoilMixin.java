package org.betterx.betternether.mixin.common;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets vanilla crops, stems and pitcher pods be planted on BetterNether's farmland (#38). On this
 * version their {@code mayPlaceOn} hardcodes {@code Blocks.FARMLAND}; the {@code #supports_crops} tag the
 * 26.x branches use does not exist yet.
 */
@Mixin({CropBlock.class, StemBlock.class, AttachedStemBlock.class, PitcherCropBlock.class})
public abstract class CropSoilMixin {
    @Inject(method = "mayPlaceOn", at = @At("RETURN"), cancellable = true)
    private void bn_acceptNetherFarmland(
            BlockState floor,
            BlockGetter level,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> info
    ) {
        if (!info.getReturnValueZ() && floor.is(NetherTerrainBlocks.FARMLAND)) {
            info.setReturnValue(true);
        }
    }
}
