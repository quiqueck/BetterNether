package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

;

public class PoorNetherGrasslands extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(113, 73, 133)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherVegetationPlaced.BLACK_BUSH_SPARSE)
               .feature(NetherVegetationPlaced.VEGETATION_POOR_GRASSLANDS)
               .feature(NetherObjectsPlaced.SMOKER_SPARSE)
               .addNetherClimate(0.0f, 0.3f, 0.0f)
               .genChance(0.3F)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        SurfaceRules.RuleSource soilStoneDist
                = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        Conditions.NETHER_VOLUME_NOISE,
                        NetherGrasslands.SOUL_SOIL
                ),
                SurfaceRules.state(Blocks.NETHERRACK.defaultBlockState())
        );

        builder.rule(
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                new SwitchRuleSource(
                                        NetherNoiseCondition.INSTANCE,
                                        List.of(NetherGrasslands.MOSS, NetherGrasslands.SOUL_SOIL, NETHERRACK)
                                )
                        ),
                        soilStoneDist
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        );
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return NetherBiomes.NETHER_GRASSLANDS.key;
    }
}