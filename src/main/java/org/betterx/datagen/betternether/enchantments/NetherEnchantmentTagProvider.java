package org.betterx.datagen.betternether.enchantments;

import org.betterx.betternether.registry.NetherEnchantments;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class NetherEnchantmentTagProvider extends WoverTagProvider.ForEnchantments {
    public NetherEnchantmentTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Enchantment> context) {
        context.add(EnchantmentTags.CURSE, NetherEnchantments.RUBY_FIRE.key(), NetherEnchantments.OBSIDIAN_BREAKER.key());
        context.add(EnchantmentTags.DOUBLE_TRADE_PRICE, NetherEnchantments.RUBY_FIRE.key(), NetherEnchantments.OBSIDIAN_BREAKER.key());

    }
}
