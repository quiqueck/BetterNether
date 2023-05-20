package org.betterx.datagen.betternether;

import org.betterx.bclib.api.v3.datagen.TagDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetherItemTagDataProvider extends TagDataProvider<Item> {

    public NetherItemTagDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(TagManager.ITEMS, List.of("minecraft", BetterNether.MOD_ID, "c"), output, registriesFuture);
    }
}
