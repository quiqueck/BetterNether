package org.betterx.betternether.mixin.common;

import org.betterx.betternether.interfaces.InitialStackStateProvider;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
    @Shadow
    @Final
    ItemStack result;

    @Inject(method = "assemble", at = @At("HEAD"), cancellable = true)
    public void bcl_assemble(
            Container container,
            RegistryAccess registryAccess,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        if (this.result.getItem() instanceof InitialStackStateProvider p) {
            //Code from original assemble Methode
            ItemStack itemStack = this.result.copy();
            CompoundTag compoundTag = container.getItem(1).getTag();
            if (compoundTag != null) {
                itemStack.setTag(compoundTag.copy());
            }
            //Code from original assemble Methode

            p.putEnchantments(itemStack, EnchantmentHelper.getEnchantments(itemStack));
            cir.setReturnValue(itemStack);
        }
    }
}

