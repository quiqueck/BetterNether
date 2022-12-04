package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.datagen.betternether.presets.FlatLevelPresetsDataProvider;
import org.betterx.datagen.betternether.worldgen.*;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetterNetherDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {

        BCLBiomeRegistry.prepareForDatagen();
        NetherBiomesDataProvider.ensureStaticallyLoaded();

        final FabricDataGenerator.Pack pack = dataGenerator.createPack();
        pack.addProvider(WorldgenRegistriesDataProvider::new);
        pack.addProvider(NetherBiomesDataProvider::new);
        pack.addProvider(BCLibRegistriesDataProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.BIOME, NetherBiomesDataProvider::bootstrap);
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureDataProvider::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, PlacedFeatureDataProvider::bootstrap);
        registryBuilder.add(Registries.STRUCTURE, StructureDataProvider::bootstrap);
        registryBuilder.add(Registries.STRUCTURE_SET, StructureDataProvider::bootstrapSets);
        registryBuilder.add(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelPresetsDataProvider::bootstrap);
    }
}
