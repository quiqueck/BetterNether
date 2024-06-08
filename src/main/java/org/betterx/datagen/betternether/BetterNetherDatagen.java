package org.betterx.datagen.betternether;

import org.betterx.betternether.BetterNether;
import org.betterx.datagen.betternether.advancements.NetherAdvancementDataProvider;
import org.betterx.datagen.betternether.enchantments.NetherEnchantmentProvider;
import org.betterx.datagen.betternether.enchantments.NetherEnchantmentTagProvider;
import org.betterx.datagen.betternether.entity.NetherEntityTypeTagProvider;
import org.betterx.datagen.betternether.presets.FlatLevelPresetsDataProvider;
import org.betterx.datagen.betternether.recipes.NetherBlockLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherChestLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherEntityLootTableProvider;
import org.betterx.datagen.betternether.recipes.NetherRecipeDataProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomeModificationProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesProvider;
import org.betterx.datagen.betternether.worldgen.StructureDataProvider;
import org.betterx.datagen.betternether.worldgen.features.*;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

import net.minecraft.core.RegistrySetBuilder;

public class BetterNetherDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        NetherRecipeDataProvider.buildRecipes();

        globalPack.addMultiProvider(NetherBiomesProvider::new);
        globalPack.addMultiProvider(ObjectFeatureDataProvider::new);
        globalPack.addMultiProvider(OreFeatureDataProvider::new);
        globalPack.addMultiProvider(TerrainFeatureDataProvider::new);
        globalPack.addMultiProvider(PlacedTreeFeatureDataProvider::new);
        globalPack.addMultiProvider(VegetationFeatureDataProvider::new);
        globalPack.addMultiProvider(VineFeatureDataProvider::new);
        globalPack.addMultiProvider(StructureDataProvider::new);
        globalPack.addRegistryProvider(NetherBiomeModificationProvider::new);
        globalPack.addRegistryProvider(FlatLevelPresetsDataProvider::new);
        globalPack.addProvider(NetherChestLootTableProvider::new);
        globalPack.addProvider(NetherEntityLootTableProvider::new);
        globalPack.addProvider(NetherEnchantmentProvider::new);
        globalPack.addProvider(NetherBlockTagDataProvider::new);
        globalPack.addProvider(NetherItemTagDataProvider::new);
        globalPack.addProvider(NetherEnchantmentTagProvider::new);
        globalPack.addProvider(NetherEntityTypeTagProvider::new);

        globalPack.callOnInitializeDatapack((generator, pack, location) -> {
            if (location == null) {
                pack.addProvider(NetherRecipeDataProvider::new);
                pack.addProvider(NetherAdvancementDataProvider::new);
                pack.addProvider(NetherBlockLootTableProvider::new);
            }
        });

    }

    @Override
    protected void onBuildRegistry(RegistrySetBuilder registryBuilder) {
        super.onBuildRegistry(registryBuilder);
    }

    @Override
    protected ModCore modCore() {
        return BetterNether.C;
    }
}
