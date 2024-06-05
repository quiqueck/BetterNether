package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.api.v3.datagen.BlockLootTableProvider;
import org.betterx.betternether.BetterNether;

import net.minecraft.core.HolderLookup;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetherBlockLootTableProvider extends BlockLootTableProvider {
    public NetherBlockLootTableProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup, List.of(BetterNether.C.modId));
    }
}
