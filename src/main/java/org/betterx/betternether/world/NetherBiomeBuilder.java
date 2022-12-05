package org.betterx.betternether.world;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeBuilder;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.worlds.together.world.event.WorldBootstrap;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.ArrayList;
import java.util.List;

public class NetherBiomeBuilder {
    public static boolean useLegacyGeneration = false;
    static final SurfaceRules.RuleSource BEDROCK = SurfaceRules.state(Blocks.BEDROCK.defaultBlockState());
    //(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove)
    static final SurfaceRules.VerticalGradientConditionSource BEDROCK_BOTTOM =
            new SurfaceRules.VerticalGradientConditionSource(
                    BetterNether.makeID("bedrock_floor"),
                    VerticalAnchor.bottom(),
                    VerticalAnchor.aboveBottom(5)
            );
    static final SurfaceRules.VerticalGradientConditionSource BEDROCK_TOP =
            new SurfaceRules.VerticalGradientConditionSource(
                    BetterNether.makeID("bedrock_roof"),
                    VerticalAnchor.belowTop(5),
                    VerticalAnchor.top()
            );

    private static void addVanillaStructures(BCLBiomeBuilder builder) {
        builder.carver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE);
        builder.structure(BiomeTags.HAS_RUINED_PORTAL_NETHER);
    }

    private static void addVanillaFeatures(BCLBiomeBuilder builder) {
        builder
                .feature(GenerationStep.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA)
                .defaultMushrooms()
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA)
                .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED);
    }

    private static void addBNStructures(BCLBiomeBuilder builder) {
        builder.structure(NetherStructures.PYRAMIDS);
        builder.structure(NetherStructures.GHAST_HIVE);
        builder.structure(NetherTags.BETTER_NETHER_DECORATIONS);
    }

    public static NetherBiome create(NetherBiomeConfig data) {
        return create(data, null);
    }

    public static NetherBiome create(NetherBiomeConfig data, BCLBiome edgeBiome) {
        return create(data, null, edgeBiome);
    }

    public static NetherBiome createSubBiome(NetherBiomeConfig data, BCLBiome parentBiome) {
        return create(data, parentBiome, null);
    }

    private static NetherBiome create(NetherBiomeConfig data, BCLBiome parentBiome, BCLBiome edgeBiome) {
        final ResourceLocation ID = data.ID;

        BCLBiomeBuilder builder = BCLBiomeBuilder
                .start(ID)
                //.category(BiomeCategory.NETHER)
                .surface(data.surface().build())
                .tag(NetherTags.BETTER_NETHER)
                .temperature(BCLBiomeBuilder.DEFAULT_NETHER_TEMPERATURE)
                .wetness(BCLBiomeBuilder.DEFAULT_NETHER_WETNESS)
                .precipitation(Precipitation.NONE)
                .waterColor(BCLBiomeBuilder.DEFAULT_NETHER_WATER_COLOR)
                .waterFogColor(BCLBiomeBuilder.DEFAULT_NETHER_WATER_FOG_COLOR)
                .skyColor(BCLBiomeBuilder.calculateSkyColor(BCLBiomeBuilder.DEFAULT_NETHER_TEMPERATURE))
                .music(SoundEvents.MUSIC_BIOME_NETHER_WASTES)
                .mood(SoundEvents.AMBIENT_NETHER_WASTES_MOOD)
                .loop(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
                .additions(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS)
                .edge(edgeBiome)
                .parentBiome(parentBiome)
                .netherBiome()
                .feature(NetherVegetationPlaced.WART_CAP);


        if (data.hasVanillaStructures()) addVanillaStructures(builder);
        if (data.hasBNStructures()) addBNStructures(builder);
        if (data.hasVanillaFeatures()) addVanillaFeatures(builder);
        if (data.hasVanillaOres()) builder.netherDefaultOres();

        if (data.hasBNFeatures()) NetherFeatures.addDefaultBNFeatures(builder);

        for (NetherEntities.KnownSpawnTypes spawnType : NetherEntities.KnownSpawnTypes.values()) {
            spawnType.addSpawn(builder, data);
        }

        NetherFeatures.addDefaultFeatures(builder);

        if (data.hasDefaultOres()) NetherFeatures.addDefaultOres(builder);
        if (data.hasNetherCity()) builder.structure(NetherStructures.CITY_STRUCTURE);


        data.addCustomBuildData(builder);

        return builder.build(data.getSupplier()).biome();
    }

    public static List<BCLBiome> getAllBnBiomes() {
        List<BCLBiome> res = new ArrayList<>();
        var access = WorldBootstrap.getLastRegistryAccess();
        Registry<BCLBiome> reg;
        if (access == null) reg = BCLBiomeRegistry.BUILTIN_BCL_BIOMES;
        else reg = access.registryOrThrow(BCLBiomeRegistry.BCL_BIOMES_REGISTRY);

        for (var e : reg.entrySet()) {
            if (e.getValue().getIntendedType().equals(BiomeAPI.BiomeType.NETHER)) {
                res.add(e.getValue());
            }
        }

        return res;
    }
}
