package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.BN;
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

public class VanillaHammersRecipes extends WoverRecipeProvider {
    public VanillaHammersRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Vanilla Hammers Recipes");
    }

    private static void makeHammerRecipe(RecipeOutput context, Item hammer, Block block, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(hammer);

        RecipeBuilder
                .crafting(BetterNether.C.mk(id.getPath()), hammer)
                .shape("#I#", " S ", " S ")
                .addMaterial('#', block)
                .addMaterial('I', item)
                .addMaterial('S', NetherBlocks.MAT_REED.getStem())
                .category(RecipeCategory.TOOLS)
                .build(context);

    }

    @Override
    protected void bootstrap(HolderLookup.Provider provider, RecipeOutput context) {
        makeHammerRecipe(context, NetherItems.CINCINNASITE_HAMMER, NetherBlocks.CINCINNASITE_FORGED, NetherItems.CINCINNASITE_INGOT);
        makeHammerRecipe(context, NetherItems.NETHER_RUBY_HAMMER, NetherBlocks.NETHER_RUBY_BLOCK, NetherItems.NETHER_RUBY);

        RecipeBuilder
                .crafting(BN.id("cincinnasite_hammer_diamond"), NetherItems.CINCINNASITE_HAMMER_DIAMOND)
                .shape("#I#")
                .addMaterial('#', Items.DIAMOND)
                .addMaterial('I', NetherItems.CINCINNASITE_HAMMER)
                .category(RecipeCategory.TOOLS)
                .build(context);

    }
}
