package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.SlabBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

/**
 * Slab cut from the {@link Roof} block. Registered after {@link Roof} (see {@code RoofMaterial}).
 */
public class RoofSlab extends SlotFromDefinition {
    public static final RoofSlab SLOT = new RoofSlab();

    private RoofSlab() {
        super(NetherSlots.ROOF_SLAB);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, def -> new SlabBlock(def.getProperties()));
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                .crafting(key.location(), block)
                .outputCount(6)
                .shape("###")
                .addMaterial('#', set.recipeMaterial(NetherSlots.ROOF))
                .group("slabs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.slab(() -> set.getBlock(NetherSlots.ROOF));
    }
}
