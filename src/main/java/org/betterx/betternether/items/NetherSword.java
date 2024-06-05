package org.betterx.betternether.items;

import org.betterx.bclib.items.tool.BaseSwordItem;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class NetherSword extends BaseSwordItem implements ItemWithCustomStack {
    public NetherSword(Tier material, float attackDamage, float attackSpeed) {
        super(material, (int) attackDamage, attackSpeed, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (getTier() == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE_KEY, 1, provider);
        }
    }
}
