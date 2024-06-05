package org.betterx.betternether.items;

import org.betterx.bclib.items.tool.BasePickaxeItem;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.enchantment.api.EnchantmentUtils;
import org.betterx.wover.item.api.ItemWithCustomStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantments;

public class NetherPickaxe extends BasePickaxeItem implements ItemWithCustomStack {
    public NetherPickaxe(Tier material, float attackDamage, float attackSpeed) {
        super(material, (int) attackDamage, attackSpeed, NetherItems.defaultSettings().fireResistant());
    }


    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        provider.lookup(Registries.ENCHANTMENT).ifPresent(lookup -> {
            int obsidianLevel = 0;
            if (this.getTier() == BNToolMaterial.CINCINNASITE_DIAMOND) obsidianLevel = 2;
            else if (this.getTier() == BNToolMaterial.NETHER_RUBY) {
                obsidianLevel = 1;
            } else if (this.getTier() == BNToolMaterial.FLAMING_RUBY) {
                obsidianLevel = 3;
                EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE_KEY, 1, lookup);
                EnchantmentUtils.enchantInWorld(stack, Enchantments.MENDING, 1, lookup);
            }

            if (obsidianLevel > 0) {
                EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.OBSIDIAN_BREAKER_KEY, obsidianLevel, lookup);
            }
        });

    }
}
