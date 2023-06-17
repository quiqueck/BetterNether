package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Roof extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public Roof() {
        super("roof");
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BaseBlock.Wood(FabricBlockSettings.copyOf(parentMaterial.getBlock(
                WoodSlots.PLANKS)));
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        BCLRecipeBuilder
                .crafting(id, parentMaterial.getBlock(suffix))
                .setOutputCount(4)
                .setShape("# #", "###", " # ")
                .addMaterial('#', parentMaterial.getBlock(WoodSlots.PLANKS))
                .setGroup("planks")
                .setCategory(RecipeCategory.BUILDING_BLOCKS)
                .build();
    }
}
