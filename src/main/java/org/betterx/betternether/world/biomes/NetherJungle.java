package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.SoundsRegistry;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;

public class NetherJungle extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(62, 169, 61)
               .loop(SoundsRegistry.AMBIENT_NETHER_JUNGLE)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_WARPED_FOREST)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(NetherStructures.JUNGLE_TEMPLES)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherObjectsPlaced.JUNGLE_BONES)
               .feature(NetherTreesPlaced.RUBEUS_TREE)
               .feature(NetherTreesPlaced.STALAGNATE)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.RUBEUS_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_JUNGLE)
               .feature(NetherVegetationPlaced.JELLYFISH_MUSHROOM)
               .feature(NetherVinesPlaced.BLACK_VINE)
               .feature(NetherVinesPlaced.BLOOMING_VINE)
               .feature(NetherVinesPlaced.EYE_VINE)
               .feature(NetherVinesPlaced.GOLDEN_VINE_SPARSE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_LUCIS)
               .feature(NetherVegetationPlaced.WALL_JUNGLE)
               .addNetherClimate(0.5f, 0.8f, 0)
        ;
    }

    @Override
    public boolean hasBNStructures() {
        return false;
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case GHAST, ZOMBIFIED_PIGLIN, MAGMA_CUBE, ENDERMAN, PIGLIN, STRIDER, HOGLIN, PIGLIN_BRUTE -> res = 0;
            case JUNGLE_SKELETON -> res = 40;
        }
        return res;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(NetherBlocks.JUNGLE_GRASS.defaultBlockState());
    }
}