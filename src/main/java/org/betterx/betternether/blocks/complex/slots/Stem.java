package org.betterx.betternether.blocks.complex.slots;

import org.betterx.betternether.blocks.BlockStem;
import org.betterx.betternether.blocks.NetherLoot;
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
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.block.Block;

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
        // BlockStem (WP6.12): reparented off BlockBaseNotFull, whose dead canSuffocate/isSimpleFullBlock/
        // allowsSpawning overrides are gone. Every stem always dropped itself unconditionally via
        // BlockBase's inherited getDrops() override (no loot table json was generated for any of them -
        // StalagnateMaterial/NetherMushroomMaterial/MushroomFirMaterial each add a Stem), reproduced
        // explicitly as NetherLoot.dropSelfNoExplosion() here so all three stay in sync.
        def.strength(0.5f).noOcclusion().addTags(BlockTags.MINEABLE_WITH_AXE).addTrait(NetherLoot.dropSelfNoExplosion());
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
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.pillar();
    }
}
