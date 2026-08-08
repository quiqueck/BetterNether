package org.betterx.betternether.world;

import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherBiome extends WoverBiomeData {
    // 26.3 removed KeyDispatchDataCodec, which was only ever a wrapper around a MapCodec. WoVer's
    // BiomeData#codec() and BiomeCodecRegistry#register now take/return the MapCodec directly, so the
    // KEY_CODEC constant is gone rather than replaced.
    public static final MapCodec<NetherBiome> CODEC = codec(NetherBiome::new);

    public NetherBiome(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generationData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent
    ) {
        super(
                fogDensity, biome, generationData, terrainHeight,
                genChance, edgeSize, vertical, edge, parent
        );
    }

    @Override
    public MapCodec<? extends WoverBiomeData> codec() {
        return CODEC;
    }
}
