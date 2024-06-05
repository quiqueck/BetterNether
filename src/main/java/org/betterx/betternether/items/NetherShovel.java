package org.betterx.betternether.items;

import org.betterx.bclib.items.tool.BaseShovelItem;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.enchantment.api.EnchantmentUtils;
import org.betterx.wover.item.api.ItemWithCustomStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class NetherShovel extends BaseShovelItem implements ItemWithCustomStack {
    public NetherShovel(Tier material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (getTier() == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE_KEY, 1, provider);
        }
    }
}
