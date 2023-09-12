package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class WartForestEdge extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(191, 28, 28)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTerrainPlaced.REPLACE_SOUL_SANDSTONE)
               .feature(NetherObjectsPlaced.BASALT_STALAGMITE)
               .feature(NetherVegetationPlaced.BLACK_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_WART_FOREST_EDGE)
               .addNetherClimate(-0.5f, 0.45f, 0.0f)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.rule(
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                new SwitchRuleSource(
                                        NetherNoiseCondition.INSTANCE,
                                        List.of(
                                                NetherGrasslands.SOUL_SOIL,
                                                NetherGrasslands.SOUL_SAND,
                                                NetherGrasslands.MOSS,
                                                NETHERRACK
                                        )
                                )
                        ),
                        SurfaceRules.ifTrue(
                                Conditions.NETHER_VOLUME_NOISE,
                                NetherGrasslands.SOUL_SAND
                        ),
                        NETHERRACK
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        )
        ;
    }

    @Override
    public boolean hasNetherCity() {
        return false;
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case FLYING_PIG -> res = type.weight;
            case NAGA -> res = 0;
        }
        return res;
    }
}