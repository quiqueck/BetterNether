package org.betterx.betternether.items;

import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class NetherSword extends Item implements ItemWithCustomStack {
    private final ToolMaterial material;

    public NetherSword(ToolMaterial material, Item.Properties settings) {
        super(settings);
        this.material = material;
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (material == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE.key(), 1, provider);
        }
    }
}
