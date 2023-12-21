package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v3.datagen.RegistrySupplier;
import org.betterx.betternether.BetterNether;
import org.betterx.datagen.betternether.worldgen.features.ConfiguredTerrainFeatureDataProvider;
import org.betterx.datagen.betternether.worldgen.features.PlacedTerrainFeatureDataProvider;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class NetherRegistrySupplier extends RegistrySupplier {
    public static final NetherRegistrySupplier INSTANCE = new NetherRegistrySupplier();

    protected NetherRegistrySupplier() {
        super(List.of(BetterNether.C.modId));
    }

    @Override
    protected List<RegistryInfo<?>> initializeRegistryList(@Nullable List<String> modIDs) {
        return List.of(
                new RegistryInfo<>(
                        Registries.CONFIGURED_FEATURE,
                        ConfiguredFeature.DIRECT_CODEC,
                        ConfiguredTerrainFeatureDataProvider::bootstrapLegacy
                ),
                new RegistryInfo<>(
                        Registries.PLACED_FEATURE,
                        PlacedFeature.DIRECT_CODEC,
                        PlacedTerrainFeatureDataProvider::bootstrapLegacy
                )
        );
    }


    @Override
    public void bootstrapRegistries(RegistrySetBuilder registryBuilder) {
        super.bootstrapRegistries(registryBuilder);
    }
}
