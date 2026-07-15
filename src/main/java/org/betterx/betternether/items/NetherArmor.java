package org.betterx.betternether.items;

import org.betterx.betternether.items.materials.BNArmorTiers;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;

public class NetherArmor extends Item implements ItemWithCustomStack {
    private final ArmorMaterial material;

    public NetherArmor(ArmorMaterial material, Item.Properties settings) {
        super(settings);
        this.material = material;
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (BNArmorTiers.FLAMING_RUBY.is(material)) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE.key(), 1, provider);
        }
    }
}
