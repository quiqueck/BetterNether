package org.betterx.betternether.world.biomes;


import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.providers.NetherGrasslandsNumericProvider;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class FloodedDeltas extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(104, 95, 112)
               .loop(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
               .additions(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS)
               .mood(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD)
               .music(SoundEvents.MUSIC_BIOME_BASALT_DELTAS)
               .particles(ParticleTypes.WHITE_ASH, 0.12F)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherTerrainPlaced.FLOODED_LAVA_PIT)
               .feature(NetherObjectsPlaced.BASALT_STALAGMITE)
               .feature(NetherObjectsPlaced.BLACKSTONE_STALAGMITE)
               .feature(NetherObjectsPlaced.BASALT_STALACTITE)
               .feature(NetherObjectsPlaced.BLACKSTONE_STALACTITE)
               .addNetherClimate(-0.5f, -0.1f, 0.175f)
               .genChance(0.3f)
        ;
    }

    @Override
    public boolean hasNetherCity() {
        return false;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);

        builder.ceil(Blocks.DEEPSLATE.defaultBlockState())
               .rule(
                       new SwitchRuleSource(
                               NetherGrasslandsNumericProvider.DEFAULT,
                               List.of(
                                       SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState()),
                                       SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState())
                               )
                       ),
                       BaseSurfaceRuleBuilder.FLOOR_PRIORITY
               );
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return Biomes.BASALT_DELTAS;
    }
}
