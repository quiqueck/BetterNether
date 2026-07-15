package org.betterx.betternether.blocks.complex.slots;

import org.betterx.betternether.blocks.BlockStem;
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
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

/**
 * The thin axis-aligned "stem" of a Nether tree. Four stems craft into a log.
 */
public class Stem extends SlotFromDefinition {
    public static final Stem SLOT = new Stem();

    protected Stem() {
        super(NetherSlots.STEM);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, def -> new BlockStem(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.strength(0.5f).noOcclusion().addTags(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                .crafting(key.location().withSuffix("_to_log"), set.getBlock(SlotType.LOG))
                .outputCount(1)
                .shape("##", "##")
                .addMaterial('#', set.recipeMaterial(NetherSlots.STEM))
                .group("logs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.pillar();
    }
}
