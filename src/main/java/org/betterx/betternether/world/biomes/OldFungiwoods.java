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

public class OldFungiwoods extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(166, 38, 95)
               .loop(SoundsRegistry.AMBIENT_MUSHROOM_FOREST)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .particles(ParticleTypes.MYCELIUM, 0.1F)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.OLD_RED_MUSHROOM)
               .feature(NetherTreesPlaced.OLD_BROWN_MUSHROOM)
               .feature(NetherTreesPlaced.BIG_RED_MUSHROOM)
               .feature(NetherTreesPlaced.BIG_BROWN_MUSHROOM)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_MUSHROOM_FORREST)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_MUSHROOM_RED_WITH_MOSS)
               .addNetherClimate(0.0f, 0.65f, 0)
               .genChance(0.3f)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(NetherBlocks.NETHER_MYCELIUM.defaultBlockState());
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return NetherBiomes.NETHER_MUSHROOM_FORREST.key;
    }
}