package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.api.v3.datagen.RecipeDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.recipes.BlockRecipes;
import org.betterx.betternether.recipes.ItemRecipes;

import net.minecraft.core.HolderLookup;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetherRecipeDataProvider extends RecipeDataProvider {
    public NetherRecipeDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(List.of(BetterNether.C.modId), output, registryLookup);
    }

    public static void buildRecipes() {
        BlockRecipes.register();
        ItemRecipes.register();
    }
}
