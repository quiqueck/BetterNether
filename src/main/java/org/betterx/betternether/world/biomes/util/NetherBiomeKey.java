package org.betterx.betternether.world.biomes.util;

import org.betterx.betternether.world.NetherBiomeConfig;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class NetherBiomeKey<C extends NetherBiomeConfig> extends BiomeKey<NetherBiomeBuilder> {
    protected NetherBiomeKey(@NotNull ResourceLocation location) {
        super(location);
    }

    @Override
    @Deprecated
    public NetherBiomeBuilder bootstrap(BiomeBootstrapContext context) {
        throw new IllegalArgumentException("NetherBiomeKey must be bootstrapped with a config");
    }

    public NetherBiomeBuilder bootstrap(BiomeBootstrapContext context, C config) {
        return new NetherBiomeBuilder(context, this).configure(config);
    }
}
