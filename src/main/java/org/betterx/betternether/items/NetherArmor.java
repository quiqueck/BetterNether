package org.betterx.betternether.items;

import org.betterx.bclib.items.BaseArmorItem;
import org.betterx.betternether.items.materials.BNArmorMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class NetherArmor extends BaseArmorItem implements ItemWithCustomStack {
    public NetherArmor(ArmorMaterial material, Type type) {
        super(material, type, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public ItemStack getDefaultInstance() {
        return super.getDefaultInstance();
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (material.value() == BNArmorMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE_KEY, 1, provider);
        }
    }
}
