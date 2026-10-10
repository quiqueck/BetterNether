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
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.util.List;

public class NetherGrasslands extends NetherBiomeConfig {
    static final MaterialRule SOUL_SOIL = MaterialRules.state(Blocks.SOUL_SOIL.defaultBlockState());
    static final MaterialRule SOUL_SAND = MaterialRules.state(Blocks.SOUL_SAND.defaultBlockState());
    static final MaterialRule MOSS = MaterialRules.state(NetherTerrainBlocks.NETHERRACK_MOSS.defaultBlockState());

    // 26.3 folded the dyed blocks into ColorCollection records, so Blocks.BLUE_CONCRETE is now
    // Blocks.CONCRETE.blue(). Note LIME_CONCRETE maps onto .lime(); the local constant keeps its
    // old LIME_GREEN name so the commented-out palette below still reads.
    private static final MaterialRule BLUE = MaterialRules.state(Blocks.CONCRETE.blue().defaultBlockState());
    private static final MaterialRule LIGHT_BLUE = MaterialRules.state(Blocks.CONCRETE.lightBlue().defaultBlockState());
    private static final MaterialRule CYAN = MaterialRules.state(Blocks.CONCRETE.cyan().defaultBlockState());
    private static final MaterialRule GREEN = MaterialRules.state(Blocks.CONCRETE.green().defaultBlockState());
    private static final MaterialRule LIME_GREEN = MaterialRules.state(Blocks.CONCRETE.lime().defaultBlockState());
    private static final MaterialRule YELLOW = MaterialRules.state(Blocks.CONCRETE.yellow().defaultBlockState());
    private static final MaterialRule ORANGE = MaterialRules.state(Blocks.CONCRETE.orange().defaultBlockState());
    private static final MaterialRule RED = MaterialRules.state(Blocks.CONCRETE.red().defaultBlockState());
    private static final MaterialRule PINK = MaterialRules.state(Blocks.CONCRETE.pink().defaultBlockState());
    private static final MaterialRule PURPLE = MaterialRules.state(Blocks.CONCRETE.purple().defaultBlockState());
    private static final MaterialRule MAGENTA = MaterialRules.state(Blocks.CONCRETE.magenta().defaultBlockState());
    private static final MaterialRule BLACK = MaterialRules.state(Blocks.CONCRETE.black().defaultBlockState());
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
                MaterialRules.sequence(
                        MaterialRules.ifTrue(
                                NetherBiomeBuilder.ON_FLOOR,
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
