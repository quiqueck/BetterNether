package org.betterx.betternether.mixin.common;

import net.minecraft.world.level.chunk.ChunkGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorDebugMixin {
    @ModifyArg(
            method = "method_39788",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/ints/IntSet;add(I)Z"
            ))
    private static int bcl_test(int par1) {
        if (par1 < 0) {
            System.out.println("failing index mapping : " + par1);
        }
        return par1;
    }
}
