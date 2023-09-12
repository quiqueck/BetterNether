package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.SoundsRegistry;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class GravelDesert extends NetherBiomeConfig {
    public static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());


    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(170, 48, 0)
               .mood(SoundEvents.AMBIENT_NETHER_WASTES_MOOD)
               .loop(SoundsRegistry.AMBIENT_GRAVEL_DESERT)
               .additions(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS)
               .music(SoundEvents.MUSIC_BIOME_NETHER_WASTES)
               .particles(ParticleTypes.ASH, 0.02F)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherVegetationPlaced.NETHER_CACTUS)
               .feature(NetherVegetationPlaced.VEGETATION_GRAVEL_DESERT)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(0.5f, -0.7f, 0)
        ;
    }


    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);

        builder.ceil(Blocks.NETHERRACK.defaultBlockState())
               .floor(Blocks.GRAVEL.defaultBlockState())
               .rule(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, GRAVEL), BaseSurfaceRuleBuilder.FLOOR_PRIORITY + 1)
               .belowFloor(Blocks.GRAVEL.defaultBlockState(), 4, Conditions.NETHER_VOLUME_NOISE)
        ;
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case NAGA -> res = 20;
        }
        return res;
    }
}
