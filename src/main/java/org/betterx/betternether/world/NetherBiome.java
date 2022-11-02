package org.betterx.betternether.world;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeSettings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.Optional;

public class NetherBiome extends BCLBiome {
    public static final Codec<NetherBiome> CODEC = RecordCodecBuilder.create(instance ->
            codecWithSettings(instance).apply(instance, NetherBiome::new)
    );
    public static final KeyDispatchDataCodec<NetherBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public KeyDispatchDataCodec<? extends BCLBiome> codec() {
        return KEY_CODEC;
    }

    protected NetherBiome(
            float terrainHeight,
            float fogDensity,
            float genChance,
            int edgeSize,
            boolean vertical,
            Optional<ResourceLocation> edge,
            ResourceLocation biomeID,
            Optional<List<Climate.ParameterPoint>> parameterPoints,
            Optional<ResourceLocation> biomeParent,
            Optional<String> intendedType
    ) {
        super(
                terrainHeight,
                fogDensity,
                genChance,
                edgeSize,
                vertical,
                edge,
                biomeID,
                parameterPoints,
                biomeParent,
                intendedType
        );
    }

    protected NetherBiome(ResourceLocation biomeID, Biome biome, BCLBiomeSettings settings) {
        super(biomeID, biome, settings);
    }
}
