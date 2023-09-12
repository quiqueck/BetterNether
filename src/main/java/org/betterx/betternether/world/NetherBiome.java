package org.betterx.betternether.world;

import org.betterx.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherBiome extends WoverBiomeData {
    public static final Codec<NetherBiome> CODEC = codec(NetherBiome::new);
    public static final KeyDispatchDataCodec<NetherBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    public NetherBiome(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull List<Climate.ParameterPoint> parameterPoints,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent
    ) {
        super(
                fogDensity, biome, parameterPoints, terrainHeight,
                genChance, edgeSize, vertical, edge, parent
        );
    }

    @Override
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }
}
