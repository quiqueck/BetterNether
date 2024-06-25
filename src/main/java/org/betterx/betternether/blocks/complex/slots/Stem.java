package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockStem;
import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
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
        return new RecipeEntry(WoodSlots.LOG.suffix + "_" + suffix, (ctx, parentMaterial1, id) -> makeRecipe(ctx, parentMaterial1, id));
    }

    @Override
    protected void modifyBlockEntry(
            WoodenComplexMaterial parentMaterial,
            @NotNull BlockEntry entry
    ) {
        entry.setBlockTags(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    protected @Nullable void makeRecipe(RecipeOutput context, ComplexMaterial parentMaterial, ResourceLocation id) {
        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder
                .crafting(id, parentMaterial.getBlock(WoodSlots.LOG));
        CraftingRecipeBuilder craftingRecipeBuilder2 = craftingRecipeBuilder1.outputCount(1);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder2.shape("##", "##")
                                                                            .addMaterial('#', parentMaterial.getBlock(suffix));
        BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder = craftingRecipeBuilder.group("planks");
        craftingRecipeBuilderBaseRecipeBuilder.category(RecipeCategory.BUILDING_BLOCKS)
                                              .build(context);
    }
}
