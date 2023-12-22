package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

;

public class UpsideDownForest extends NetherBiomeConfig {
    static final SurfaceRules.RuleSource CEILEING_MOSS = SurfaceRules.state(NetherBlocks.CEILING_MUSHROOMS.defaultBlockState());
    static final SurfaceRules.RuleSource NETHERRACK_MOSS = SurfaceRules.state(NetherBlocks.NETHERRACK_MOSS.defaultBlockState());
    static final SurfaceRules.ConditionSource NOISE_CEIL_LAYER = SurfaceRules.noiseCondition(
            Noises.NETHER_STATE_SELECTOR,
            0.0
    );

    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(111, 188, 111)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .structure(BiomeTags.HAS_NETHER_FOSSIL)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.ANCHOR_TREE)
               .feature(NetherTreesPlaced.SAKURA_TREE)
               .feature(NetherTreesPlaced.ANCHOR_TREE_BRANCH)
               .feature(NetherTreesPlaced.ANCHOR_TREE_ROOT)
               .feature(NetherObjectsPlaced.FOREST_LITTER)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.SAKURA_BUSH)
               .feature(NetherVegetationPlaced.MOSS_COVER)
               .feature(NetherVinesPlaced.NEON_EQUISETUM)
               .feature(NetherVinesPlaced.PATCH_WHISPERING_GOURD_VINE)
               .feature(NetherVegetationPlaced.HOOK_MUSHROOM)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_LUCIS)
               .feature(NetherVegetationPlaced.WALL_UPSIDE_DOWN)
               .addNetherClimate(0.45f, 0.8f, 0.125f)
               .vertical(true)
               .genChance(0.25f);
    }

    @Override
    public boolean hasBNStructures() {
        return false;
    }

    @Override
    public boolean hasBNFeatures() {
        return false;
    }


    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.rule(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_CEILING,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(
                                Conditions.FORREST_FLOOR_SURFACE_NOISE_A,
                                CEILEING_MOSS
                        ), NETHERRACK)
                ),
                BaseSurfaceRuleBuilder.CEILING_PRIORITY
        ).rule(

                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(
                                Conditions.roughNoise(Noises.NETHERRACK, 0.021),
                                NETHERRACK_MOSS
                        ), SurfaceRules.state(
                                NetherBlocks.MUSHROOM_GRASS.defaultBlockState()))
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        );
    }
}