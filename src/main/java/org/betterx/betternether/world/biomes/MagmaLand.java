package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.api.Conditions;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

;

public class MagmaLand extends NetherBiomeConfig {

    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(248, 158, 68)
               .loop(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
               .additions(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS)
               .mood(SoundEvents.AMBIENT_NETHER_WASTES_MOOD)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherTerrainPlaced.LAVA_PITS_DENSE)
               .feature(NetherTerrainPlaced.MAGMA_BLOBS)
               .feature(NetherObjectsPlaced.OBSIDIAN_CRYSTAL)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.BLACK_BUSH)
               .feature(NetherVegetationPlaced.VEGETATION_MAGMA_LAND)
               .feature(NetherVinesPlaced.GOLDEN_VINE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(0.8f, -0.7f, 0)
        ;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.chancedFloor(
                Blocks.MAGMA_BLOCK.defaultBlockState(),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                Conditions.NETHER_VOLUME_NOISE,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.hole(),
                                                SurfaceRules.state(Blocks.NETHERRACK.defaultBlockState())
                                        ),
                                        SurfaceRules.state(Blocks.RED_SAND.defaultBlockState())
                                )
                        ),
                        SurfaceRules.state(Blocks.NETHERRACK.defaultBlockState())
                ),
                org.betterx.wover.surface.api.Conditions.NETHER_SURFACE_NOISE
        );
    }
}
