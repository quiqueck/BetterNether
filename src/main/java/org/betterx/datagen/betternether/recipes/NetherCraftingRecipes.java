package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.BCLib;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class NetherCraftingRecipes extends WoverRecipeProvider {
    public NetherCraftingRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Crafting Recipes");
    }

    @Override
    protected void bootstrap(RecipeOutput context) {
        CraftingRecipeBuilder craftingRecipeBuilder11 = RecipeBuilder.crafting(BCLib.makeID("tag_smith_table"), Blocks.SMITHING_TABLE);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder10 = craftingRecipeBuilder11
                .shape("II", "##", "##")
                .addMaterial('#', ItemTags.PLANKS)
                .addMaterial('I', CommonItemTags.IRON_INGOTS);
        craftingRecipeBuilderBaseRecipeBuilder10.category(RecipeCategory.DECORATIONS)
                                                .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder10 = RecipeBuilder.crafting(BCLib.makeID("tag_cauldron"), Blocks.CAULDRON);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder9 = craftingRecipeBuilder10
                .shape("I I", "I I", "III")
                .addMaterial('I', CommonItemTags.IRON_INGOTS);
        craftingRecipeBuilderBaseRecipeBuilder9.category(RecipeCategory.BREWING)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder9 = RecipeBuilder.crafting(BCLib.makeID("tag_hopper"), Blocks.HOPPER);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder8 = craftingRecipeBuilder9
                .shape("I I", "ICI", " I ")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('C', CommonItemTags.CHEST);
        craftingRecipeBuilderBaseRecipeBuilder8.category(RecipeCategory.REDSTONE)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder8 = RecipeBuilder.crafting(BCLib.makeID("tag_piston"), Blocks.PISTON);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder7 = craftingRecipeBuilder8
                .shape("WWW", "CIC", "CDC")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('D', Items.REDSTONE)
                .addMaterial('C', Items.COBBLESTONE)
                .addMaterial('W', ItemTags.PLANKS);
        craftingRecipeBuilderBaseRecipeBuilder7.category(RecipeCategory.REDSTONE)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder = RecipeBuilder.crafting(BCLib.makeID("tag_rail"), Blocks.RAIL);
        CraftingRecipeBuilder craftingRecipeBuilder7 = craftingRecipeBuilder
                .outputCount(16);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder6 = craftingRecipeBuilder7
                .shape("I I", "ISI", "I I")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('S', Items.STICK);
        craftingRecipeBuilderBaseRecipeBuilder6.category(RecipeCategory.TRANSPORTATION)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder6 = RecipeBuilder.crafting(BCLib.makeID("tag_stonecutter"), Blocks.STONECUTTER);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder5 = craftingRecipeBuilder6
                .shape(" I ", "SSS")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('S', Items.STONE);
        craftingRecipeBuilderBaseRecipeBuilder5.category(RecipeCategory.DECORATIONS)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder5 = RecipeBuilder.crafting(BCLib.makeID("tag_compass"), Items.COMPASS);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder4 = craftingRecipeBuilder5
                .shape(" I ", "IDI", " I ")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('D', Items.REDSTONE);
        craftingRecipeBuilderBaseRecipeBuilder4.category(RecipeCategory.TOOLS)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder4 = RecipeBuilder.crafting(BCLib.makeID("tag_bucket"), Items.BUCKET);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder3 = craftingRecipeBuilder4
                .shape("I I", " I ")
                .addMaterial('I', CommonItemTags.IRON_INGOTS);
        craftingRecipeBuilderBaseRecipeBuilder3.category(RecipeCategory.MISC)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder3 = RecipeBuilder.crafting(BCLib.makeID("tag_minecart"), Items.MINECART);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder2 = craftingRecipeBuilder3
                .shape("I I", "III")
                .addMaterial('I', CommonItemTags.IRON_INGOTS);
        craftingRecipeBuilderBaseRecipeBuilder2.category(RecipeCategory.TRANSPORTATION)
                                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder2 = RecipeBuilder.crafting(BCLib.makeID("tag_shield"), Items.SHIELD);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder1 = craftingRecipeBuilder2
                .shape("WIW", "WWW", " W ")
                .addMaterial('I', CommonItemTags.IRON_INGOTS)
                .addMaterial('W', ItemTags.PLANKS);
        craftingRecipeBuilderBaseRecipeBuilder1.category(RecipeCategory.COMBAT)
                                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder.crafting(BCLib.makeID("tag_shulker_box"), Blocks.SHULKER_BOX);
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder = craftingRecipeBuilder1
                .shape("S", "C", "S")
                .addMaterial('S', Items.SHULKER_SHELL)
                .addMaterial('C', CommonItemTags.CHEST);
        craftingRecipeBuilderBaseRecipeBuilder.category(RecipeCategory.DECORATIONS)
                                              .build(context);
    }
}
