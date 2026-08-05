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
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.item.NetherResourceItems;

public class NetherOreBlocks {

    // Cincinnasite //
    // Former Materials.stone(COLOR_RED) preset (+ the ctor's own strength()/sound() overrides), inlined at
    // the registration site (WP3.6). ORE_BLOCK.dropping(...) already supplies instrument(BASEDRUM)/
    // requiresCorrectToolForDrops()/the pickaxe tag - per R7 only the delta (mapColor, plus strength(3,5),
    // which overrides ORE_BLOCK's own strength(3,9)) is added here. WP8.3 (REVIEWED): sound aligned to
    // NETHER_ORE (was NETHERRACK) - vanilla's own nether ores (nether_quartz_ore) use a dedicated
    // NETHER_ORE sound rather than the plain terrain-break sound.
    public static final Block CINCINNASITE_ORE = NetherBlocks.defineBlock("cincinnasite_ore", p -> new BlockOre(p, 0, true))
            .addTrait(NetherMaterial.ore(() -> NetherResourceItems.CINCINNASITE, 1, 3))
            .mapColor(MapColor.COLOR_RED)
            .strength(3.0f, 5.0f)
            .sound(SoundType.NETHER_ORE)
            .addTags(ToolTiers.IRON_TOOL.blockTag)
            .buildAndRegister();
    // Ruby //
    // See CINCINNASITE_ORE above for why only these three setters are needed (R7).
    public static final Block NETHER_RUBY_ORE = NetherBlocks.defineBlock("nether_ruby_ore", p -> new BlockOre(p, 5, true))
            .addTrait(NetherMaterial.ore(() -> NetherResourceItems.NETHER_RUBY, 1, 2))
            .mapColor(MapColor.COLOR_RED)
            .strength(3.0f, 5.0f)
            .sound(SoundType.NETHER_ORE)
            .addTags(ToolTiers.DIAMOND_TOOL.blockTag)
            .buildAndRegister();
    // Vanilla Ores
    // See CINCINNASITE_ORE above for why only these three setters are needed (R7).
    public static final Block NETHER_LAPIS_ORE = NetherBlocks.defineBlock("nether_lapis_ore", p -> new BlockOre(p, 3, false))
            .addTrait(NetherMaterial.ore(() -> NetherResourceItems.LAPIS_PILE, 3, 6))
            .mapColor(MapColor.COLOR_RED)
            .strength(3.0f, 5.0f)
            .sound(SoundType.NETHER_ORE)
            .addTags(ToolTiers.IRON_TOOL.blockTag)
            .buildAndRegister();
    // Former Materials.stone(COLOR_RED) preset (+ the ctor's own strength()/sound()/randomTicks()/
    // lightLevel() overrides), inlined at the registration site (WP3.6).
    public static final Block NETHER_REDSTONE_ORE = NetherBlocks.defineBlock("nether_redstone_ore", RedstoneOreBlock::new)
            .addTrait(NetherMaterial.ore(() -> Items.REDSTONE, 1, 3))
            .mapColor(MapColor.COLOR_RED)
            .strength(3.0f, 5.0f)
            .sound(SoundType.NETHER_ORE)
            .addTags(ToolTiers.IRON_TOOL.blockTag)
            .randomTicks()
            .lightLevel(RedstoneOreBlock.litBlockEmission(9))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
