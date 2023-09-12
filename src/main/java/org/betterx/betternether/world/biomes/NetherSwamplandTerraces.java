package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class NetherSwamplandTerraces extends NetherSwampland {
    @Override
    protected void addCustomSwamplandBuildData(NetherBiomeBuilder builder) {
        builder.feature(NetherTerrainPlaced.LAVA_TERRACE).addNetherClimate(0.08f, 0.85f, 0.1f);
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return NetherBiomes.NETHER_SWAMPLAND.key;
    }
}
