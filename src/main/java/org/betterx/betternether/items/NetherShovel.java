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
 * 26.3 deleted {@code ShovelItem}. Its constructor was already nothing more than
 * {@code super(properties.shovel(material, damage, speed))}, and its {@code useOn} override - path
 * making and campfire extinguishing - became the data-driven {@code minecraft:block_transformer} item
 * component that {@link Item.Properties#shovel} attaches. See {@link NetherAxe} for the full reasoning.
 */
public class NetherShovel extends Item implements ItemWithCustomStack {
    private final ToolMaterial material;

    public NetherShovel(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties settings) {
        super(settings.shovel(material, attackDamage, attackSpeed));
        this.material = material;
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (material == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE.key(), 1, provider);
        }
    }
}
