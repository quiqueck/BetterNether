package org.betterx.betternether.items;

import org.betterx.bclib.items.tool.BaseAxeItem;
import org.betterx.betternether.interfaces.InitialStackStateProvider;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class NetherAxe extends BaseAxeItem implements InitialStackStateProvider {
    public NetherAxe(Tier material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, NetherItems.defaultSettings().fireResistant());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public void putEnchantments(ItemStack stack, Map<Enchantment, Integer> defaultEnchants) {
        if (getTier() == BNToolMaterial.FLAMING_RUBY) {
            defaultEnchants.put(NetherEnchantments.RUBY_FIRE, 1);
            EnchantmentHelper.setEnchantments(defaultEnchants, stack);
        }
    }

}
