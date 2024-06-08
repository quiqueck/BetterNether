package org.betterx.datagen.betternether.enchantments;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class NetherEnchantmentTagProvider extends WoverTagProvider.ForEnchantments {
    public NetherEnchantmentTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Enchantment> context) {
        context.add(EnchantmentTags.CURSE, NetherEnchantments.RUBY_FIRE.key(), NetherEnchantments.OBSIDIAN_BREAKER.key());
        context.add(EnchantmentTags.DOUBLE_TRADE_PRICE, NetherEnchantments.RUBY_FIRE.key(), NetherEnchantments.OBSIDIAN_BREAKER.key());

    }
}
