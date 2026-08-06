package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;


import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.providers.NetherGrasslandsNumericProvider;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import de.ambertation.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class NetherGrasslands extends NetherBiomeConfig {
    static final SurfaceRules.RuleSource SOUL_SOIL = SurfaceRules.state(Blocks.SOUL_SOIL.defaultBlockState());
    static final SurfaceRules.RuleSource SOUL_SAND = SurfaceRules.state(Blocks.SOUL_SAND.defaultBlockState());
    static final SurfaceRules.RuleSource MOSS = SurfaceRules.state(NetherTerrainBlocks.NETHERRACK_MOSS.defaultBlockState());

    // 26.2 folded the dyed blocks into ColorCollection records, so Blocks.BLUE_CONCRETE is now
    // Blocks.CONCRETE.blue(). Note LIME_CONCRETE maps onto .lime(); the local constant keeps its
    // old LIME_GREEN name so the commented-out palette below still reads.
    private static final SurfaceRules.RuleSource BLUE = SurfaceRules.state(Blocks.CONCRETE.blue().defaultBlockState());
    private static final SurfaceRules.RuleSource LIGHT_BLUE = SurfaceRules.state(Blocks.CONCRETE.lightBlue().defaultBlockState());
    private static final SurfaceRules.RuleSource CYAN = SurfaceRules.state(Blocks.CONCRETE.cyan().defaultBlockState());
    private static final SurfaceRules.RuleSource GREEN = SurfaceRules.state(Blocks.CONCRETE.green().defaultBlockState());
    private static final SurfaceRules.RuleSource LIME_GREEN = SurfaceRules.state(Blocks.CONCRETE.lime().defaultBlockState());
    private static final SurfaceRules.RuleSource YELLOW = SurfaceRules.state(Blocks.CONCRETE.yellow().defaultBlockState());
    private static final SurfaceRules.RuleSource ORANGE = SurfaceRules.state(Blocks.CONCRETE.orange().defaultBlockState());
    private static final SurfaceRules.RuleSource RED = SurfaceRules.state(Blocks.CONCRETE.red().defaultBlockState());
    private static final SurfaceRules.RuleSource PINK = SurfaceRules.state(Blocks.CONCRETE.pink().defaultBlockState());
    private static final SurfaceRules.RuleSource PURPLE = SurfaceRules.state(Blocks.CONCRETE.purple().defaultBlockState());
    private static final SurfaceRules.RuleSource MAGENTA = SurfaceRules.state(Blocks.CONCRETE.magenta().defaultBlockState());
    private static final SurfaceRules.RuleSource BLACK = SurfaceRules.state(Blocks.CONCRETE.black().defaultBlockState());
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
