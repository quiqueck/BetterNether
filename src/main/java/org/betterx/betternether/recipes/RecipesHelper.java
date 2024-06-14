package org.betterx.betternether.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

public class RecipesHelper {
    private static final String[] SHAPE_ROOF = new String[]{"# #", "###", " # "};
    private static final String[] SHAPE_STAIR = new String[]{"#  ", "## ", "###"};
    private static final String[] SHAPE_SLAB = new String[]{"###"};
    private static final String[] SHAPE_BUTTON = new String[]{"#"};
    private static final String[] SHAPE_PLATE = new String[]{"##"};
    private static final String[] SHAPE_X2 = new String[]{"##", "##"};
    private static final String[] SHAPE_3X2 = new String[]{"###", "###"};
    private static final String[] SHAPE_COLORING = new String[]{"###", "#I#", "###"};
    private static final String[] SHAPE_ROUND = new String[]{"###", "# #", "###"};
    private static final String[] SHAPE_FIRE_BOWL = new String[]{"#I#", " # ", "L L"};

    public interface RecipeBuilder extends Consumer<org.betterx.wover.recipe.api.RecipeBuilder.Templates> {
    }

    private static final List<RecipeBuilder> RECIPES = new LinkedList<>();

    @ApiStatus.Internal
    public static void provideRecipes(org.betterx.wover.recipe.api.RecipeBuilder.Templates t) {
        RECIPES.forEach(r -> r.accept(t));
    }

    public static void addProvider(RecipeBuilder provider) {
        RECIPES.add(provider);
    }

    public static void makeRoofRecipe(Block source, Block roof) {
        RECIPES.add((t -> t.makeRoofRecipe(source, roof)));
    }

    public static void makeStairsRecipe(Block source, Block stairs) {
        RECIPES.add((t -> t.makeStairsRecipe(source, stairs)));
    }

    public static void makeSlabRecipe(Block source, Block slab) {
        RECIPES.add((t -> t.makeSlabRecipe(source, slab)));
    }

    public static void makeButtonRecipe(Block source, Block button) {
        RECIPES.add((t -> t.makeButtonRecipe(source, button)));
    }

    public static void makePlateRecipe(Block source, Block plate) {
        RECIPES.add((t -> t.makePlateRecipe(source, plate)));
    }

    public static void makeSimpleRecipe2(Block source, Block result, int count, String group, RecipeCategory category) {
        RECIPES.add((t -> t.makeSimpleRecipe2x2(source, result, count, group, category)));
    }

    public static void makeWallRecipe(Block source, Block wall) {
        RECIPES.add((t -> t.makeWallRecipe(source, wall)));
    }

    public static void makeColoringRecipe(Block source, Block result, Item dye, String group, RecipeCategory category) {
        RECIPES.add((t -> t.makeColoringRecipe(source, result, dye, group, category)));
    }

    public static void makeRoundRecipe(Block source, Block result, String group, RecipeCategory category) {
        RECIPES.add((t -> t.makeRoundRecipe(source, result, group, category)));
    }

    public static void makeFireBowlRecipe(Block material, Block inside, Item leg, Block result) {
        RECIPES.add((t -> t.makeFireBowlRecipe(material, inside, leg, result)));
    }
}