package org.betterx.betternether.world.biomes;

import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class OldWarpedWoods extends NetherBiomeConfig {
    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(26, 5, 26)
               .loop(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
               .additions(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS)
               .mood(SoundEvents.AMBIENT_WARPED_FOREST_MOOD)
               .particles(ParticleTypes.WARPED_SPORE, 0.025F)
               .structure(BiomeTags.HAS_BASTION_REMNANT)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .feature(NetherFeatures.NETHER_RUBY_ORE)
               .feature(NetherTreesPlaced.BIG_WARPED_TREE)
               .feature(NetherObjectsPlaced.STALAGMITE)
               .feature(NetherVegetationPlaced.VEGETATION_OLD_WARPED_WOODS)
               .feature(NetherVinesPlaced.BLACK_VINE)
               .feature(NetherVinesPlaced.TWISTING_VINES)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(0.0f, 0.55f, 0.375f)
        ;
    }

    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case GHAST, ZOMBIFIED_PIGLIN, MAGMA_CUBE, PIGLIN, HOGLIN, PIGLIN_BRUTE -> res = 0;
            case ENDERMAN, STRIDER -> res = type.weight;
        }
        return res;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        builder.floor(Blocks.WARPED_NYLIUM.defaultBlockState());
    }

    @Override
    public ResourceKey<Biome> subBiomeOf() {
        return Biomes.WARPED_FOREST;
    }
}