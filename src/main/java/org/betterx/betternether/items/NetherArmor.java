package org.betterx.betternether.items;

import org.betterx.bclib.items.BaseArmorItem;
import org.betterx.betternether.interfaces.InitialStackStateProvider;
import org.betterx.betternether.items.materials.BNArmorMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class NetherArmor extends BaseArmorItem implements InitialStackStateProvider {
    public NetherArmor(ArmorMaterial material, Type type) {
        super(material, type, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public ItemStack getDefaultInstance() {
        return super.getDefaultInstance();
    }

    @Override
    public void putEnchantments(ItemStack stack, ItemEnchantments.Mutable defaultEnchants) {
        if (material == BNArmorMaterial.FLAMING_RUBY) {
            defaultEnchants.set(NetherEnchantments.RUBY_FIRE, 1);
            EnchantmentHelper.setEnchantments(stack, defaultEnchants);
        }
    }
}
