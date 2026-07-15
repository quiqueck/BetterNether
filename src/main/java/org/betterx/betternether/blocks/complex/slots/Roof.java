package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.data.recipes.RecipeCategory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A full "roof" block crafted from planks; a plain wood cube (behaviour supplied by the set's
 * {@code WOOD_BLOCK} trait via {@code NetherWoodenMaterial#addCommonBlockDefinitions}).
 */
public class Roof extends SlotFromDefinition {
    public static final Roof SLOT = new Roof();

    private Roof() {
        super(NetherSlots.ROOF);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, org.betterx.wover.block.api.BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                .crafting(key.location(), block)
                .outputCount(4)
                .shape("# #", "###", " # ")
                .addMaterial('#', set.recipeMaterial(SlotType.PLANKS))
                .group("planks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
