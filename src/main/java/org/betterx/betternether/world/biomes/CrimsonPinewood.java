package org.betterx.betternether.world.biomes;

import org.betterx.betternether.noise.OpenSimplexNoise;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class CrimsonPinewood extends NetherBiomeConfig {
    private static final OpenSimplexNoise TERRAIN = new OpenSimplexNoise(614);

    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(51, 3, 3)
               .loop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
               .music(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)
               .particles(ParticleTypes.CRIMSON_SPORE, 0.025F)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.CRIMSON_PINE)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.WART_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_CRIMSON_GLOWING_WOODS)
               .feature(NetherVinesPlaced.GOLDEN_VINE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .feature(NetherVegetationPlaced.WALL_MUSHROOM_RED_WITH_MOSS)
               .addNetherClimate(0.35f, 0.1f, 0.0f)
               .genChance(0.3f)
        ;
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case HOGLIN, FLYING_PIG -> res = type.weight;
            case NAGA -> res = 0;
        }
        return res;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.chancedFloor(
                Blocks.NETHER_WART_BLOCK.defaultBlockState(),
                Blocks.CRIMSON_NYLIUM.defaultBlockState(),
                Conditions.FORREST_FLOOR_SURFACE_NOISE_B
        );
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return Biomes.CRIMSON_FOREST;
    }
}
