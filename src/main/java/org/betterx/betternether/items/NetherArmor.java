package org.betterx.betternether.items;

import org.betterx.bclib.items.BaseArmorItem;
import org.betterx.betternether.interfaces.InitialStackStateProvider;
import org.betterx.betternether.items.materials.BNArmorMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class NetherArmor extends BaseArmorItem implements InitialStackStateProvider {
    public NetherArmor(ArmorMaterial material, Type type) {
        super(material, type, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public void putEnchantments(ItemStack stack, Map<Enchantment, Integer> defaultEnchants) {
        if (material == BNArmorMaterial.FLAMING_RUBY) {
            defaultEnchants.put(NetherEnchantments.RUBY_FIRE, 1);
            EnchantmentHelper.setEnchantments(defaultEnchants, stack);
        }
    }
}
