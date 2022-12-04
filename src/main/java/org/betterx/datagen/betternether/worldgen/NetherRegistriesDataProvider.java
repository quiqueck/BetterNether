package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v3.datagen.RegistriesDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.datagen.betternether.NetherRegistrySupplier;

import net.minecraft.core.HolderLookup;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.concurrent.CompletableFuture;

public class NetherRegistriesDataProvider extends RegistriesDataProvider {
    public NetherRegistriesDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(BetterNether.LOGGER, NetherRegistrySupplier.INSTANCE, output, registriesFuture);
    }
}
