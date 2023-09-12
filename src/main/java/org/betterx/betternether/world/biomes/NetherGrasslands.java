package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.providers.NetherGrasslandsNumericProvider;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class NetherGrasslands extends NetherBiomeConfig {
    static final SurfaceRules.RuleSource SOUL_SOIL = SurfaceRules.state(Blocks.SOUL_SOIL.defaultBlockState());
    static final SurfaceRules.RuleSource SOUL_SAND = SurfaceRules.state(Blocks.SOUL_SAND.defaultBlockState());
    static final SurfaceRules.RuleSource MOSS = SurfaceRules.state(NetherBlocks.NETHERRACK_MOSS.defaultBlockState());

    private static final SurfaceRules.RuleSource BLUE = SurfaceRules.state(Blocks.BLUE_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource LIGHT_BLUE = SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource CYAN = SurfaceRules.state(Blocks.CYAN_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource GREEN = SurfaceRules.state(Blocks.GREEN_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource LIME_GREEN = SurfaceRules.state(Blocks.LIME_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource YELLOW = SurfaceRules.state(Blocks.YELLOW_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource ORANGE = SurfaceRules.state(Blocks.ORANGE_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource RED = SurfaceRules.state(Blocks.RED_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource PINK = SurfaceRules.state(Blocks.PINK_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource PURPLE = SurfaceRules.state(Blocks.PURPLE_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource MAGENTA = SurfaceRules.state(Blocks.MAGENTA_CONCRETE.defaultBlockState());
    private static final SurfaceRules.RuleSource BLACK = SurfaceRules.state(Blocks.BLACK_CONCRETE.defaultBlockState());
    //List.of(BLUE, LIGHT_BLUE, CYAN, GREEN, LIME_GREEN, YELLOW, ORANGE, RED, PINK, MAGENTA, PURPLE, BLACK)


    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(113, 73, 133)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_NETHER_WASTES)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherVegetationPlaced.NETHER_REED)
               .feature(NetherVegetationPlaced.BLACK_BUSH_SPARSE)
               .feature(NetherVegetationPlaced.VEGETATION_GRASSLANDS)
               .feature(NetherObjectsPlaced.SMOKER)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_MUSHROOMS_WITH_MOSS)
               .addNetherClimate(0.0f, 0.7f, 0.0f)
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
                                        NetherGrasslandsNumericProvider.DEFAULT,
                                        List.of(SOUL_SOIL, SOUL_SAND, MOSS, NETHERRACK)
                                )
                        ),
                        new SwitchRuleSource(
                                NetherGrasslandsNumericProvider.DEFAULT,
                                List.of(SOUL_SOIL, SOUL_SAND, NETHERRACK)
                        )
                ),
                BaseSurfaceRuleBuilder.FLOOR_PRIORITY
        );
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case FIREFLY -> res = type.weight * 3;
        }
        return res;
    }

}
