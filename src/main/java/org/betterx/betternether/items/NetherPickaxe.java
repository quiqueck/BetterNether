package org.betterx.betternether.items;

import org.betterx.bclib.items.tool.BasePickaxeItem;
import org.betterx.betternether.interfaces.InitialStackStateProvider;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class NetherPickaxe extends BasePickaxeItem implements InitialStackStateProvider {
    public NetherPickaxe(Tier material, float attackDamage, float attackSpeed) {
        super(material, (int) attackDamage, attackSpeed, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public void putEnchantments(ItemStack stack, Map<Enchantment, Integer> defaultEnchants) {
        int obsidianLevel = 0;
        if (this.getTier() == BNToolMaterial.CINCINNASITE_DIAMOND) obsidianLevel = 2;
        else if (this.getTier() == BNToolMaterial.NETHER_RUBY) {
            obsidianLevel = 1;
        } else if (this.getTier() == BNToolMaterial.FLAMING_RUBY) {
            obsidianLevel = 3;
            defaultEnchants.put(NetherEnchantments.RUBY_FIRE, 1);
            defaultEnchants.put(Enchantments.MENDING, 1);
        }

        if (obsidianLevel > 0) {
            defaultEnchants.put(NetherEnchantments.OBSIDIAN_BREAKER, obsidianLevel);
            EnchantmentHelper.setEnchantments(defaultEnchants, stack);
        }
    }
}
