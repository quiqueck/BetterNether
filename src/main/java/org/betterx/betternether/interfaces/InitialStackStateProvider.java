package org.betterx.betternether.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public interface InitialStackStateProvider {
    default void initializeState(ItemStack stack) {
        putEnchantments(stack, new ItemEnchantments.Mutable(ItemEnchantments.EMPTY));
    }

    void putEnchantments(ItemStack stack, ItemEnchantments.Mutable defaultEnchants);
}
