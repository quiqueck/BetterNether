package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import org.betterx.bclib.furniture.block.BaseBarStool;
import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.bclib.furniture.block.BaseTaburet;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.blocks.complex.*;
import org.betterx.betternether.blocks.complex.slots.VanillaNetherWood;
import org.betterx.betternether.blocks.complex.slots.VanillaWood;
import org.betterx.betternether.registry.features.configured.NetherVines;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Recipe-trait factories shared across the block registry-split category classes (WP7.6-7.8).
 * Relocated verbatim from {@code NetherBlocks} per docs/registry-split-map.md section 4.
 */
public class NetherRecipeTraits {


    /**
     * The reverse-crafting recipe {@code registerStalactite} used to queue via {@code RecipesHelper}: a 2x2
     * square of the produced stalactite crafts back into 1 {@code source} block, in the
     * {@code nether_stalactite} recipe-book group. Expressed as a {@link BlockTraits#RECIPE} trait (like
     * BCLib's {@code RecipeTraits}) so it stays visible at each registration site instead of hidden behind
     * a helper; {@code BlockTraits.RECIPE.with(...)} is itself a no-op outside datagen.
     */
    public static BlockTrait<?, ?> stalactiteRecipe(Block source) {
        return BlockTraits.RECIPE.with((key, block, context) -> new RecipeBuilder.Templates(context, BetterNether.C)
                .makeSimpleRecipe2x2(block, source, 1, "nether_stalactite", RecipeCategory.DECORATIONS));
    }


    /**
     * The "craft {@code count} out of a 2x2 of {@code source}" recipe {@code registerMakeable2X2}/
     * {@code registerMakeable2X2Soul} used to queue via {@code RecipesHelper.makeSimpleRecipe2}. Like
     * {@link #stalactiteRecipe(Block)}, expressed as a {@link BlockTraits#RECIPE} trait so it stays visible
     * at each registration site (no BCLib {@code RecipeTraits} equivalent exists for this shape).
     */
    public static BlockTrait<?, ?> simple2x2Recipe(Block source, int count, String group, RecipeCategory category) {
        return BlockTraits.RECIPE.with((key, block, context) -> new RecipeBuilder.Templates(context, BetterNether.C)
                .makeSimpleRecipe2x2(source, block, count, group, category));
    }


    /**
     * The "ring of source around the furnace" recipe {@code registerFurnace} used to queue via
     * {@code RecipesHelper.makeRoundRecipe}, in the {@code nether_furnace} recipe-book group. Like
     * {@link #stalactiteRecipe(Block)}, expressed as a {@link BlockTraits#RECIPE} trait so it stays visible
     * at each registration site (no BCLib {@code RecipeTraits} equivalent exists for this shape).
     */
    public static BlockTrait<?, ?> furnaceRecipe(Block source) {
        return BlockTraits.RECIPE.with((key, block, context) -> new RecipeBuilder.Templates(context, BetterNether.C)
                .makeRoundRecipe(source, block, "nether_furnace", RecipeCategory.DECORATIONS));
    }
}
