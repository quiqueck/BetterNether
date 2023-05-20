package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BNTaburet;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Taburet extends SimpleMaterialSlot<WoodenComplexMaterial> {
    public Taburet() {
        super("taburet");
    }

    public static void makeTaburetRecipe(ResourceLocation id, Block taburet, Block planks) {
        BCLRecipeBuilder.crafting(id, taburet)
                        .setShape("##", "II")
                        .addMaterial('#', planks)
                        .addMaterial('I', Items.STICK)
                        .setGroup("taburet")
                        .setCategory(RecipeCategory.DECORATIONS)
                        .build();
    }

    @Override
    protected @NotNull Block createBlock(
            WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
    ) {
        return new BNTaburet(parentMaterial.getBlock(WoodSlots.SLAB));
    }

    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
        Taburet.makeTaburetRecipe(id, parentMaterial.getBlock(suffix), parentMaterial.getBlock(WoodSlots.SLAB));
    }
}
