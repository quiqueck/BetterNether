package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class SulfuricBoneReef extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(154, 144, 49)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .particles(ParticleTypes.ASH, 0.01F)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherObjectsPlaced.BONES)
               .feature(NetherObjectsPlaced.BONE_STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_SULFURIC_BONE_REEF)
               .feature(NetherVegetationPlaced.PATCH_JELLYFISH_MUSHROOM)
               .feature(NetherVinesPlaced.GOLDEN_LUMABUS_VINE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(0.0f, 0.6f, 0.370f)
               .genChance(0.3f);
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(NetherBlocks.SEPIA_MUSHROOM_GRASS.defaultBlockState());
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return NetherBiomes.BONE_REEF.key;
    }
}