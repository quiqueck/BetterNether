package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.api.v3.datagen.RecipeDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.recipes.BlockRecipes;
import org.betterx.betternether.recipes.ItemRecipes;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;

public class NetherRecipeDataProvider extends RecipeDataProvider {
    public NetherRecipeDataProvider(FabricDataOutput output) {
        super(List.of(BetterNether.MOD_ID), output);
    }

    public static void buildRecipes() {
        BlockRecipes.register();
        ItemRecipes.register();
    }
}
