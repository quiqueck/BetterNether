package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class VanillaExcavatorsRecipes extends WoverRecipeProvider {
    public VanillaExcavatorsRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Vanilla Excavatprs Recipes");
    }

    private static void makeExcavatorRecipe(RecipeOutput context, Item excavator, Block block, Item item) {
        final ResourceLocation id = BuiltInRegistries.ITEM.getKey(excavator);
        RecipeBuilder
                .crafting(BetterNether.C.id(id.getPath()), excavator)
                .shape(" I ", "#S#", " S ")
                .addMaterial('#', block)
                .addMaterial('I', item)
                .addMaterial('S', NetherBlocks.MAT_REED.getStem())
                .category(RecipeCategory.TOOLS)
                .build(context);
    }

    @Override
    protected void bootstrap(HolderLookup.Provider provider, RecipeOutput context) {
        makeExcavatorRecipe(
                context,
                NetherItems.CINCINNASITE_EXCAVATOR,
                NetherBlocks.CINCINNASITE_FORGED,
                NetherItems.CINCINNASITE_INGOT
        );
        makeExcavatorRecipe(
                context,
                NetherItems.NETHER_RUBY_EXCAVATOR,
                NetherBlocks.NETHER_RUBY_BLOCK,
                NetherItems.NETHER_RUBY
        );

        RecipeBuilder
                .crafting(BetterNether.C.id("cincinnasite_excavator_diamond"), NetherItems.CINCINNASITE_EXCAVATOR_DIAMOND)
                .shape("#I#")
                .addMaterial('#', Items.DIAMOND)
                .addMaterial('I', NetherItems.CINCINNASITE_EXCAVATOR)
                .category(RecipeCategory.TOOLS)
                .build(context);
    }
}
