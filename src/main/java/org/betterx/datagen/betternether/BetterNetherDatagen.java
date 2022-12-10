package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.datagen.betternether.worldgen.NetherAdvancementDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherRegistriesDataProvider;
import org.betterx.datagen.betternether.worldgen.WorldgenRegistriesDataProvider;

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
        pack.addProvider(NetherRegistriesDataProvider::new);
        pack.addProvider(NetherAdvancementDataProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        NetherRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);
        registryBuilder.add(Registries.BIOME, NetherBiomesDataProvider::bootstrap);
    }
}
