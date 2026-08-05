package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class VanillaHammersRecipes extends WoverRecipeProvider {
    public VanillaHammersRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Vanilla Hammers Recipes");
    }

    private static void makeHammerRecipe(RecipeBuilder.Context context, Item hammer, Block block, Item item) {
        Identifier id = BuiltInRegistries.ITEM.getKey(hammer);

        RecipeBuilder
                .crafting(BetterNether.C.mk(id.getPath()), hammer)
                .shape("#I#", " S ", " S ")
                .addMaterial('#', block)
                .addMaterial('I', item)
                .addMaterial('S', NetherWoodBlocks.MAT_REED.getStem())
                .category(RecipeCategory.TOOLS)
                .build(context);

    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        makeHammerRecipe(context, NetherEquipmentItems.CINCINNASITE_HAMMER, NetherMetalBlocks.CINCINNASITE_FORGED, NetherResourceItems.CINCINNASITE_INGOT);
        makeHammerRecipe(context, NetherEquipmentItems.NETHER_RUBY_HAMMER, NetherMetalBlocks.NETHER_RUBY_BLOCK, NetherResourceItems.NETHER_RUBY);

        RecipeBuilder
                .crafting(BN.id("cincinnasite_hammer_diamond"), NetherEquipmentItems.CINCINNASITE_HAMMER_DIAMOND)
                .shape("#I#")
                .addMaterial('#', Items.DIAMOND)
                .addMaterial('I', NetherEquipmentItems.CINCINNASITE_HAMMER)
                .category(RecipeCategory.TOOLS)
                .build(context);

    }
}
