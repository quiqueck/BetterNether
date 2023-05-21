package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BlockStem;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Stem extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public Stem() {
        super("stem");
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BlockStem(parentMaterial.woodColor);
    }

    protected @Nullable RecipeEntry getRecipeEntry(WoodenComplexMaterial parentMaterial) {
        return new RecipeEntry(WoodSlots.LOG.suffix + "_" + suffix, this::makeRecipe);
    }

    @Override
    protected void modifyBlockEntry(WoodenComplexMaterial parentMaterial, @NotNull BlockEntry entry) {
        entry.setBlockTags(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        BCLRecipeBuilder
                .crafting(id, parentMaterial.getBlock(WoodSlots.LOG))
                .setOutputCount(1)
                .setShape("##", "##")
                .addMaterial('#', parentMaterial.getBlock(suffix))
                .setGroup("planks")
                .setCategory(RecipeCategory.BUILDING_BLOCKS)
                .build();
    }
}
