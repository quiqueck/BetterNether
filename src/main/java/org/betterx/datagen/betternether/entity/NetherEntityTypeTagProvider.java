package org.betterx.datagen.betternether.entity;

import org.betterx.betternether.registry.NetherEntities;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class NetherEntityTypeTagProvider extends WoverTagProvider.ForEntityTypes {
    public NetherEntityTypeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<EntityType<?>> context) {
        context.add(EntityTypeTags.UNDEAD, NetherEntities.NAGA.type(), NetherEntities.SKULL.type());
        context.add(EntityTypeTags.ARTHROPOD, NetherEntities.FIREFLY.type());
    }
}
