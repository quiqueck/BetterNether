package org.betterx.betternether.mixin.common;

import org.betterx.betternether.BetterNether;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin {
    @Inject(method = "freeze", at = @At("HEAD"))
    void bn_debug(CallbackInfoReturnable<Registry<?>> cir) {
        if (this.toString().contains("worlds_together:worldgen/betterx/surface_rules")) {
            BetterNether.LOGGER.info("Freeze " + this.toString());
        }
    }
}
