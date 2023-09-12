package org.betterx.datagen.betternether;

import org.betterx.betternether.BetterNether;
import org.betterx.datagen.betternether.advancements.NetherAdvancementDataProvider;
import org.betterx.datagen.betternether.recipes.NetherBlockLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherChestLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherEntityLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherRecipeDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesProvider;
import org.betterx.datagen.betternether.worldgen.NetherRegistriesDataProvider;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

import net.minecraft.core.RegistrySetBuilder;

public class BetterNetherDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        NetherRecipeDataProvider.buildRecipes();

        globalPack.addMultiProvider(NetherBiomesProvider::new);
        
        globalPack.callOnInitializeDatapack((generator, pack, location) -> {
            if (location == null) {
                pack.addProvider(NetherRegistriesDataProvider::new);
                pack.addProvider(NetherRecipeDataProvider::new);
                pack.addProvider(NetherAdvancementDataProvider::new);
                pack.addProvider(NetherBlockTagDataProvider::new);
                pack.addProvider(NetherItemTagDataProvider::new);
                pack.addProvider(NetherChestLootTableProvider::new);
                pack.addProvider(NetherEntityLootTableProvider::new);
                pack.addProvider(NetherBlockLootTableProvider::new);
            }
        });

    }

    @Override
    protected void onBuildRegistry(RegistrySetBuilder registryBuilder) {
        super.onBuildRegistry(registryBuilder);
        NetherRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);
    }

    @Override
    protected ModCore modCore() {
        return BetterNether.C;
    }
}
