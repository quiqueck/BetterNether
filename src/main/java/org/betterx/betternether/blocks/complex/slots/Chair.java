package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BNNormalChair;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Chair extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public Chair() {
        super("chair");
    }

    public static void makeChairRecipe(ResourceLocation id, Block chair, Block planks) {
        BCLRecipeBuilder.crafting(id, chair)
                        .setShape("I ", "##", "II")
                        .addMaterial('#', planks)
                        .addMaterial('I', Items.STICK)
                        .setGroup("chair")
                        .setCategory(RecipeCategory.DECORATIONS)
                        .build();
    }

    @Override
    protected void modifyBlockEntry(WoodenComplexMaterial parentMaterial, @NotNull BlockEntry entry) {
        entry.setBlockTags(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BNNormalChair(parentMaterial.getBlock(WoodSlots.SLAB));
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        Chair.makeChairRecipe(id, parentMaterial.getBlock(suffix), parentMaterial.getBlock(WoodSlots.SLAB));
    }
}
