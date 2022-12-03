package org.betterx.datagen.betternether;

import org.betterx.datagen.betternether.worldgen.ConfiguredFeatureDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesDataProvider;
import org.betterx.datagen.betternether.worldgen.WorldgenRegistriesDataProvider;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetterNetherDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        final FabricDataGenerator.Pack pack = dataGenerator.createPack();

        pack.addProvider(WorldgenRegistriesDataProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.BIOME, NetherBiomesDataProvider::bootstrap);
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureDataProvider::bootstrap);
    }
}
