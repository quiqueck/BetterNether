package org.betterx.betternether.mixin.common;

import org.betterx.betternether.interfaces.InitialStackStateProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void bcl_assemble(
            SmithingRecipeInput smithingRecipeInput, HolderLookup.Provider provider,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        if (this.result.getItem() instanceof InitialStackStateProvider p) {
            //Code from original assemble Methode
            ItemStack itemStack = smithingRecipeInput
                    .base()
                    .transmuteCopy(this.result.getItem(), this.result.getCount());
            itemStack.applyComponents(this.result.getComponentsPatch());
            //Code from original assemble Methode

            p.putEnchantments(itemStack, new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemStack)));
            cir.setReturnValue(itemStack);
        }
    }
}

