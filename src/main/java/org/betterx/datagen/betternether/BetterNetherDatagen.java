package org.betterx.datagen.betternether;

import org.betterx.datagen.betternether.advancements.NetherAdvancementDataProvider;
import org.betterx.datagen.betternether.recipes.NetherRecipeDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherRegistriesDataProvider;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetterNetherDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        NetherBiomesDataProvider.ensureStaticallyLoaded();
        NetherRecipeDataProvider.buildRecipes();

        final FabricDataGenerator.Pack pack = dataGenerator.createPack();
        pack.addProvider(NetherBiomesDataProvider::new);
        pack.addProvider(NetherRegistriesDataProvider::new);
        pack.addProvider(NetherRecipeDataProvider::new);
        pack.addProvider(NetherAdvancementDataProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        NetherRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);
        registryBuilder.add(Registries.BIOME, NetherBiomesDataProvider::bootstrap);
    }
}
