package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.SoundsRegistry;
import org.betterx.betternether.registry.features.placed.*;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class NetherSwampland extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(137, 19, 78)
               .loop(SoundsRegistry.AMBIENT_SWAMPLAND)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherTreesPlaced.WILLOW_TREE)
               .feature(NetherVegetationPlaced.WILLOW_BUSH)
               .feature(NetherVegetationPlaced.BLACK_BUSH_SPARSE)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_SWAMPLAND)
               .feature(NetherVegetationPlaced.JELLYFISH_MUSHROOM)
               .feature(NetherObjectsPlaced.SMOKER)
               .feature(NetherVinesPlaced.BLACK_VINE)
               .feature(NetherVinesPlaced.BLOOMING_VINE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_MUSHROOMS_WITH_MOSS)
        ;
        this.addCustomSwamplandBuildData(builder);
    }

    protected void addCustomSwamplandBuildData(NetherBiomeBuilder builder) {
        builder.feature(NetherTerrainPlaced.LAVA_SWAMP).addNetherClimate(0.1f, 0.8f, 0.1f);
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case ENDERMAN, GHAST, ZOMBIFIED_PIGLIN, PIGLIN, HOGLIN, PIGLIN_BRUTE -> res = 0;
            case MAGMA_CUBE, STRIDER -> res = 40;
        }
        return res;
    }

    public boolean hasDefaultOres() {
        return false;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.rule(
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(
                                        Conditions.roughNoise(Noises.NETHERRACK, 0.14),
                                        SurfaceRules.state(NetherBlocks.SWAMPLAND_GRASS.defaultBlockState())
                                )
                        ),
                        SurfaceRules.ifTrue(
                                Conditions.roughNoise(Noises.NETHER_WART, 0.19),
                                NetherGrasslands.SOUL_SAND
                        ),
                        NetherGrasslands.SOUL_SOIL
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        );
    }
}
