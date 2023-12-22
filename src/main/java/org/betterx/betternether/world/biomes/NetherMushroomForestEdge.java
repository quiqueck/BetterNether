package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherOresPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.providers.NetherMushroomForestEdgeNumericProvider;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class NetherMushroomForestEdge extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(200, 121, 157)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .feature(NetherOresPlaced.NETHER_RUBY_ORE)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_MUSHROOM_FORREST_EDGE)
               .addNetherClimate(0.0f, 0.7f, 0.1f)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.rule(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        new SwitchRuleSource(
                                NetherMushroomForestEdgeNumericProvider.DEFAULT,
                                List.of(
                                        SurfaceRules.state(NetherBlocks.NETHER_MYCELIUM.defaultBlockState()),
                                        SurfaceRules.state(NetherBlocks.NETHERRACK_MOSS.defaultBlockState()),
                                        NETHERRACK
                                )
                        )
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        );
    }

    @Override
    public boolean hasNetherCity() {
        return false;
    }
}