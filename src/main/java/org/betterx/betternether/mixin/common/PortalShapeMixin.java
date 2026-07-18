package org.betterx.betternether.mixin.common;

import org.betterx.betternether.portals.BNPortalShape;
import org.betterx.betternether.portals.BNPortalShapeProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.portal.PortalShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalShape.class)
public class PortalShapeMixin implements BNPortalShapeProvider {
    @Unique
    private BNPortalShape bn_shape;

    @Override
    public void bn_setShape(BNPortalShape shape) {
        this.bn_shape = shape;
    }

    @Override
    public BNPortalShape bn_getShape() {
        return this.bn_shape;
    }

    // findAnyShape is the single entry point used both for portal creation (fire lighting)
    // and for validating existing portals. Attach BetterNether's flexible flood-fill shape to
    // every PortalShape it produces so the overrides below can drive the behaviour.
    @Inject(method = "findAnyShape", at = @At("RETURN"))
    private static void bn_findAnyShape(
            BlockGetter blockGetter,
            BlockPos blockPos,
            Direction.Axis axis,
            CallbackInfoReturnable<PortalShape> cir
    ) {
        PortalShape shape = cir.getReturnValue();
        if (shape != null) {
            ((BNPortalShapeProvider) shape).bn_setShape(new BNPortalShape(blockGetter, blockPos, axis));
        }
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void bn_isValid(CallbackInfoReturnable<Boolean> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.isValid());
        }
    }

    @Inject(method = "isComplete", at = @At("HEAD"), cancellable = true)
    private void bn_isComplete(CallbackInfoReturnable<Boolean> cir) {
        if (bn_shape != null) {
            cir.setReturnValue(bn_shape.isComplete());
        }
    }

    @Inject(method = "createPortalBlocks", at = @At("HEAD"), cancellable = true)
    private void bn_createPortalBlocks(LevelAccessor levelAccessor, CallbackInfo ci) {
        if (bn_shape != null) {
            bn_shape.createPortalBlocks(levelAccessor);
            ci.cancel();
        }
    }
}
