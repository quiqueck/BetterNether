package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.NetherBlocks;
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

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import java.util.List;

public class SoulPlain extends NetherBiomeConfig {
    private static final SurfaceRules.RuleSource SOUL_SAND = SurfaceRules.state(Blocks.SOUL_SAND.defaultBlockState());
    private static final SurfaceRules.RuleSource SOUL_SOIL = SurfaceRules.state(Blocks.SOUL_SOIL.defaultBlockState());
    private static final SurfaceRules.RuleSource SOUL_SANDSTONE = SurfaceRules.state(NetherBlocks.SOUL_SANDSTONE.defaultBlockState());
    private static final SurfaceRules.RuleSource LAVA = SurfaceRules.state(Blocks.MAGMA_BLOCK.defaultBlockState());

    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(196, 113, 239)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY)
               .particles(ParticleTypes.PORTAL, 0.02F)
               .structure(BiomeTags.HAS_NETHER_FOSSIL)
               .feature(NetherTerrainPlaced.REPLACE_SOUL_SANDSTONE)
               .feature(NetherFeatures.NETHER_RUBY_ORE_SOUL)
               .feature(NetherObjectsPlaced.BASALT_STALAGMITE_SPARSE)
               .feature(NetherVegetationPlaced.BLACK_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_SOUL_PLAIN)
               .feature(NetherObjectsPlaced.BASALT_STALACTITE_SPARSE)
               .addNetherClimate(0.01f, -0.5f, 0.0f)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);

        RuleSource soilSandDist
                = SurfaceRules.sequence(SurfaceRules.ifTrue(Conditions.NETHER_VOLUME_NOISE, SOUL_SOIL), SOUL_SAND);

        RuleSource soilSandStoneDist
                = SurfaceRules.sequence(new SwitchRuleSource(
                NetherNoiseCondition.INSTANCE,
                List.of(
                        SOUL_SOIL,
                        SOUL_SAND,
                        SOUL_SANDSTONE,
                        LAVA,
                        LAVA,
                        SOUL_SAND
                )
        ));

        RuleSource soilStoneDist
                = SurfaceRules.sequence(
                SurfaceRules.ifTrue(Conditions.NETHER_VOLUME_NOISE, SOUL_SOIL),
                SOUL_SANDSTONE
        );

        builder.rule(
                       SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, soilSandDist),
                       BaseSurfaceRuleBuilder.CEILING_PRIORITY + 1
               )
               .ceil(NetherBlocks.SOUL_SANDSTONE.defaultBlockState())
               .rule(
                       SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, soilStoneDist),
                       BaseSurfaceRuleBuilder.CEILING_PRIORITY - 1
               )
               .rule(soilSandStoneDist, BaseSurfaceRuleBuilder.CEILING_PRIORITY - 2);
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return Biomes.SOUL_SAND_VALLEY;
    }
}