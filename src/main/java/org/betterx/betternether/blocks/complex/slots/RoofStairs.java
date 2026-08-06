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
import net.minecraft.world.level.block.StairBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

/**
 * Stairs cut from the {@link Roof} block. Must be registered after {@link Roof} in the set's slot order
 * (see {@code RoofMaterial#createDefaultDefinitions}) so the roof block already exists.
 */
public class RoofStairs extends SlotFromDefinition {
    public static final RoofStairs SLOT = new RoofStairs();

    private RoofStairs() {
        super(NetherSlots.ROOF_STAIRS);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // STAIR_BLOCK.withDefault() carries #minecraft:stairs + item tag AND its own LOOT_TABLE.dropSelf(),
        // so it replaces the bare loot trait and restores the stairs tag these roof pieces lost.
        def.addTrait(BlockTraits.STAIR_BLOCK.withDefault());
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name,
                def -> new StairBlock(set.getBlock(NetherSlots.ROOF).defaultBlockState(), def.getProperties())
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                .crafting(key.identifier(), block)
                .outputCount(4)
                .shape("#  ", "## ", "###")
                .addMaterial('#', set.recipeMaterial(NetherSlots.ROOF))
                .group("stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.stairs(() -> set.getBlock(NetherSlots.ROOF));
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a stepped block), so it must not inherit the set material's sulfur cube
        // archetype - a cube renders what it swallowed as a block model, and a stairs inside one reads as a
        // bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
