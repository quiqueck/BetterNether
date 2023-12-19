package org.betterx.betternether.recipes;

import org.betterx.bclib.recipes.BCLRecipeManager;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class BNRecipeManager extends BCLRecipeManager {
    public static ShapelessRecipe makeEmptyRecipe(ResourceLocation id) {
        ShapelessRecipe recipe = new ShapelessRecipe(
                "empty",
                CraftingBookCategory.MISC,
                new ItemStack(Items.AIR),
                NonNullList.create()
        );
        return recipe;
    }
}