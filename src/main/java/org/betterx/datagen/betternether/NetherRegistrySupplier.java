package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeData;
import org.betterx.bclib.api.v3.datagen.RegistrySupplier;
import org.betterx.betternether.BetterNether;
import org.betterx.datagen.betternether.presets.FlatLevelPresetsDataProvider;
import org.betterx.datagen.betternether.worldgen.ConfiguredFeatureDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesDataProvider;
import org.betterx.datagen.betternether.worldgen.PlacedFeatureDataProvider;
import org.betterx.datagen.betternether.worldgen.StructureDataProvider;
import org.betterx.worlds.together.surfaceRules.AssignedSurfaceRule;
import org.betterx.worlds.together.surfaceRules.SurfaceRuleRegistry;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class NetherRegistrySupplier extends RegistrySupplier {
    public static final NetherRegistrySupplier INSTANCE = new NetherRegistrySupplier();

    protected NetherRegistrySupplier() {
        super(List.of(BetterNether.MOD_ID));
    }

    @Override
    protected List<RegistryInfo<?>> initializeRegistryList(@Nullable List<String> modIDs) {
        return List.of(
                new RegistryInfo<>(
                        BCLBiomeRegistry.BCL_BIOMES_REGISTRY,
                        BiomeData.CODEC,
                        NetherBiomesDataProvider::bootstrapBCL
                ),
                new RegistryInfo<>(
                        SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                        AssignedSurfaceRule.CODEC
                ),
                new RegistryInfo<>(
                        Registries.STRUCTURE,
                        Structure.DIRECT_CODEC, StructureDataProvider::bootstrap
                ),
                new RegistryInfo<>(
                        Registries.STRUCTURE_SET,
                        StructureSet.DIRECT_CODEC,
                        StructureDataProvider::bootstrapSets
                ),
                new RegistryInfo<>(
                        Registries.CONFIGURED_FEATURE,
                        ConfiguredFeature.DIRECT_CODEC,
                        ConfiguredFeatureDataProvider::bootstrap
                ),
                new RegistryInfo<>(
                        Registries.PLACED_FEATURE,
                        PlacedFeature.DIRECT_CODEC,
                        PlacedFeatureDataProvider::bootstrap
                ),
                new RegistryInfo<>(
                        Registries.BIOME,
                        Biome.DIRECT_CODEC,
                        NetherBiomesDataProvider::bootstrap
                ),
                new RegistryInfo<>(
                        Registries.FLAT_LEVEL_GENERATOR_PRESET,
                        FlatLevelGeneratorPreset.DIRECT_CODEC,
                        FlatLevelPresetsDataProvider::bootstrap
                )
        );
    }


    @Override
    public void bootstrapRegistries(RegistrySetBuilder registryBuilder) {
        super.bootstrapRegistries(registryBuilder);
    }
}
