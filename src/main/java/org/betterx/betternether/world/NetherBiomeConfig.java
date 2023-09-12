package org.betterx.betternether.world;

import org.betterx.betternether.registry.NetherEntities.KnownSpawnTypes;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public abstract class NetherBiomeConfig {
    public static final SurfaceRules.RuleSource NETHERRACK = SurfaceRules.state(Blocks.NETHERRACK.defaultBlockState());
    public static final SurfaceRules.RuleSource BEDROCK_BOTTOM = SurfaceRules.ifTrue(
            NetherBiomeBuilder.BEDROCK_BOTTOM,
            NetherBiomeBuilder.BEDROCK
    );
    public static final SurfaceRules.RuleSource BEDROCK_TOP = SurfaceRules.ifTrue(
            SurfaceRules.not(NetherBiomeBuilder.BEDROCK_TOP),
            NetherBiomeBuilder.BEDROCK
    );

    protected NetherBiomeConfig() {
    }

    public ResourceKey<Biome> subBiomeOf() {
        return null;
    }

    public ResourceKey<Biome> withEdgeBiome() {
        return null;
    }

    public boolean hasVanillaFeatures() {
        return true;
    }

    public boolean hasVanillaOres() {
        return true;
    }

    public boolean hasStalactites() {
        return false;
    }

    public boolean hasDefaultOres() {
        return true;
    }

    public boolean hasNetherCity() {
        return true;
    }

    public boolean hasVanillaStructures() {
        return true;
    }

    public boolean hasBNStructures() {
        return true;
    }

    public boolean hasBNFeatures() {
        return true;
    }


    public <M extends Mob> int spawnWeight(KnownSpawnTypes type) {
        int res = type.weight;
        switch (type) {
            case JUNGLE_SKELETON, FLYING_PIG, HOGLIN, PIGLIN_BRUTE -> res = 0;
        }
        return res;
    }

    public abstract void addCustomBuildData(NetherBiomeBuilder builder);

    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        builder.rule(BEDROCK_TOP, BaseSurfaceRuleBuilder.STEEP_SURFACE_PRIORITY + 2)
               .rule(BEDROCK_BOTTOM, BaseSurfaceRuleBuilder.STEEP_SURFACE_PRIORITY + 1)
               .rule(NETHERRACK, BaseSurfaceRuleBuilder.FILLER_PRIORITY - 1);
    }
}
