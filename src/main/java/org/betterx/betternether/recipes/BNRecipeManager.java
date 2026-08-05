package org.betterx.betternether.recipes;

import org.betterx.bclib.recipes.BCLRecipeManager;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class BNRecipeManager extends BCLRecipeManager {
    public static ShapelessRecipe makeEmptyRecipe(Identifier id) {
        ShapelessRecipe recipe = new ShapelessRecipe(
                new Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "empty"),
                new ItemStackTemplate(Items.AIR),
                List.of()
        );
        return recipe;
    }
}
