package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.placed.*;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class UpsideDownForestCleared extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(111, 188, 121)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherOresPlaced.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.ANCHOR_TREE_SPARSE)
               .feature(NetherTreesPlaced.ANCHOR_TREE_ROOT)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.SAKURA_BUSH)
               .feature(NetherVegetationPlaced.MOSS_COVER)
               .feature(NetherVinesPlaced.NEON_EQUISETUM)
               .feature(NetherVinesPlaced.PATCH_WHISPERING_GOURD_VINE)
               .feature(NetherVegetationPlaced.HOOK_MUSHROOM)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_LUCIS)
               .feature(NetherVegetationPlaced.WALL_UPSIDE_DOWN)
               .addNetherClimate(0.45f, 0.3f, 0.125f)
               .genChance(0.5f)
        ;
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
                               SurfaceRules.ON_FLOOR,
                               SurfaceRules.sequence(
                                       SurfaceRules.ifTrue(
                                               Conditions.roughNoise(Noises.NETHERRACK, 0.221),
                                               UpsideDownForest.NETHERRACK_MOSS
                                       ),
                                       SurfaceRules.state(
                                               NetherBlocks.MUSHROOM_GRASS.defaultBlockState())
                               )
                       ),
                       BaseSurfaceRuleBuilder.FLOOR_PRIORITY
               )
               .rule(
                       SurfaceRules.ifTrue(
                               SurfaceRules.ON_CEILING,
                               SurfaceRules.sequence(
                                       SurfaceRules.ifTrue(
                                               UpsideDownForest.NOISE_CEIL_LAYER,
                                               UpsideDownForest.CEILEING_MOSS
                                       ),
                                       NETHERRACK
                               )
                       ),
                       BaseSurfaceRuleBuilder.FLOOR_PRIORITY - 1
               );
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return NetherBiomes.UPSIDE_DOWN_FOREST.key;
    }
}