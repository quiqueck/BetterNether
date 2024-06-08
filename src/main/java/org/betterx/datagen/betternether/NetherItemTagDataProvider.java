package org.betterx.datagen.betternether;

import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

public class NetherItemTagDataProvider extends WoverTagProvider.ForItems {
    public NetherItemTagDataProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(ItemTagBootstrapContext context) {
        context.add(
                NetherTags.FLAMING_RUBY_ENCHANTABLE,
                NetherItems.FLAMING_RUBY_SET.getAll()
        );

        context.add(
                NetherTags.OBSIDIAN_BREAKER_ENCHANTABLE,
                NetherTags.NETHER_PICKAXES
        );
    }
}
