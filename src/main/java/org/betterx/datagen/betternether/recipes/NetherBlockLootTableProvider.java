package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.api.v3.datagen.BlockLootTableProvider;
import org.betterx.betternether.BetterNether;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;

public class NetherBlockLootTableProvider extends BlockLootTableProvider {
    public NetherBlockLootTableProvider(
            FabricDataOutput output
    ) {
        super(output, List.of(BetterNether.MOD_ID));
    }
}
