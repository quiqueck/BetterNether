package org.betterx.betternether.world.biomes;


import org.betterx.betternether.blocks.BlockSoulSandstone;
import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.features.placed.*;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;

;

public class WartForest extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(151, 6, 6)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .particles(ParticleTypes.CRIMSON_SPORE, 0.05F)
               .feature(NetherOresPlaced.NETHER_RUBY_ORE)
               .feature(NetherTerrainPlaced.REPLACE_SOUL_SANDSTONE)
               .feature(NetherObjectsPlaced.WART_DEADWOOD)
               .feature(NetherTreesPlaced.SOUL_LILY)
               .feature(NetherTreesPlaced.WART_TREE)
               .feature(NetherObjectsPlaced.BASALT_STALAGMITE_SPARSE)
               .feature(NetherVegetationPlaced.BLACK_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_WART_FOREST)
               .feature(NetherObjectsPlaced.BASALT_STALACTITE_SPARSE)
               .addNetherClimate(-0.5f, 0.5f, 0.0f)
               .edgeSize(9)
        ;
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
                                                NetherGrasslands.SOUL_SAND,
                                                NETHERRACK
                                        )
                                )
                        ),
                        SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(4, true, 1, CaveSurface.FLOOR),
                                new SwitchRuleSource(
                                        NetherNoiseCondition.INSTANCE,
                                        List.of(
                                                SurfaceRules.state(
                                                        NetherBlocks.SOUL_SANDSTONE
                                                                .defaultBlockState()
                                                                .setValue(BlockSoulSandstone.UP, true)
                                                ),
                                                SurfaceRules.state(
                                                        NetherBlocks.SOUL_SANDSTONE
                                                                .defaultBlockState()
                                                                .setValue(BlockSoulSandstone.UP, false)
                                                )
                                        )
                                )
                        ),
                        new SwitchRuleSource(
                                NetherNoiseCondition.INSTANCE,
                                List.of(
                                        NetherGrasslands.SOUL_SOIL,
                                        NetherGrasslands.SOUL_SAND,
                                        NETHERRACK
                                )
                        )
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        )
        ;
    }

    @Override
    public ResourceKey<Biome> withEdgeBiome() {
        return NetherBiomes.WART_FOREST_EDGE.key;
    }
}
