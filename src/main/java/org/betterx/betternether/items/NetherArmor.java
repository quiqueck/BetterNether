package org.betterx.betternether.items;

import org.betterx.bclib.items.BaseArmorItem;
import org.betterx.betternether.items.materials.BNArmorMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class NetherArmor extends BaseArmorItem implements ItemWithCustomStack {
    public NetherArmor(Holder<ArmorMaterial> material, Type type) {
        super(material, type, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return super.getDefaultInstance();
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (BNArmorMaterial.FLAMING_RUBY.is(material)) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE_KEY, 1, provider);
        }
    }
}
