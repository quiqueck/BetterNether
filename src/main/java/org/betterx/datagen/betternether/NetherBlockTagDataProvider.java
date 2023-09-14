package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v3.datagen.TagDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetherBlockTagDataProvider extends TagDataProvider<Block> {

    public NetherBlockTagDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(TagManager.BLOCKS, List.of(BetterNether.C.modId), output, registriesFuture);
    }
}
