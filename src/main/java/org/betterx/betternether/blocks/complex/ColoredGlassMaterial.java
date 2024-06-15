package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BNGlass;
import org.betterx.betternether.blocks.BNPane;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ColoredGlassMaterial {
    public final Block white;
    public final Block orange;
    public final Block magenta;
    public final Block light_blue;
    public final Block yellow;
    public final Block lime;
    public final Block pink;
    public final Block gray;
    public final Block light_gray;
    public final Block cyan;
    public final Block purple;
    public final Block blue;
    public final Block brown;
    public final Block green;
    public final Block red;
    public final Block black;

    /**
     * Full Block Constructor
     *
     * @param name - base name of block (prefix) and it's group
     * @param base - block base for material properties and crafting
     */
    public <T extends Block> ColoredGlassMaterial(String name, Block base) {
        white = makeInstance(name, base, Items.WHITE_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        orange = makeInstance(name, base, Items.ORANGE_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        magenta = makeInstance(name, base, Items.MAGENTA_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        light_blue = makeInstance(name, base, Items.LIGHT_BLUE_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        yellow = makeInstance(name, base, Items.YELLOW_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        lime = makeInstance(name, base, Items.LIME_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        pink = makeInstance(name, base, Items.PINK_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        gray = makeInstance(name, base, Items.GRAY_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        light_gray = makeInstance(name, base, Items.LIGHT_GRAY_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        cyan = makeInstance(name, base, Items.CYAN_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        purple = makeInstance(name, base, Items.PURPLE_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        blue = makeInstance(name, base, Items.BLUE_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        brown = makeInstance(name, base, Items.BROWN_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        green = makeInstance(name, base, Items.GREEN_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        red = makeInstance(name, base, Items.RED_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
        black = makeInstance(name, base, Items.BLACK_DYE, true, false, RecipeCategory.BUILDING_BLOCKS);
    }

    /**
     * Pane Block Constructor
     *
     * @param name           - base name of block (prefix) and it's group
     * @param base           - block base for material properties and crafting
     * @param paneDropItself - will pane drop itself on break or not (will require silk
     *                       touch)
     */
    public <T extends Block> ColoredGlassMaterial(String name, Block base, boolean paneDropItself) {
        white = makeInstance(name, base, Items.WHITE_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        orange = makeInstance(name, base, Items.ORANGE_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        magenta = makeInstance(name, base, Items.MAGENTA_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        light_blue = makeInstance(name, base, Items.LIGHT_BLUE_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        yellow = makeInstance(name, base, Items.YELLOW_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        lime = makeInstance(name, base, Items.LIME_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        pink = makeInstance(name, base, Items.PINK_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        gray = makeInstance(name, base, Items.GRAY_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        light_gray = makeInstance(name, base, Items.LIGHT_GRAY_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        cyan = makeInstance(name, base, Items.CYAN_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        purple = makeInstance(name, base, Items.PURPLE_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        blue = makeInstance(name, base, Items.BLUE_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        brown = makeInstance(name, base, Items.BROWN_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        green = makeInstance(name, base, Items.GREEN_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        red = makeInstance(name, base, Items.RED_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
        black = makeInstance(name, base, Items.BLACK_DYE, false, paneDropItself, RecipeCategory.DECORATIONS);
    }

    private Block makeInstance(
            String group,
            Block base,
            Item dye,
            boolean isFullBlock,
            boolean paneDropItself,
            RecipeCategory category
    ) {
        Block block = isFullBlock ? new BNGlass(base) : BNPane.from(base, paneDropItself);
        String name = group + "_" + ((DyeItem) dye).getDyeColor().getSerializedName();

        NetherBlocks.registerBlockDirectly(name, block);
        if (ModCore.isDatagen())
            RecipesHelper.makeColoringRecipe(base, block, dye, group, category);

        return block;
    }
}