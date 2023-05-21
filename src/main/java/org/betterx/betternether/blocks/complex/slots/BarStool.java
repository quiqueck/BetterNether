package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BNBarStool;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarStool extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public BarStool() {
        super("bar_stool");
    }

    public static void makeBarStoolRecipe(ResourceLocation id, Block barStool, Block planks) {
        BCLRecipeBuilder.crafting(id, barStool)
                        .setShape("##", "II", "II")
                        .addMaterial('#', planks)
                        .addMaterial('I', Items.STICK)
                        .setGroup("bar_stool")
                        .setCategory(RecipeCategory.DECORATIONS)
                        .build();
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BNBarStool(parentMaterial.getBlock(WoodSlots.SLAB));
    }

    @Override
    protected void modifyBlockEntry(WoodenComplexMaterial parentMaterial, @NotNull BlockEntry entry) {
        entry.setBlockTags(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        BarStool.makeBarStoolRecipe(id, parentMaterial.getBlock(suffix), parentMaterial.getBlock(WoodSlots.SLAB));
    }
}
