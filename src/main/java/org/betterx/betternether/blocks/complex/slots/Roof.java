package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.data.recipes.RecipeCategory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;

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
    protected void addSlotSpecificDefinitions(BlockSet<?> set, de.ambertation.wover.block.api.BlockDefinition<?, ?> def) {
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
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
