package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.blocks.BaseStairsBlock;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.recipes.BCLRecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RoofStairs extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public RoofStairs() {
        super("roof_stairs");
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BaseStairsBlock(parentMaterial.getBlock(NetherSlots.ROOF), true);
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        BCLRecipeBuilder
                .crafting(id, parentMaterial.getBlock(suffix))
                .setOutputCount(4)
                .setShape("#  ", "## ", "###")
                .addMaterial('#', parentMaterial.getBlock(NetherSlots.ROOF))
                .setGroup("stairs")
                .setCategory(RecipeCategory.BUILDING_BLOCKS)
                .build();
    }
}