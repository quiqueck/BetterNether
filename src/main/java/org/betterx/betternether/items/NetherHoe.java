package org.betterx.betternether.items;

import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

/**
 * 26.3 deleted {@code HoeItem}. Its constructor was already nothing more than
 * {@code super(properties.hoe(material, damage, speed))}, and its {@code useOn} override - tilling and
 * the dirt/rooted-dirt conversions - became the data-driven {@code minecraft:block_transformer} item
 * component that {@link Item.Properties#hoe} attaches. See {@link NetherAxe} for the full reasoning.
 */
public class NetherHoe extends Item implements ItemWithCustomStack {
    private final ToolMaterial material;

    public NetherHoe(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties settings) {
        super(settings.hoe(material, attackDamage, attackSpeed));
        this.material = material;
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (material == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE.key(), 1, provider);
        }
    }
}
