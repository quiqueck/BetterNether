package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class VanillaExcavatorsRecipes extends WoverRecipeProvider {
    public VanillaExcavatorsRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Vanilla Excavatprs Recipes");
    }

    private static void makeExcavatorRecipe(RecipeBuilder.Context context, Item excavator, Block block, Item item) {
        final ResourceLocation id = BuiltInRegistries.ITEM.getKey(excavator);
        RecipeBuilder
                .crafting(BetterNether.C.id(id.getPath()), excavator)
                .shape(" I ", "#S#", " S ")
                .addMaterial('#', block)
                .addMaterial('I', item)
                .addMaterial('S', NetherWoodBlocks.MAT_REED.getStem())
                .category(RecipeCategory.TOOLS)
                .build(context);
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        makeExcavatorRecipe(
                context,
                NetherEquipmentItems.CINCINNASITE_EXCAVATOR,
                NetherMetalBlocks.CINCINNASITE_FORGED,
                NetherResourceItems.CINCINNASITE_INGOT
        );
        makeExcavatorRecipe(
                context,
                NetherEquipmentItems.NETHER_RUBY_EXCAVATOR,
                NetherMetalBlocks.NETHER_RUBY_BLOCK,
                NetherResourceItems.NETHER_RUBY
        );

        RecipeBuilder
                .crafting(BetterNether.C.id("cincinnasite_excavator_diamond"), NetherEquipmentItems.CINCINNASITE_EXCAVATOR_DIAMOND)
                .shape("#I#")
                .addMaterial('#', Items.DIAMOND)
                .addMaterial('I', NetherEquipmentItems.CINCINNASITE_EXCAVATOR)
                .category(RecipeCategory.TOOLS)
                .build(context);
    }
}
