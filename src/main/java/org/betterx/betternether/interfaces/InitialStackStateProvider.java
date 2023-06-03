package org.betterx.betternether.interfaces;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public interface InitialStackStateProvider {
    default void initializeState(ItemStack stack) {
        putEnchantments(stack, new HashMap<>());
    }

    default void onCraftedBy(ItemStack itemStack, Level level, Player player) {
        putEnchantments(itemStack, EnchantmentHelper.getEnchantments(itemStack));
    }
    void putEnchantments(ItemStack stack, Map<Enchantment, Integer> defaultEnchants);
}
