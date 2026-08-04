package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.GlassBlockTrait;
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
import net.minecraft.resources.ResourceLocation;
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

public class NetherObsidianBlocks {

    // Obsidian //
    // Former registerBlockDropSelf bundle, inlined at the registration site (WP5.12): the
    // BlockTraits.LOOT_TABLE.dropSelf() the helper baked in unconditionally, made visible.
    public static final Block BLUE_WEEPING_OBSIDIAN = NetherBlocks.defineBlock("blue_weeping_obsidian", BlueWeepingObsidianBlock::new)
            .replacePropertiesWithCopy(Blocks.CRYING_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(NetherModels.obsidianVariants())
            .addTrait(NetherMaterial.obsidian())
            .buildAndRegister();
    public static final Block WEEPING_OBSIDIAN = NetherBlocks.defineBlock("weeping_obsidian", VanillaWeepingObsidianBlock::new)
            .replacePropertiesWithCopy(Blocks.CRYING_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(NetherModels.obsidianVariants())
            .addTrait(NetherMaterial.obsidian())
            .buildAndRegister();
    public static final Block BLUE_CRYING_OBSIDIAN = NetherBlocks.defineBlock("blue_crying_obsidian", BlueCryingObsidianBlock::new)
            .replacePropertiesWithCopy(Blocks.CRYING_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(NetherModels.obsidianVariants())
            .addTrait(NetherMaterial.obsidian())
            .buildAndRegister();
    // Former registerObsidianCube bundle, inlined at the registration site (WP5.7): copy obsidian's
    // properties, self-drop loot, cube model, and the obsidian-portal-frame classification.
    public static final Block OBSIDIAN_BRICKS = NetherBlocks.defineBlock("obsidian_bricks", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block OBSIDIAN_BRICKS_STAIRS = NetherBlocks.defineBlock(
            "obsidian_bricks_stairs",
            p -> new StairBlock(OBSIDIAN_BRICKS.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(OBSIDIAN_BRICKS)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> OBSIDIAN_BRICKS))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.stairsFrom(OBSIDIAN_BRICKS))
            .buildAndRegister();
    public static final Block OBSIDIAN_BRICKS_SLAB = NetherBlocks.defineBlock("obsidian_bricks_slab", SlabBlock::new)
            .replacePropertiesWithCopy(OBSIDIAN_BRICKS)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> OBSIDIAN_BRICKS))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.slabFrom(OBSIDIAN_BRICKS))
            .buildAndRegister();
    public static final Block OBSIDIAN_TILE = NetherBlocks.defineBlock("obsidian_tile", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block OBSIDIAN_TILE_SMALL = NetherBlocks.defineBlock("obsidian_tile_small", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block OBSIDIAN_TILE_STAIRS = NetherBlocks.defineBlock(
            "obsidian_tile_stairs",
            p -> new StairBlock(OBSIDIAN_TILE_SMALL.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(OBSIDIAN_TILE_SMALL)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> OBSIDIAN_TILE_SMALL))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.stairsFrom(OBSIDIAN_TILE_SMALL))
            .buildAndRegister();
    public static final Block OBSIDIAN_TILE_SLAB = NetherBlocks.defineBlock("obsidian_tile_slab", SlabBlock::new)
            .replacePropertiesWithCopy(OBSIDIAN_TILE_SMALL)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> OBSIDIAN_TILE_SMALL))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.slabFrom(OBSIDIAN_TILE_SMALL))
            .buildAndRegister();
    public static final Block OBSIDIAN_ROD_TILES = NetherBlocks.defineBlock("obsidian_rod_tiles", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    // BlockObsidianGlass (WP6.12): noOcclusion()/isSuffocating(false)/isViewBlocking(false) moved out of
    // its constructor (R1); it always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // Those three calls were hand-rolled here and, like BaseGlassBlock, missed isValidSpawn/
    // isRedstoneConductor - so this block inherited both from opaque Blocks.OBSIDIAN. GlassBlockTrait
    // carries the whole recipe; the no-resistance overload is deliberate, so obsidian's 1200 survives.
    public static final Block OBSIDIAN_GLASS = NetherBlocks.defineBlock("obsidian_glass", BlockObsidianGlass::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(GlassBlockTrait.glass())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BNPane's dropSelf=true forced a self-drop via getDrops(), unconditionally, with no committed loot
    // table json - reproduced explicitly (WP6.10) as NetherLoot.dropSelfNoExplosion().
    // BNPane.Glass dissolved to plain BNPane (WP6.14 sweep); strength(0.3, 0.3)/noOcclusion() moved out of
    // its constructor (R1) to here. class= changes Glass -> BNPane (CLASS-ONLY).
    public static final Block OBSIDIAN_GLASS_PANE = NetherBlocks.defineBlock("obsidian_glass_pane", p -> new BNPane(p))
            .replacePropertiesWithCopy(OBSIDIAN_GLASS)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.glass())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .strength(0.3F, 0.3F)
            .noOcclusion()
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN = NetherBlocks.defineBlock("blue_obsidian", p -> new BNObsidian(p, BLUE_CRYING_OBSIDIAN))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_BRICKS = NetherBlocks.defineBlock("blue_obsidian_bricks", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_BRICKS_STAIRS = NetherBlocks.defineBlock(
            "blue_obsidian_bricks_stairs",
            p -> new StairBlock(BLUE_OBSIDIAN_BRICKS.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(BLUE_OBSIDIAN_BRICKS)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> BLUE_OBSIDIAN_BRICKS))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.stairsFrom(BLUE_OBSIDIAN_BRICKS))
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_BRICKS_SLAB = NetherBlocks.defineBlock("blue_obsidian_bricks_slab", SlabBlock::new)
            .replacePropertiesWithCopy(BLUE_OBSIDIAN_BRICKS)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> BLUE_OBSIDIAN_BRICKS))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.slabFrom(BLUE_OBSIDIAN_BRICKS))
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_TILE = NetherBlocks.defineBlock("blue_obsidian_tile", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_TILE_SMALL = NetherBlocks.defineBlock("blue_obsidian_tile_small", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_TILE_STAIRS = NetherBlocks.defineBlock(
            "blue_obsidian_tile_stairs",
            p -> new StairBlock(BLUE_OBSIDIAN_TILE_SMALL.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(BLUE_OBSIDIAN_TILE_SMALL)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> BLUE_OBSIDIAN_TILE_SMALL))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.stairsFrom(BLUE_OBSIDIAN_TILE_SMALL))
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_TILE_SLAB = NetherBlocks.defineBlock("blue_obsidian_tile_slab", SlabBlock::new)
            .replacePropertiesWithCopy(BLUE_OBSIDIAN_TILE_SMALL)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> BLUE_OBSIDIAN_TILE_SMALL))
            .addTrait(NetherMaterial.obsidian())
            .addTrait(RecipeTraits.slabFrom(BLUE_OBSIDIAN_TILE_SMALL))
            .buildAndRegister();
    public static final Block BLUE_OBSIDIAN_ROD_TILES = NetherBlocks.defineBlock("blue_obsidian_rod_tiles", p -> new BNObsidian(p, null))
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .buildAndRegister();
    // BlockObsidianGlass (WP6.12): same reproduction as OBSIDIAN_GLASS above, GlassBlockTrait included.
    public static final Block BLUE_OBSIDIAN_GLASS = NetherBlocks.defineBlock("blue_obsidian_glass", BlockObsidianGlass::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.obsidianPortalFrame())
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(GlassBlockTrait.glass())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BNPane's dropSelf=true forced a self-drop via getDrops(), unconditionally, with no committed loot
    // table json - reproduced explicitly (WP6.10) as NetherLoot.dropSelfNoExplosion().
    // BNPane.Glass dissolved to plain BNPane (WP6.14 sweep); strength(0.3, 0.3)/noOcclusion() moved out of
    // its constructor (R1) to here. class= changes Glass -> BNPane (CLASS-ONLY).
    public static final Block BLUE_OBSIDIAN_GLASS_PANE = NetherBlocks.defineBlock("blue_obsidian_glass_pane", p -> new BNPane(p))
            .replacePropertiesWithCopy(BLUE_OBSIDIAN_GLASS)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.glass())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .strength(0.3F, 0.3F)
            .noOcclusion()
            .buildAndRegister();

    public static void ensureLoaded() {}
}
