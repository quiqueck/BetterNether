package org.betterx.betternether;

import org.betterx.worlds.together.surfaceRules.SurfaceRuleRegistry;

import net.minecraft.data.BuiltinRegistries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBuiltinRegistriesProvider;

public class BetterNetherDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        BetterNether.LOGGER.info(SurfaceRuleRegistry.BUILTIN_SURFACE_RULES.toString());
        BetterNether.LOGGER.info(BuiltinRegistries.BIOME.toString());
        //NetherBiomesDatagen.registerForDatagen();
        final FabricDataGenerator.Pack pack = dataGenerator.createPack();
        pack.addProvider(FabricBuiltinRegistriesProvider.forCurrentMod());
    }
}
