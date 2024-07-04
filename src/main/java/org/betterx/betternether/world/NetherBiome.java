package org.betterx.betternether.world;

import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherBiome extends WoverBiomeData {
    public static final MapCodec<NetherBiome> CODEC = codec(NetherBiome::new);
    public static final KeyDispatchDataCodec<NetherBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

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
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }
}
