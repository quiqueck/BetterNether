package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;

public class BoneReef extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(47, 221, 202)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .particles(ParticleTypes.WARPED_SPORE, 0.01F)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherTerrainPlaced.LAVA_PITS_SPARSE)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherObjectsPlaced.BONES)
               .feature(NetherObjectsPlaced.BONE_STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_BONE_REEF)
               .feature(NetherVegetationPlaced.JELLYFISH_MUSHROOM)
               .feature(NetherVinesPlaced.LUMABUS_VINE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(0.0f, 0.7f, 0.375f)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(NetherBlocks.MUSHROOM_GRASS.defaultBlockState());
    }
}
