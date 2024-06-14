package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.blocks.BaseSlabBlock;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RoofSlab extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public RoofSlab() {
        super("roof_slab");
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BaseSlabBlock.Wood(parentMaterial.getBlock(NetherSlots.ROOF), false);
    }


    @Override
    protected @Nullable void makeRecipe(RecipeOutput context, ComplexMaterial parentMaterial, ResourceLocation id) {
        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder
                .crafting(id, parentMaterial.getBlock(suffix));
        CraftingRecipeBuilder craftingRecipeBuilder2 = craftingRecipeBuilder1.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder2.shape("###")
                                                                            .addMaterial('#', parentMaterial.getBlock(NetherSlots.ROOF));
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder = craftingRecipeBuilder.group("slabs");
        craftingRecipeBuilderBaseRecipeBuilder.category(RecipeCategory.BUILDING_BLOCKS)
                                              .build(context);
    }
}