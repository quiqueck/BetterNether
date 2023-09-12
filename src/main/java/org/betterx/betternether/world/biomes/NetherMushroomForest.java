package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.SoundsRegistry;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class NetherMushroomForest extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(166, 38, 95)
               .loop(SoundsRegistry.AMBIENT_MUSHROOM_FOREST)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .particles(ParticleTypes.MYCELIUM, 0.1F)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.MUSHROOM_FIR)
               .feature(NetherTreesPlaced.BIG_RED_MUSHROOM)
               .feature(NetherTreesPlaced.BIG_BROWN_MUSHROOM)
               .feature(NetherTreesPlaced.GIANT_MOLD)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_MUSHROOM_FORREST)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_LUCIS)
               .feature(NetherVegetationPlaced.WALL_MUSHROOMS)
               .addNetherClimate(0.0f, 0.7f, 0)
               .edgeSize(6)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(NetherBlocks.NETHER_MYCELIUM.defaultBlockState());
    }

    @Override
    public ResourceKey<Biome> withEdgeBiome() {
        return NetherBiomes.NETHER_MUSHROOM_FORREST_EDGE.key;
    }
}
