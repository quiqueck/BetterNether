package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeData;
import org.betterx.bclib.api.v3.datagen.RegistriesDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.surfaceRules.AssignedSurfaceRule;
import org.betterx.worlds.together.surfaceRules.SurfaceRuleRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class BCLibRegistriesDataProvider extends RegistriesDataProvider {
    public BCLibRegistriesDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(BetterNether.LOGGER, List.of(BetterNether.MOD_ID), output, registriesFuture);
    }

    @Override
    protected List<RegistryInfo<?>> initializeRegistryList(@Nullable List<String> modIDs) {
        return List.of(
                new RegistryInfo<>(BCLBiomeRegistry.BCL_BIOMES_REGISTRY, BiomeData.CODEC),
                new RegistryInfo<>(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY, AssignedSurfaceRule.CODEC),
                new RegistryInfo<>(Registries.STRUCTURE, Structure.DIRECT_CODEC),
                new RegistryInfo<>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC),
                new RegistryInfo<>(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
                new RegistryInfo<>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
                new RegistryInfo<>(Registries.BIOME, Biome.DIRECT_CODEC),
                new RegistryInfo<>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC)
        );
    }
}
