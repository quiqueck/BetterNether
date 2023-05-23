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
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherBiomesDataProvider::new);
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherRegistriesDataProvider::new);
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherRecipeDataProvider::new);
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherAdvancementDataProvider::new);
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherBlockTagDataProvider::new);
        NetherRegistrySupplier.INSTANCE.addProviderWithLock(pack, NetherItemTagDataProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        NetherRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);
        NetherRegistrySupplier.INSTANCE.addWithLock(
                registryBuilder,
                Registries.BIOME,
                NetherBiomesDataProvider::bootstrap
        );
    }
}
