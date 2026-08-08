package org.betterx.datagen.betternether;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.datagen.betternether.advancements.NetherAdvancementDataProvider;
import org.betterx.datagen.betternether.enchantments.NetherEnchantmentProvider;
import org.betterx.datagen.betternether.enchantments.NetherEnchantmentTagProvider;
import org.betterx.datagen.betternether.entity.NetherEntityTypeTagProvider;
import org.betterx.datagen.betternether.presets.FlatLevelPresetsDataProvider;
import org.betterx.datagen.betternether.recipes.*;
import org.betterx.datagen.betternether.worldgen.NetherBiomeModificationProvider;
import org.betterx.datagen.betternether.worldgen.NetherBiomesProvider;
import org.betterx.datagen.betternether.worldgen.StructureDataProvider;
import org.betterx.datagen.betternether.worldgen.features.*;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.api.provider.BlockPropertiesProvider;
import de.ambertation.wover.datagen.api.provider.WoverBlockRegistrationsProvider;
import de.ambertation.wover.datagen.api.provider.WoverBlockShapesProvider;
import de.ambertation.wover.datagen.api.provider.WoverEquipmentAssetProvider;
import de.ambertation.wover.datagen.api.provider.WoverItemRegistrationsProvider;
import de.ambertation.wover.pottable.api.datagen.WoverPottablePlantRegistryProvider;
import de.ambertation.wover.pottable.api.datagen.WoverPottableSoilRegistryProvider;

import net.minecraft.core.RegistrySetBuilder;

public class BetterNetherDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        NetherItems.register();
        NetherBlocks.register();

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
        // No BetterNether blocks have vanilla-namespace counterparts to add by hand, unlike
        // BetterEnd's EndPottablePlantProvider/EndPottableSoilProvider - the generic trait-driven
        // providers are enough on their own.
        globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new);
        globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new);
        globalPack.addProvider(NetherChestLootTableProvider::new);
        globalPack.addProvider(NetherEntityLootTableProvider::new);
        globalPack.addProvider(NetherLootAdditionProvider::new);
        globalPack.addProvider(NetherEnchantmentProvider::new);
        globalPack.addProvider(NetherBlockTagDataProvider::new);
        globalPack.addProvider(NetherItemTagDataProvider::new);
        globalPack.addProvider(NetherEnchantmentTagProvider::new);
        globalPack.addProvider(NetherEntityTypeTagProvider::new);
        globalPack.addProvider(NetherModelProvider::new);
        globalPack.addProvider(WoverEquipmentAssetProvider::new);
        globalPack.addProvider(NetherBlockRecipesProvider::new);
        globalPack.addProvider(NetherItemRecipeProvider::new);
        globalPack.addProvider(NetherCraftingRecipes::new);
        globalPack.addProvider(BlockPropertiesProvider::new);
        globalPack.addProvider(WoverBlockRegistrationsProvider::new);
        globalPack.addProvider(WoverItemRegistrationsProvider::new);
        globalPack.addProvider(WoverBlockShapesProvider::new);


        globalPack.callOnInitializeDatapack((generator, pack, location) -> {
            if (location == null) {
                pack.addProvider(NetherAdvancementDataProvider::new);
                pack.addProvider(NetherBlockLootTableProvider::new);
            }
        });

        //Add providers for the vanilla hammers extension
        addDatapack(BetterNether.VANILLA_HAMMERS_PACK)
                .addProvider(VanillaHammersRecipes::new);

        //Add providers for the vanilla excavators extension
        addDatapack(BetterNether.VANILLA_EXCAVATORS_PACK)
                .addProvider(VanillaExcavatorsRecipes::new);
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
