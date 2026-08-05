package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
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
        // See RoofStairs: SLAB_BLOCK.withDefault() restores #minecraft:slabs and carries the loot.
        def.addTrait(BlockTraits.SLAB_BLOCK.withDefault());
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
                .crafting(key.identifier(), block)
                .outputCount(6)
                .shape("###")
                .addMaterial('#', set.recipeMaterial(NetherSlots.ROOF))
                .group("slabs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        // hand-authored blockstate/model in src/main/resources (was an addMaterialOverrides .ignore())
        return ModelTraitLibrary.externalModel();
    }
}
