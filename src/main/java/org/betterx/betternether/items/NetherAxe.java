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
 * 26.3 deleted {@code AxeItem}. Its constructor was already nothing more than
 * {@code super(properties.axe(material, damage, speed))} (verified against the 26.1 bytecode), and its
 * {@code useOn} override - log stripping - became the data-driven {@code minecraft:block_transformer}
 * item component that {@link Item.Properties#axe} attaches. Extending {@link Item} and calling
 * {@code axe(...)} is therefore an exact replacement, not a downgrade; this is the same move WoVer made
 * in {@code EquipmentSet} and BCLib in {@code ToolBlockTransformers}.
 */
public class NetherAxe extends Item implements ItemWithCustomStack {
    private final ToolMaterial material;

    public NetherAxe(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties settings) {
        super(settings.axe(material, attackDamage, attackSpeed));
        this.material = material;
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        if (material == BNToolMaterial.FLAMING_RUBY) {
            EnchantmentUtils.enchantInWorld(stack, NetherEnchantments.RUBY_FIRE.key(), 1, provider);
        }
    }
}
