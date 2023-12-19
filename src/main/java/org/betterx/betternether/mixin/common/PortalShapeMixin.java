package org.betterx.betternether.mixin.common;

import org.betterx.betternether.portals.BNPortalShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.portal.PortalShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalShape.class)
public class PortalShapeMixin {
    @Unique
    private BNPortalShape bn_shape;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Object;<init>()V", shift = At.Shift.AFTER))
    private void bn_init(LevelAccessor levelAccessor, BlockPos blockPos, Direction.Axis axis, CallbackInfo ci) {
        bn_shape = new BNPortalShape(levelAccessor, blockPos, axis);
    }

    @Inject(method = "calculateBottomLeft", at = @At(value = "HEAD"), cancellable = true)
    private void bn_calculateBottomLeft(BlockPos blockPos, CallbackInfoReturnable<BlockPos> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.calculateBottomLeft(blockPos));
            cir.cancel();
        }
    }

    @Inject(method = "calculateWidth", at = @At(value = "HEAD"), cancellable = true)
    private void bn_calculateWidth(CallbackInfoReturnable<Integer> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.calculateWidth());
            cir.cancel();
        }
    }

    @Inject(method = "calculateHeight", at = @At(value = "HEAD"), cancellable = true)
    private void bn_calculateHeight(CallbackInfoReturnable<Integer> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.calculateHeight());
            cir.cancel();
        }
    }

    @Inject(method = "createPortalBlocks", at = @At(value = "HEAD"), cancellable = true)
    private void bn_createPortalBlocks(CallbackInfo ci) {
        if (bn_shape != null) {
            bn_shape.createPortalBlocks();
            ci.cancel();
        }
    }

    @Inject(method = "isComplete", at = @At(value = "HEAD"), cancellable = true)
    private void bn_isComplete(CallbackInfoReturnable<Boolean> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.isComplete());
            cir.cancel();
        }
    }

    @Inject(method = "isValid", at = @At(value = "HEAD"), cancellable = true)
    private void bn_isValid(CallbackInfoReturnable<Boolean> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.isValid());
            cir.cancel();
        }
    }
}
