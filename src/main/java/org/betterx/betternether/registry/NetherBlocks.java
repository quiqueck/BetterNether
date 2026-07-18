package org.betterx.betternether.registry;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import org.betterx.bclib.furniture.block.BaseBarStool;
import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.bclib.furniture.block.BaseTaburet;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.blocks.complex.*;
import org.betterx.betternether.blocks.complex.slots.VanillaNetherWood;
import org.betterx.betternether.blocks.complex.slots.VanillaWood;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.features.configured.NetherVines;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.complex.api.equipment.ToolTiers;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class NetherBlocks {
    public static final Block NETHER_BRICK_TILE_LARGE = registerBlock(
            "nether_brick_tile_large",
            Blocks.NETHER_BRICKS,
            NetherTraits.and(NetherMaterial.stone(), ModelTraitLibrary.cube()),
            BNNetherBrick::new
    );

    // Reed //
    public static final Block NETHER_REED_STEM = registerBlock(
            "nether_reed_stem",
            NetherTraits.and(
                    NetherRender.cutout(),
                    BlockTraits.MINEABLE_WITH.needsHoe(),
                    BlockTraits.MINEABLE_WITH.needsSword(),
                    WeightedCrossModelTrait.booleanDispatch(
                            BlockNetherReed.TOP,
                            List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/nether_reed_stem"))),
                            List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/reeds_top"))),
                            WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/nether_reed_stem"))
                    )
            ),
            BlockNetherReed::new
    );
    public static final NetherReedMaterial MAT_REED = new NetherReedMaterial().init();

    // Stalagnate //
    public static final StalagnateMaterial MAT_STALAGNATE = new StalagnateMaterial().init();

    // Willow //
    public static final WillowMaterial MAT_WILLOW = new WillowMaterial().init();
    public static final Block WILLOW_LEAVES = registerLeaves(
            "willow_leaves",
            MAT_WILLOW.getSapling(),
            p -> new BlockWillowLeaves(MAT_WILLOW.getSapling(), p)
    );

    // Wart //
    public static final WartMaterial MAT_WART = new WartMaterial(
            "wart",
            MapColor.COLOR_RED,
            MapColor.COLOR_RED
    ).init();
    // Cincinnasite //
    public static final Block CINCINNASITE_ORE = registerBlock(
            "cincinnasite_ore",
            BlockTraits.ORE_BLOCK.dropping(() -> NetherItems.CINCINNASITE, 1, 3),
            p -> new BlockOre(p, 0, true),
            ToolTiers.IRON_TOOL.blockTag
    );
    public static final Block CINCINNASITE_BLOCK = registerBlock("cincinnasite_block", NetherMaterial.cincinnasite(), BlockCincinnasite::new);
    public static final Block CINCINNASITE_FORGED = registerBlock("cincinnasite_forged", NetherTraits.and(NetherMaterial.cincinnasite(), ModelTraitLibrary.cube()), BlockCincinnasite::new);
    public static final Block CINCINNASITE_PILLAR = registerBlock(
            "cincinnasite_pillar",
            CINCINNASITE_BLOCK,
            NetherMaterial.cincinnasite(),
            BlockCincinnasitPillar::new
    );
    public static final Block CINCINNASITE_BRICKS = registerBlock("cincinnasite_bricks", NetherTraits.and(NetherMaterial.cincinnasite(), ModelTraitLibrary.cube()), BlockCincinnasite::new);
    public static final Block CINCINNASITE_BRICK_PLATE = registerBlock(
            "cincinnasite_brick_plate",
            NetherTraits.and(NetherMaterial.cincinnasite(), ModelTraitLibrary.cube()),
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_STAIRS = registerStairs("cincinnasite_stairs", CINCINNASITE_FORGED, false, NetherMaterial.cincinnasite());
    public static final Block CINCINNASITE_SLAB = registerSlab("cincinnasite_slab", CINCINNASITE_FORGED, false,
            NetherMaterial.cincinnasite(), ModelTraitLibrary.externalModel());
    public static final Block TABURET_CINCINNASITE = registerTaburet(
            "taburet_cincinnasite",
            CINCINNASITE_SLAB,
            NetherModels.taburetCincinnasite()
    );
    public static final Block CHAIR_CINCINNASITE = registerChair(
            "chair_cincinnasite",
            CINCINNASITE_SLAB,
            NetherModels.chairCincinnasite()
    );
    public static final Block BAR_STOOL_CINCINNASITE = registerBarStool(
            "bar_stool_cincinnasite",
            CINCINNASITE_SLAB,
            NetherModels.barStoolCincinnasite()
    );
    public static final Block CINCINNASITE_BUTTON = registerBlockDropSelf(
            "cincinnasite_button",
            CINCINNASITE_FORGED,
            NetherModels.button("block/cincinnasite_button"),
            NetherMaterial.cincinnasite(),
            p -> new net.minecraft.world.level.block.ButtonBlock(
                    BlockSetType.GOLD,
                    20,
                    p
            )
    );
    public static final Block CINCINNASITE_PLATE = registerPlate(
            "cincinnasite_plate",
            CINCINNASITE_FORGED,
            BlockSetType.GOLD,
            NetherModels.pressurePlate("block/cincinnasite_plate_up")
    );
    public static final Block CINCINNASITE_LANTERN = registerBlock(
            "cincinnasite_lantern",
            CINCINNASITE_BLOCK,
            NetherTraits.and(NetherMaterial.cincinnasite(), ModelTraitLibrary.cube()),
            BlockCincinnasiteLantern::new
    );
    public static final Block CINCINNASITE_TILE_LARGE = registerBlock(
            "cincinnasite_tile_large",
            NetherMaterial.cincinnasite(),
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_TILE_SMALL = registerBlock(
            "cincinnasite_tile_small",
            NetherMaterial.cincinnasite(),
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_CARVED = registerBlock("cincinnasite_carved", NetherMaterial.cincinnasite(), BlockCincinnasite::new);
    public static final Block CINCINNASITE_WALL = registerWall("cincinnasite_wall", CINCINNASITE_FORGED,
            NetherMaterial.cincinnasite(), ModelTraitLibrary.externalModel());
    public static final Block CINCINNASITE_BRICKS_PILLAR = registerBlock(
            "cincinnasite_bricks_pillar",
            CINCINNASITE_FORGED,
            // BNPillar no longer implements DropSelfLootProvider; the loot table it used to get from that
            // interface (a plain self-drop, no survives_explosion - the block is not explosion resistant) is
            // now carried explicitly as a trait so it stays byte-identical.
            NetherTraits.and(NetherMaterial.cincinnasite(), NetherLoot.dropSelfNoExplosion()),
            BNPillar.Metal::new
    );
    public static final Block CINCINNASITE_BARS = registerBlock(
            "cincinnasite_bars",
            CINCINNASITE_FORGED,
            NetherTraits.concat(NetherRender.translucent(), NetherMaterial.cincinnasite()),
            p -> new BNPane.Metal(p, true)
    );
    public static final Block CINCINNASITE_PEDESTAL = registerBlock(
            "cincinnasite_pedestal",
            CINCINNASITE_BLOCK,
            NetherMaterial.cincinnasite(),
            BlockCincinnasitePedestal::new
    );
    public static final Block CINCINNASITE_FRAME = registerBlock(
            "cincinnasite_frame",
            CINCINNASITE_BLOCK,
            NetherTraits.concat(NetherRender.cutout(), NetherMaterial.cincinnasite()),
            BlockCincinnasiteFrame::new
    );
    public static final Block CINCINNASITE_LANTERN_SMALL = registerBlock(
            "cincinnasite_lantern_small",
            CINCINNASITE_LANTERN,
            NetherTraits.concat(NetherRender.cutout(), NetherMaterial.cincinnasite()),
            BlockSmallLantern.Metal::new
    );
    // BNChain no longer implements DropSelfLootProvider; carry its plain self-drop (no survives_explosion)
    // as a trait so the table stays byte-identical.
    public static final Block CINCINNASITE_CHAIN = registerBlock("cincinnasite_chain", Blocks.CHAIN, NetherTraits.and(NetherTraits.concat(NetherRender.cutout(), NetherMaterial.cincinnasite()), NetherLoot.dropSelfNoExplosion()), BNChain::new);
    // Ruby //
    public static final Block NETHER_RUBY_ORE = registerBlock(
            "nether_ruby_ore",
            BlockTraits.ORE_BLOCK.dropping(() -> NetherItems.NETHER_RUBY, 1, 2),
            p -> new BlockOre(p, 5, true),
            ToolTiers.DIAMOND_TOOL.blockTag
    );
    public static final Block NETHER_RUBY_BLOCK = registerBlock(
            "nether_ruby_block",
            Blocks.DIAMOND_BLOCK,
            // Classify as STONE_BLOCK but keep the gem-block toughness (diamond-block 5/6) chained after the
            // trait, since STONE_BLOCK's material default (2/6) would soften it.
            NetherTraits.and(NetherMaterial.stone(), NetherProps.strength(5.0f, 6.0f), ModelTraitLibrary.cube()),
            BlockNetherRuby::new
    );
    public static final Block NETHER_RUBY_STAIRS = registerStairs("nether_ruby_stairs", NETHER_RUBY_BLOCK, true, NetherMaterial.metal());
    public static final Block NETHER_RUBY_SLAB = registerSlab("nether_ruby_slab", NETHER_RUBY_BLOCK, true,
            NetherMaterial.metal(), ModelTraitLibrary.externalModel());
    // Vanilla Ores
    public static final Block NETHER_LAPIS_ORE = registerBlock(
            "nether_lapis_ore",
            BlockTraits.ORE_BLOCK.dropping(() -> NetherItems.LAPIS_PILE, 3, 6),
            p -> new BlockOre(p, 3, false),
            ToolTiers.IRON_TOOL.blockTag
    );
    public static final Block NETHER_REDSTONE_ORE = registerBlock("nether_redstone_ore", BlockTraits.ORE_BLOCK.dropping(() -> Items.REDSTONE, 1, 3), RedstoneOreBlock::new);
    // Bricks //
    public static final Block NETHER_BRICK_TILE_SMALL = registerBlock(
            "nether_brick_tile_small",
            Blocks.NETHER_BRICKS,
            NetherTraits.and(NetherMaterial.stone(), ModelTraitLibrary.cube()),
            BNNetherBrick::new
    );
    public static final Block NETHER_BRICK_WALL = registerWall("nether_brick_wall", NETHER_BRICK_TILE_LARGE,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block NETHER_BRICK_TILE_SLAB = registerSlab(
            "nether_brick_tile_slab",
            NETHER_BRICK_TILE_SMALL,
            false,
            NetherMaterial.stone(),
            ModelTraitLibrary.externalModel()
    );
    public static final Block NETHER_BRICK_TILE_STAIRS = registerStairs(
            "nether_brick_tile_stairs",
            NETHER_BRICK_TILE_SMALL,
            false,
            NetherMaterial.stone()
    );
    // Bone //
    public static final Block BONE_BLOCK = registerBlock("bone_block", Blocks.BONE_BLOCK, NetherMaterial.stone(), BNBoneBlock::new);
    public static final Block BONE_STAIRS = registerStairs("bone_stairs", BONE_BLOCK, false, NetherMaterial.stone());
    public static final Block BONE_SLAB = registerSlab("bone_slab", BONE_BLOCK, false,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block BONE_BUTTON = registerButton(
            "bone_button",
            BONE_BLOCK,
            BlockSetType.CRIMSON,
            NetherMaterial.stone(),
            NetherModels.button("block/bone_button")
    );
    public static final Block BONE_PLATE = registerPlate(
            "bone_plate",
            BONE_BLOCK,
            BlockSetType.CRIMSON,
            NetherModels.pressurePlate("block/bone_block_plate")
    );
    public static final Block BONE_WALL = registerWall("bone_wall", BONE_BLOCK,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block BONE_TILE = registerBlock("bone_tile", Blocks.BONE_BLOCK, NetherMaterial.stone(), BNBoneBlock::new);
    public static final Block BONE_REED_DOOR = registerDoor(
            "bone_reed_door",
            BONE_BLOCK,
            p -> new BNWoodlikeDoor(p, WoodType.CRIMSON)
    );
    public static final Block BONE_CINCINNASITE_DOOR = registerDoor(
            "bone_cincinnasite_door",
            BONE_BLOCK,
            p -> new BNWoodlikeDoor(p, WoodType.CRIMSON)
    );
    // Quartz Glass //
    public static final Block QUARTZ_GLASS = registerBlock("quartz_glass", Blocks.GLASS, NetherTraits.of(NetherModels.quartzGlass()), BNGlass::new);
    public static final Block QUARTZ_GLASS_FRAMED = registerBlock(
            "quartz_glass_framed",
            CINCINNASITE_BLOCK,
            NetherTraits.of(NetherModels.quartzGlass()),
            BNGlass::new
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed",
            QUARTZ_GLASS_FRAMED
    );
    public static final Block QUARTZ_GLASS_PANE = registerBlock(
            "quartz_glass_pane",
            QUARTZ_GLASS,
            NetherTraits.concat(NetherRender.translucent(), NetherMaterial.glass()),
            p -> new BNPane.Glass(p, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_pane",
            QUARTZ_GLASS_PANE,
            p -> new BNPane.Glass(p, false),
            // These panes drop only when silk-touched (BNPane.Glass(dropSelf=false) reads the loot table);
            // the silk-touch-only table is now generated by the wover trait instead of hand-authored.
            NetherTraits.and(NetherMaterial.glass(), BlockTraits.LOOT_TABLE.silkTouchSelf())
    );
    public static final Block QUARTZ_GLASS_FRAMED_PANE = registerBlock(
            "quartz_glass_framed_pane",
            CINCINNASITE_BLOCK,
            NetherTraits.concat(NetherRender.translucent(), NetherMaterial.metal()),
            p -> new BNPane.Metal(p, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed_pane",
            QUARTZ_GLASS_FRAMED_PANE,
            p -> new BNPane.Metal(p, true),
            NetherMaterial.metal()
    );
    // Quartz Glass Colored //
    public static final ColoredGlassMaterial QUARTZ_GLASS_COLORED = new ColoredGlassMaterial(
            "quartz_glass",
            QUARTZ_GLASS
    );
    // Obsidian //
    public static final Block BLUE_WEEPING_OBSIDIAN = registerBlockDropSelf(
            "blue_weeping_obsidian",
            Blocks.CRYING_OBSIDIAN,
            NetherModels.obsidianVariants(),
            NetherMaterial.obsidian(),
            BlueWeepingObsidianBlock::new
    );
    public static final Block WEEPING_OBSIDIAN = registerBlockDropSelf(
            "weeping_obsidian",
            Blocks.CRYING_OBSIDIAN,
            NetherModels.obsidianVariants(),
            NetherMaterial.obsidian(),
            VanillaWeepingObsidianBlock::new
    );
    public static final Block BLUE_CRYING_OBSIDIAN = registerBlockDropSelf(
            "blue_crying_obsidian",
            Blocks.CRYING_OBSIDIAN,
            NetherModels.obsidianVariants(),
            NetherMaterial.obsidian(),
            BlueCryingObsidianBlock::new
    );
    public static final Block OBSIDIAN_BRICKS = registerObsidianCube(
            "obsidian_bricks",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_BRICKS_STAIRS = registerStairs(
            "obsidian_bricks_stairs",
            OBSIDIAN_BRICKS,
            false,
            NetherMaterial.stoneTagOnly() // #32: obsidian toughness (50/1200); material 2/6 would nerf it
    );
    public static final Block OBSIDIAN_BRICKS_SLAB = registerSlab(
            "obsidian_bricks_slab",
            OBSIDIAN_BRICKS,
            false,
            NetherMaterial.stoneTagOnly(), // #32: obsidian toughness (50/1200); material 2/6 would nerf it
            ModelTraitLibrary.externalModel()
    );
    public static final Block OBSIDIAN_TILE = registerObsidianCube(
            "obsidian_tile",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_TILE_SMALL = registerObsidianCube(
            "obsidian_tile_small",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_TILE_STAIRS = registerStairs(
            "obsidian_tile_stairs",
            OBSIDIAN_TILE_SMALL,
            false,
            NetherMaterial.stoneTagOnly() // #32: obsidian toughness (50/1200); material 2/6 would nerf it
    );
    public static final Block OBSIDIAN_TILE_SLAB = registerSlab(
            "obsidian_tile_slab",
            OBSIDIAN_TILE_SMALL,
            false,
            NetherMaterial.stoneTagOnly(), // #32: obsidian toughness (50/1200); material 2/6 would nerf it
            ModelTraitLibrary.externalModel()
    );
    public static final Block OBSIDIAN_ROD_TILES = registerObsidianCube(
            "obsidian_rod_tiles",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_GLASS = registerBlock(
            "obsidian_glass",
            Blocks.OBSIDIAN,
            NetherTraits.and(NetherTraits.concat(NetherRender.translucent(), NetherMaterial.obsidianGlass()), ModelTraitLibrary.cube()),
            BlockObsidianGlass::new
    );
    public static final Block OBSIDIAN_GLASS_PANE = registerBlock(
            "obsidian_glass_pane",
            OBSIDIAN_GLASS,
            NetherTraits.concat(NetherRender.translucent(), NetherMaterial.glass()),
            p -> new BNPane.Glass(p, true)
    );
    public static final Block BLUE_OBSIDIAN = registerObsidianCube(
            "blue_obsidian",
            p -> new BNObsidian(p, BLUE_CRYING_OBSIDIAN)
    );
    public static final Block BLUE_OBSIDIAN_BRICKS = registerObsidianCube(
            "blue_obsidian_bricks",
            p -> new BNObsidian(p, null)
    );
    public static final Block BLUE_OBSIDIAN_BRICKS_STAIRS = registerStairs(
            "blue_obsidian_bricks_stairs",
            BLUE_OBSIDIAN_BRICKS,
            false,
            NetherMaterial.stoneTagOnly() // #32: obsidian toughness (50/1200); material 2/6 would nerf it
    );
    public static final Block BLUE_OBSIDIAN_BRICKS_SLAB = registerSlab(
            "blue_obsidian_bricks_slab",
            BLUE_OBSIDIAN_BRICKS,
            false,
            NetherMaterial.stoneTagOnly(), // #32: obsidian toughness (50/1200); material 2/6 would nerf it
            ModelTraitLibrary.externalModel()
    );
    public static final Block BLUE_OBSIDIAN_TILE = registerObsidianCube(
            "blue_obsidian_tile",
            p -> new BNObsidian(p, null)
    );
    public static final Block BLUE_OBSIDIAN_TILE_SMALL = registerObsidianCube(
            "blue_obsidian_tile_small",
            p -> new BNObsidian(p, null)
    );
    public static final Block BLUE_OBSIDIAN_TILE_STAIRS = registerStairs(
            "blue_obsidian_tile_stairs",
            BLUE_OBSIDIAN_TILE_SMALL,
            false,
            NetherMaterial.stoneTagOnly() // #32: obsidian toughness (50/1200); material 2/6 would nerf it
    );
    public static final Block BLUE_OBSIDIAN_TILE_SLAB = registerSlab(
            "blue_obsidian_tile_slab",
            BLUE_OBSIDIAN_TILE_SMALL,
            false,
            NetherMaterial.stoneTagOnly(), // #32: obsidian toughness (50/1200); material 2/6 would nerf it
            ModelTraitLibrary.externalModel()
    );
    public static final Block BLUE_OBSIDIAN_ROD_TILES = registerObsidianCube(
            "blue_obsidian_rod_tiles",
            p -> new BNObsidian(p, null)
    );
    public static final Block BLUE_OBSIDIAN_GLASS = registerBlock(
            "blue_obsidian_glass",
            Blocks.OBSIDIAN,
            NetherTraits.and(NetherTraits.concat(NetherRender.translucent(), NetherMaterial.obsidianGlass()), ModelTraitLibrary.cube()),
            BlockObsidianGlass::new
    );
    public static final Block BLUE_OBSIDIAN_GLASS_PANE = registerBlock(
            "blue_obsidian_glass_pane",
            BLUE_OBSIDIAN_GLASS,
            NetherTraits.concat(NetherRender.translucent(), NetherMaterial.glass()),
            p -> new BNPane.Glass(p, true)
    );
    // Soul Sandstone //
    public static final Block SOUL_SANDSTONE = registerMakeable2X2Soul(
            "soul_sandstone",
            Blocks.SANDSTONE,
            BlockSoulSandstone::new,
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            Blocks.SOUL_SAND
    );
    public static final Block SOUL_SANDSTONE_CUT = registerMakeable2X2Soul(
            "soul_sandstone_cut",
            Blocks.SANDSTONE,
            BlockSoulSandstone::new,
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            SOUL_SANDSTONE
    );
    public static final Block SOUL_SANDSTONE_CUT_STAIRS = registerStairs(
            "soul_sandstone_cut_stairs",
            SOUL_SANDSTONE_CUT,
            false,
            NetherMaterial.stone(),
            NetherModels.soulSandstoneCutStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_CUT_SLAB = registerSlab(
            "soul_sandstone_cut_slab",
            SOUL_SANDSTONE_CUT,
            false,
            NetherMaterial.stone(),
            ModelTraitLibrary.externalModel()
    );
    public static final Block SOUL_SANDSTONE_WALL = registerWall("soul_sandstone_wall", SOUL_SANDSTONE_CUT,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block SOUL_SANDSTONE_SMOOTH = registerSoulBlock(
            "soul_sandstone_smooth",
            Blocks.SANDSTONE,
            BlockBase.Stone::new
    );
    public static final Block SOUL_SANDSTONE_CHISELED = registerMakeable2X2Soul(
            "soul_sandstone_chiseled",
            Blocks.SANDSTONE,
            BlockBase.Stone::new,
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            SOUL_SANDSTONE_SMOOTH
    );
    public static final Block SOUL_SANDSTONE_STAIRS = registerStairs(
            "soul_sandstone_stairs",
            SOUL_SANDSTONE,
            false,
            NetherMaterial.stone(),
            NetherModels.soulSandstoneStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SMOOTH_STAIRS = registerStairs(
            "soul_sandstone_smooth_stairs",
            SOUL_SANDSTONE_SMOOTH,
            false,
            NetherMaterial.stone(),
            NetherModels.soulSandstoneSmoothStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SLAB = registerSlab("soul_sandstone_slab", SOUL_SANDSTONE, false,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block SOUL_SANDSTONE_SMOOTH_SLAB = registerSlab(
            "soul_sandstone_smooth_slab",
            SOUL_SANDSTONE_SMOOTH,
            false,
            NetherMaterial.stone(),
            ModelTraitLibrary.externalModel()
    );
    // Basalt Bricks //
    public static final Block BASALT_BRICKS = registerMakeable2X2(
            "basalt_bricks",
            Blocks.BASALT,
            NetherModels.basaltBricks(),
            BlockBase.Stone::new,
            "basalt_bricks",
            RecipeCategory.BUILDING_BLOCKS,
            Blocks.POLISHED_BASALT
    );
    public static final Block BASALT_BRICKS_STAIRS = registerStairs("basalt_bricks_stairs", BASALT_BRICKS, true, NetherMaterial.stone());
    public static final Block BASALT_BRICKS_SLAB = registerSlab("basalt_bricks_slab", BASALT_BRICKS, true,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block BASALT_BRICKS_WALL = registerWall("basalt_bricks_wall", BASALT_BRICKS,
            NetherMaterial.stone(), ModelTraitLibrary.externalModel());
    public static final Block BASALT_SLAB = registerSlab(
            "basalt_slab",
            Blocks.BASALT,
            false,
            NetherMaterial.stone(),
            ModelTraitLibrary.externalModel()
    );
    public static final Block ORANGE_MUSHROOM = registerBlock("orange_mushroom", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockOrangeMushroom::new);
    public static final Block RED_MOLD = registerBlock("red_mold", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockRedMold::new);
    public static final Block GRAY_MOLD = registerBlock("gray_mold", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockGrayMold::new);
    public static final Block LUCIS_SPORE = registerBlock("lucis_spore", NetherTraits.seed(), BlockLucisSpore::new);
    public static final Block GIANT_LUCIS = registerBlock(
            "giant_lucis",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe(), NetherLoot.giantLucis()),
            BlockGiantLucis::new
    );
    public static final Block GIANT_MOLD_SAPLING = registerBlock("giant_mold_sapling", NetherTraits.sapling(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockGiantMoldSapling::new);
    public static final Block JELLYFISH_MUSHROOM_SAPLING = registerBlock(
            "jellyfish_mushroom_sapling",
            NetherTraits.sapling(NetherRender.cutoutAnd(NetherSurvival.nylium())),
            BlockJellyfishMushroomSapling::new
    );
    public static final Block EYE_SEED = registerBlock("eye_seed", NetherTraits.seed(NetherRender.cutoutAnd(NetherSurvival.netherrack())), BlockEyeSeed::new);
    // Grass //
    public static final Block NETHER_GRASS = registerBlock("nether_grass", NetherTraits.plant(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.netherGrass(), NetherLoot.netherGrass())), BlockNetherGrass.NetherGrass::new);
    public static final Block SWAMP_GRASS = registerBlock("swamp_grass", NetherTraits.plant(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.grass("swamp_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.SwampGrass::new);
    public static final Block SOUL_GRASS = registerBlock("soul_grass", NetherTraits.plant(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("soul_grass", 2), NetherLoot.netherGrass())), BlockSoulGrass::new);
    public static final Block JUNGLE_PLANT = registerBlock("jungle_plant", NetherTraits.plant(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.junglePlant(), NetherLoot.netherGrass())), BlockNetherGrass.JunglePlant::new);
    public static final Block BONE_GRASS = registerBlock("bone_grass", NetherTraits.plant(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("bone_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.BoneGrass::new);
    public static final Block SEPIA_BONE_GRASS = registerBlock("sepia_bone_grass", NetherTraits.plant(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("sepia_bone_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.SepiaBoneGrass::new);
    // Vines //
    // Cutout, for the same reason as NEON_EQUISETUM below: these extend bclib's BaseVineBlock /
    // BaseSimpleVineBlock rather than BlockBase, so they were never IRenderTypeable and the old
    // registerRenderLayers() walk never gave them a layer. Every texture they reach is binary alpha.
    public static final Block BLACK_VINE = registerBlock("black_vine", NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(WeightedCrossModelTrait.booleanDispatch(
            BaseSimpleVineBlock.BOTTOM,
            List.of(
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/black_vine")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/black_vine"))
            ),
            List.of(
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/black_vine_bottom")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/black_vine_bottom"))
            ),
            WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("block/black_vine"))
    ), NetherLoot.blackVine()))), BlockBlackVine::new);
    public static final Block BLOOMING_VINE = registerBlock("blooming_vine", NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(WeightedCrossModelTrait.booleanDispatch(
            BaseSimpleVineBlock.BOTTOM,
            List.of(
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_1")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_1")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_2")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_2")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_3")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_3"))
            ),
            List.of(
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_bottom_1")),
                    WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_bottom_1"))
            ),
            WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/flowered_vine"))
    ), NetherLoot.blackVine()))), BlockBlackVine::new);
    public static final Block GOLDEN_VINE = registerVine("golden_vine", NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(WeightedCrossModelTrait.booleanDispatch(
            BaseSimpleVineBlock.BOTTOM,
            List.of(
                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/golden_vine")),
                    WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/golden_vine"))
            ),
            List.of(
                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/golden_vine_bottom")),
                    WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/golden_vine_bottom"))
            ),
            WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/golden_vine"))
    )))), BlockGoldenVine::new);

    public static final BlockLumabusVine LUMABUS_VINE = registerBlockNI(
            "lumabus_vine",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(
                    ModelTraitLibrary.externalModelDelegatedItem(),
                    NetherLoot.lumabusVine()
            ))),
            p -> new BlockLumabusVine(p, MapColor.COLOR_CYAN)
    );
    public static final BlockLumabusVine GOLDEN_LUMABUS_VINE = registerBlockNI(
            "golden_lumabus_vine",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(
                    ModelTraitLibrary.externalModelDelegatedItem(),
                    NetherLoot.lumabusVine()
            ))),
            p -> new BlockLumabusVine(p, MapColor.COLOR_YELLOW)
    );

    // Small Plants
    public static final Block SOUL_VEIN = registerBlock("soul_vein", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherSand())), BlockSoulVein::new);
    public static final Block BONE_MUSHROOM = registerBlock("bone_mushroom", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.boneBlocks())), BlockBoneMushroom::new);
    public static final Block BLACK_BUSH = registerBlock("black_bush", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackBush::new);
    public static final Block INK_BUSH = registerBlockNI("ink_bush", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockInkBush::new);
    public static final Block INK_BUSH_SEED = registerBlock("ink_bush_seed", NetherTraits.and(NetherTraits.seed(NetherRender.cutoutAnd(NetherSurvival.netherGround())), WeightedCrossModelTrait.simple(
            List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/ink_bush_seed"))),
            WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/ink_bush_seed"))
    )), BlockInkBushSeed::new);
    public static final Block SMOKER = registerBlock(
            "smoker",
            NetherTraits.and(NetherSurvival.netherGround(), BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockSmoker::new
    );
    public static final Block EGG_PLANT = registerBlock("egg_plant", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockEggPlant::new);
    public static final Block BLACK_APPLE = registerBlockNI("black_apple", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackApple::new);
    public static final Block BLACK_APPLE_SEED = registerBlock("black_apple_seed", NetherTraits.seed(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackAppleSeed::new);
    public static final Block MAGMA_FLOWER = registerBlock(
            "magma_flower",
            NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.magmaBlockOrSand()),
                    BlockTraits.MINEABLE_WITH.needsHoe()
            ),
            BlockMagmaFlower::new
    );
    public static final Block FEATHER_FERN = registerBlock("feather_fern", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockFeatherFern::new);
    public static final Block MOSS_COVER = registerBlock("moss_cover", NetherTraits.plant(NetherRender.cutout()), BlockMossCover::new);
    // Cutout: BlockNeonEquisetum extends bclib's BaseVineBlock rather than BlockBase, so it was never
    // IRenderTypeable and the old registerRenderLayers() walk never gave it a layer - its stem/leaf
    // textures have drawn their transparent pixels opaque since long before the trait migration.
    public static final Block NEON_EQUISETUM = registerVine(
            "neon_equisetum",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.and(
                    NetherSurvival.netherrack(),
                    ModelTraitLibrary.externalModel()
            ))),
            BlockNeonEquisetum::new
    );
    public static final Block HOOK_MUSHROOM = registerBlock("hook_mushroom", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.netherrack())), BlockHookMushroom::new);
    // Cutout, same BaseVineBlock gap as the vines above.
    public static final Block WHISPERING_GOURD_VINE = registerBlock(
            "whispering_gourd_vine",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(
                    ModelTraitLibrary.externalModel(),
                    NetherLoot.whisperingGourdVine()
            ))),
            BlockWhisperingGourdVine::new
    );
    public static final Block WHISPERING_GOURD = registerBlock(
            "whispering_gourd",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockWhisperingGourd::new
    );
    public static final Block WHISPERING_GOURD_LANTERN = registerBlock(
            "whispering_gourd_lantern",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockWhisperingGourdLantern::new
    );
    // Cactuses //
    public static final Block AGAVE = registerBlock(
            "agave",
            NetherTraits.plant(NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.gravel()),
                    BlockTraits.MINEABLE_WITH.needsShears()
            )),
            BlockAgave::new
    );
    public static final Block BARREL_CACTUS = registerBlock(
            "barrel_cactus",
            NetherTraits.plant(NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.gravel()),
                    BlockTraits.MINEABLE_WITH.needsShears()
            )),
            BlockBarrelCactus::new
    );
    public static final Block NETHER_CACTUS = registerBlock("nether_cactus", NetherTraits.plant(NetherRender.cutoutAnd(NetherSurvival.gravel())), BlockNetherCactus::new);
    // Wall plants
    public static final Block WALL_MOSS = registerBlock("wall_moss", NetherTraits.plant(NetherRender.cutout()), p -> new BlockPlantWall(p, MapColor.COLOR_RED));
    public static final Block WALL_MUSHROOM_BROWN = registerBlock(
            "wall_mushroom_brown",
            NetherTraits.plant(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_BROWN)
    );
    public static final Block WALL_MUSHROOM_RED = registerBlock(
            "wall_mushroom_red",
            NetherTraits.plant(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_RED)
    );
    public static final Block JUNGLE_MOSS = registerBlock(
            "jungle_moss",
            NetherTraits.plant(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_LIGHT_GREEN)
    );
    // Decorations //
    public static final Block PIG_STATUE_RESPAWNER = registerBlock(
            "pig_statue_respawner",
            CINCINNASITE_BLOCK,
            NetherTraits.concat(NetherRender.cutout(), NetherMaterial.metal()),
            BlockStatueRespawner::new
    );
    public static final Block CINCINNASITE_POT = registerBlock(
            "cincinnasite_pot",
            CINCINNASITE_BLOCK,
            NetherMaterial.cincinnasite(),
            p -> new BlockBNPot.Metal(p)
    );
    public static final Block BRICK_POT = registerBlock(
            "brick_pot",
            Blocks.NETHER_BRICKS,
            NetherMaterial.stone(),
            p -> new BlockBNPot.Stone(p)
    );
    public static final Block GEYSER = registerBlock("geyser", Blocks.NETHERRACK, NetherMaterial.stone(), BlockGeyser::new);
    public static final Block NETHERRACK_STALACTITE = registerStalactite("netherrack_stalactite", Blocks.NETHERRACK);
    public static final Block GLOWSTONE_STALACTITE = registerStalactite("glowstone_stalactite", Blocks.GLOWSTONE, NetherProps.strength(0.3F, 0.3F));
    public static final Block BLACKSTONE_STALACTITE = registerStalactite("blackstone_stalactite", Blocks.BLACKSTONE);
    public static final Block BASALT_STALACTITE = registerStalactite("basalt_stalactite", Blocks.BASALT);
    public static final Block BONE_STALACTITE = registerStalactite("bone_stalactite", BONE_BLOCK);
    // Fire Bowls
    public static final Block CINCINNASITE_FIRE_BOWL = registerFireBowl(
            "cincinnasite_fire_bowl",
            CINCINNASITE_FORGED,
            Blocks.NETHERRACK,
            NetherItems.CINCINNASITE_INGOT,
            BlockFireBowl.Metal::new,
            NetherMaterial.cincinnasite()
    );
    public static final Block BRICKS_FIRE_BOWL = registerFireBowl(
            "bricks_fire_bowl",
            NETHER_BRICK_TILE_LARGE,
            Blocks.NETHERRACK,
            Items.NETHER_BRICK,
            BlockFireBowl.Stone::new,
            NetherMaterial.stone()
    );
    public static final Block NETHERITE_FIRE_BOWL = registerFireBowl(
            "netherite_fire_bowl",
            Blocks.NETHERITE_BLOCK,
            Blocks.NETHERRACK,
            Items.NETHERITE_INGOT,
            BlockFireBowl.Metal::new,
            NetherMaterial.metalTagOnly() // #32: netherite toughness (50/1200); material 5/6 would nerf it
    );
    public static final Block CINCINNASITE_FIRE_BOWL_SOUL = registerFireBowl(
            "cincinnasite_fire_bowl_soul",
            CINCINNASITE_FORGED,
            Blocks.SOUL_SAND,
            NetherItems.CINCINNASITE_INGOT,
            BlockFireBowl.Metal::new,
            NetherMaterial.cincinnasite()
    );
    public static final Block BRICKS_FIRE_BOWL_SOUL = registerFireBowl(
            "bricks_fire_bowl_soul",
            NETHER_BRICK_TILE_LARGE,
            Blocks.SOUL_SAND,
            Items.NETHER_BRICK,
            BlockFireBowl.Stone::new,
            NetherMaterial.stone()
    );
    public static final Block NETHERITE_FIRE_BOWL_SOUL = registerFireBowl(
            "netherite_fire_bowl_soul",
            Blocks.NETHERITE_BLOCK,
            Blocks.SOUL_SAND,
            Items.NETHERITE_INGOT,
            BlockFireBowl.Metal::new,
            NetherMaterial.metalTagOnly() // #32: netherite toughness (50/1200); material 5/6 would nerf it
    );
    // Terrain //
    public static final BlockTerrain NETHERRACK_MOSS = registerBlock(
            "netherrack_moss",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockNetherMycelium NETHER_MYCELIUM = registerBlock(
            "nether_mycelium",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockNetherMycelium::new,
            CommonBlockTags.MYCELIUM,
            CommonBlockTags.NETHER_MYCELIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK,
            org.betterx.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM
    );
    public static final BlockTerrain JUNGLE_GRASS = registerBlock(
            "jungle_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain MUSHROOM_GRASS = registerBlock(
            "mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SEPIA_MUSHROOM_GRASS = registerBlock(
            "sepia_mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SWAMPLAND_GRASS = registerBlock(
            "swampland_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final Block FARMLAND = registerBlock(
            "farmland",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockFarmland::new
    );
    public static final BlockTerrain CEILING_MUSHROOMS = registerBlock(
            "ceiling_mushrooms",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            NetherTraits.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // Roofs //
    public static final Block ROOF_TILE_NETHER_BRICKS = registerRoof("roof_tile_nether_bricks", Blocks.NETHER_BRICKS, BlockBase.Stone::new, NetherMaterial.stone());
    public static final Block ROOF_TILE_NETHER_BRICKS_STAIRS = registerStairs(
            "roof_tile_nether_bricks_stairs",
            ROOF_TILE_NETHER_BRICKS,
            false,
            NetherMaterial.stone()
    );
    public static final Block ROOF_TILE_NETHER_BRICKS_SLAB = registerSlab(
            "roof_tile_nether_bricks_slab",
            ROOF_TILE_NETHER_BRICKS,
            false,
            NetherMaterial.stone(),
            ModelTraitLibrary.externalModel()
    );
    public static final Block ROOF_TILE_CINCINNASITE = registerRoof("roof_tile_cincinnasite", CINCINNASITE_FORGED, BlockBase.Metal::new, NetherMaterial.cincinnasite());
    public static final Block ROOF_TILE_CINCINNASITE_STAIRS = registerStairs(
            "roof_tile_cincinnasite_stairs",
            ROOF_TILE_CINCINNASITE,
            false,
            NetherMaterial.cincinnasite()
    );
    public static final Block ROOF_TILE_CINCINNASITE_SLAB = registerSlab(
            "roof_tile_cincinnasite_slab",
            ROOF_TILE_CINCINNASITE,
            false,
            NetherMaterial.cincinnasite(),
            ModelTraitLibrary.externalModel()
    );
    // Craft Stations //
    public static final Block BLACKSTONE_FURNACE = registerFurnace("blackstone_furnace", Blocks.BLACKSTONE);
    public static final Block BASALT_FURNACE = registerFurnace("basalt_furnace", Blocks.BASALT);
    public static final Block NETHERRACK_FURNACE = registerFurnace("netherrack_furnace", Blocks.NETHERRACK);
    public static final Block CINCINNASITE_FORGE = registerBlock(
            "cincinnasite_forge",
            CINCINNASITE_BLOCK,
            NetherTraits.and(NetherMaterial.cincinnasite(), BlockTraits.LOOT_TABLE.dropSelfCopyName()),
            BlockCincinnasiteForge::new
    );
    public static final Block NETHER_BREWING_STAND = registerBlock(
            "nether_brewing_stand",
            Blocks.NETHER_BRICKS,
            NetherTraits.and(
                    NetherTraits.concat(NetherRender.cutout(), NetherMaterial.stone()),
                    BlockTraits.LOOT_TABLE.dropSelfCopyName()
            ),
            BNBrewingStand::new,
            CommonPoiTags.CLERIC_WORKSTATION
    );
    public static final Block CINCINNASITE_ANVIL = registerBlock(
            "cincinnasite_anvil",
            CINCINNASITE_BLOCK,
            NetherMaterial.cincinnasite(),
            BlockCincinnasiteAnvil::new,
            BlockTags.ANVIL
    );

    public static final VanillaNetherWood WARPED_WOOD = new VanillaNetherWood(
            "warped",
            Blocks.WARPED_PLANKS.defaultMapColor(),
            MapColor.WARPED_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaNetherWood CRIMSON_WOOD = new VanillaNetherWood(
            "crimson",
            Blocks.CRIMSON_PLANKS.defaultMapColor(),
            MapColor.CRIMSON_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaWood OAK_WOOD = VanillaWood.create("oak", Blocks.RED_WOOL);
    public static final VanillaWood SPRUCE_WOOD = VanillaWood.create("spruce", Blocks.RED_WOOL);
    public static final VanillaWood BIRCH_WOOD = VanillaWood.create("birch", Blocks.RED_WOOL);
    public static final VanillaWood JUNGLE_WOOD = VanillaWood.create("jungle", Blocks.RED_WOOL);
    public static final VanillaWood ACACIA_WOOD = VanillaWood.create("acacia", Blocks.BLACK_WOOL);
    public static final VanillaWood DARK_OAK_WOOD = VanillaWood.create("dark_oak", Blocks.RED_WOOL);
    public static final VanillaWood CHERRY_WOOD = VanillaWood.create("cherry", Blocks.WHITE_WOOL);
    public static final VanillaWood BAMBOO_WOOD = VanillaWood.create("bamboo", Blocks.BROWN_WOOL);
    public static final VanillaWood MANGROVE_WOOD = VanillaWood.create("mangrove", Blocks.BLACK_WOOL);
    // Storage
    public static final Block CHEST_OF_DRAWERS = registerBlock(
            "chest_of_drawers",
            CINCINNASITE_BLOCK,
            NetherMaterial.metal(),
            BlockChestOfDrawers::new
    );

    private static BlockRegistry BLOCKS_REGISTRY;
    // Rubeus //
    public static final RubeusMaterial MAT_RUBEUS = new RubeusMaterial().init();
    public static final Block RUBEUS_LEAVES = registerLeaves(
            "rubeus_leaves",
            MAT_RUBEUS.getSapling(),
            p -> new BlockRubeusLeaves(MAT_RUBEUS.getSapling(), p)
    );
    // Mushroom Fir //
    public static final MushroomFirMaterial MAT_MUSHROOM_FIR = new MushroomFirMaterial().init();
    public static final Block TRIMMED_MUSHROOM_FIR_CHEST = registerTrimmedChest(
            "mushroom_fir_trimmed_chest",
            MAT_MUSHROOM_FIR.getPlanks(),
            MAT_MUSHROOM_FIR.getBlock(SlotType.CHEST),
            MAT_MUSHROOM_FIR.getStrippedLog()
    );
    // Mushroom //
    public static final NetherMushroomMaterial MAT_NETHER_MUSHROOM = new NetherMushroomMaterial().init();
    // Anchor Tree
    public static final AnchorTreeMaterial MAT_ANCHOR_TREE = new AnchorTreeMaterial().init();
    public static final Block ANCHOR_TREE_LEAVES = registerLeaves(
            "anchor_tree_leaves",
            MAT_ANCHOR_TREE.getSapling(),
            p -> new BNLeaves(
                    MAT_ANCHOR_TREE.getSapling(),
                    Materials.staticLeaves(p, MapColor.COLOR_GREEN, false).noOcclusion()
            )
    );
    public static final Block ANCHOR_TREE_VINE = registerBlockNI(
            "anchor_tree_vine",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(WeightedCrossModelTrait.propertyDispatch(
                    BlockAnchorTreeVine.SHAPE,
                    List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine_end_2")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine_end_2"))
                            )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine_end_1")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine_end_1"))
                            )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine"))
                            ))
                    ),
                    WeightedCrossModelTrait.Item.delegated()
            )))),
            BlockAnchorTreeVine::new
    );
    // Nether Sakura
    public static final NetherSakuraMaterial MAT_NETHER_SAKURA = new NetherSakuraMaterial().init();
    public static final Block NETHER_SAKURA_LEAVES = registerLeaves(
            "nether_sakura_leaves",
            MAT_NETHER_SAKURA.getSapling(),
            p -> new BlockNetherSakuraLeaves(MAT_NETHER_SAKURA.getSapling(), p),
            netherSakuraLeavesModelTrait()
    );

    /**
     * The nether-sakura-leaves blockstate is a 28-entry weighted variant list (equal weight): a base leaf model
     * plus six flower-overlay members, each placed at the four Y rotations. The six flower members are
     * texture-permutation children of just two hand-authored geometry templates - {@code
     * nether_sakura_leaves_flowers_3} (flower box UV {@code [0,0,16,16]}) parents {@code _flowers_1}/{@code _2};
     * {@code nether_sakura_leaves_flowers_6} (flower box UV {@code [16,16,0,0]}) parents {@code _flowers_4}/{@code
     * _5}. The base leaf model, the two flower templates and the custom-display item model stay hand-authored;
     * the four sibling children and the blockstate are generated. Item is kept ({@link
     * WeightedTemplateModelTrait.Item#none()}) because {@code item/nether_sakura_leaves} carries a bespoke display
     * transform the default item fallback preserves.
     */
    private static BlockModelTrait netherSakuraLeavesModelTrait() {
        final var base = BetterNether.C.mk("block/nether_sakura_leaves");
        final var tmplP = BetterNether.C.mk("block/nether_sakura_leaves_flowers_3");
        final var tmplM = BetterNether.C.mk("block/nether_sakura_leaves_flowers_6");
        final var nsf1 = BetterNether.C.mk("block/nether_sakura_flowers_1");
        final var nsf2 = BetterNether.C.mk("block/nether_sakura_flowers_2");
        final var nsf3 = BetterNether.C.mk("block/nether_sakura_flowers_3");
        // permutation A (flowers_1 / flowers_4): flowers1=nsf3, flowers2=nsf1, flowers3=nsf2
        final var permA = java.util.Map.of("flowers1", nsf3, "flowers2", nsf1, "flowers3", nsf2);
        // permutation B (flowers_2 / flowers_5): flowers1=nsf2, flowers2=nsf3, flowers3=nsf1
        final var permB = java.util.Map.of("flowers1", nsf2, "flowers2", nsf3, "flowers3", nsf1);
        final java.util.List<WeightedTemplateModelTrait.Layer> members = java.util.List.of(
                WeightedTemplateModelTrait.model(base),
                WeightedTemplateModelTrait.child(tmplP, permA),
                WeightedTemplateModelTrait.child(tmplP, permB),
                WeightedTemplateModelTrait.model(tmplP),
                WeightedTemplateModelTrait.child(tmplM, permA),
                WeightedTemplateModelTrait.child(tmplM, permB),
                WeightedTemplateModelTrait.model(tmplM)
        );
        final java.util.List<WeightedTemplateModelTrait.Layer> variants = new java.util.ArrayList<>();
        for (WeightedTemplateModelTrait.Layer m : members) {
            for (int y : new int[]{0, 90, 180, 270}) {
                variants.add(m.rotated(0, y));
            }
        }
        return WeightedTemplateModelTrait.simple(variants, WeightedTemplateModelTrait.Item.none());
    }
    // Soul lily //
    public static final Block SOUL_LILY = registerBlockNI(
            "soul_lily",
            NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.soulGround()),
                    BlockTraits.MINEABLE_WITH.needsAxe()
            ),
            BlockSoulLily::new
    );
    public static final Block SOUL_LILY_SAPLING = registerBlock("soul_lily_sapling", NetherTraits.and(NetherTraits.sapling(NetherRender.cutoutAnd(NetherSurvival.soulGroundOrFarmland())), ModelTraitLibrary.crossPlant()), BlockSoulLilySapling::new);
    // Large & Small Mushrooms //
    public static final Block RED_LARGE_MUSHROOM = registerBlockNI(
            "red_large_mushroom",
            NetherTraits.and(NetherRender.cutout(), BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockRedLargeMushroom::new
    );
    public static final Block BROWN_LARGE_MUSHROOM = registerBlockNI(
            "brown_large_mushroom",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockBrownLargeMushroom::new
    );
    // Lucis //
    public static final Block LUCIS_MUSHROOM = registerBlockNI(
            "lucis_mushroom",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockLucisMushroom::new
    );
    // Giant Mold //
    public static final Block GIANT_MOLD = registerBlockNI(
            "giant_mold",
            NetherTraits.and(NetherRender.cutout(), BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockGiantMold::new
    );
    // Sodium renders the translucent variant with artefacts, so under Sodium this falls back to cutout.
    // The conditional moved here verbatim from the old BlockJellyfishMushroom constructor.
    public static final Block JELLYFISH_MUSHROOM = registerBlockNI(
            "jellyfish_mushroom",
            NetherTraits.and(
                    FabricLoader.getInstance().isModLoaded("sodium")
                            ? NetherRender.cutout()
                            : NetherRender.translucent(),
                    BlockTraits.MINEABLE_WITH.needsAxe()
            ),
            BlockJellyfishMushroom::new
    );
    // Eyes //
    public static final Block EYEBALL = registerBlockNI(
            "eyeball",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsHoe()),
            BlockEyeball::new
    );
    public static final Block EYEBALL_SMALL = registerBlockNI(
            "eyeball_small",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsHoe()),
            BlockEyeballSmall::new
    );
    // eye_vine has no block item (its clone item is EYE_SEED); it drops nothing, so no loot table is generated.
    // Cutout, same BaseVineBlock gap as the vines above.
    public static final Block EYE_VINE = registerBlockNI("eye_vine", NetherTraits.compostable(NetherRender.cutoutAnd(NetherTraits.of(WeightedCrossModelTrait.simple(
            List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/eye_vine"))),
            WeightedCrossModelTrait.Item.delegated()
    )))), BlockEyeVine::new);

    public static final Block POTTED_PLANT = registerBlockNI(
            "potted_plant",
            NetherTraits.and(NetherRender.cutout(), BlockTraits.MINEABLE_WITH.needsHoe()),
            BlockPottedPlant::new
    );
    public static final Block VEINED_SAND = registerBlockNI(
            "veined_sand",
            Blocks.SAND,
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsShovel()),
            BlockVeinedSand::new,
            NetherTags.NETHER_SAND
    );

    public static final Block NETHERRACK_SLAB = registerSlab("netherrack_slab", Blocks.NETHERRACK, true, NetherMaterial.stone());
    public static final Block NETHERRACK_STAIR = registerStairs("netherrack_stairs", Blocks.NETHERRACK, true, NetherMaterial.stone());
    public static final Block NETHERRACK_WALLS = registerWall("netherrack_wall", Blocks.NETHERRACK, NetherMaterial.stone());


    // DEFERED BLOCKS //
    public static final Block LUMABUS_SEED = registerBlock(
            "lumabus_seed",
            NetherTraits.seed(NetherRender.cutout()),
            p -> new BlockLumabusSeed(p, LUMABUS_VINE, () -> NetherVines.LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    );

    public static final Block GOLDEN_LUMABUS_SEED = registerBlock(
            "golden_lumabus_seed",
            NetherTraits.seed(NetherRender.cutout()),
            p -> new BlockLumabusSeed(p, GOLDEN_LUMABUS_VINE, () -> NetherVines.GOLDEN_LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    );

    private NetherBlocks() {

    }

    @NotNull
    public static BlockRegistry getBlockRegistry() {
        if (BLOCKS_REGISTRY == null) {
            BLOCKS_REGISTRY = BlockRegistry.forMod(BetterNether.C);
        }
        return BLOCKS_REGISTRY;
    }

    public static Stream<Block> getModBlocks() {
        return getBlockRegistry().allBlocks();
    }

    public static Stream<BlockItem> getModBlockItems() {
        return getBlockRegistry().allBlockItems();
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    public static <T extends Block> T registerBlock(
            String name,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    public static <T extends Block> T registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    public static <T extends Block> T registerBlock(
            String name,
            Block propertiesSource,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    public static <T extends Block> T registerBlock(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    private static <T extends Block> T registerBlockNI(
            String name,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .withBlockItem((d, b) -> null)
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    private static <T extends Block> T registerBlockNI(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .withBlockItem((d, b) -> null)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    private static <T extends Block> T registerBlockNI(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .withBlockItem((d, b) -> null)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    private static <T extends Block> T registerBlockNI(
            String name,
            Block propertiesSource,
            List<BlockTrait<?, ?>> traits,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .withBlockItem((d, b) -> null)
                .addTags(tags);
        definition.addTrait(traits);
        return definition.buildAndRegister();
    }

    // A full block (no bclib/wover loot provider on its class) that should simply drop itself.
    @SafeVarargs
    private static <T extends Block> T registerBlockDropSelf(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return registerBlockDropSelf(name, propertiesSource, null, factory, tags);
    }

    /**
     * @param model the model trait to build the block with, or {@code null} to leave the block to the
     *              legacy {@code BlockModelProvider} interface on its class
     */
    @SafeVarargs
    private static <T extends Block> T registerBlockDropSelf(
            String name,
            Block propertiesSource,
            BlockModelTrait model,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return registerBlockDropSelf(name, propertiesSource, model, List.of(), factory, tags);
    }

    @SafeVarargs
    private static <T extends Block> T registerBlockDropSelf(
            String name,
            Block propertiesSource,
            BlockModelTrait model,
            List<BlockTrait<?, ?>> traits,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTrait(model)
                .addTags(tags);
        definition.addTrait(traits);
        return definition.buildAndRegister();
    }

    // A standalone door (one that is not part of a wood set). The model trait routes through vanilla's
    // createDoor, which supplies the blockstate, the door models and the flat item model - see
    // registerStairs for why the trait is needed at all.
    private static <T extends Block> T registerDoor(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource);

        definition.addTrait(ModelTraitLibrary.door());
        // BNWoodlikeDoor carries no Behaviour* marker, so - like the slot blocks (see registerStairs) - it
        // needs its mineable tool tag spelled out or it drops nothing. Every door built here is wood.
        definition.addTrait(NetherMaterial.wood());

        return definition.buildAndRegister();
    }

    // A plain full-cube obsidian variant. Same as registerBlockDropSelf plus the cube model: BNObsidian
    // extends bclib's BaseBlock, which used to generate its own model, but no longer implements
    // BlockModelProvider - so these need a model trait or they get no blockstate at all (see
    // registerStairs).
    private static <T extends Block> T registerObsidianCube(
            String name,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(Blocks.OBSIDIAN)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf());

        definition.addTrait(ModelTraitLibrary.cube());
        definition.addTrait(NetherMaterial.obsidianPortalFrame());

        return definition.buildAndRegister();
    }

    // A "trimmed" variant of a wooden chest, for a chest that lives outside its wood set (the wover CHEST
    // slot only builds the set's own chest). Mirrors org.betterx.wover.sets.api.blocks.types.Chest: a
    // ChestBlock on the vanilla chest block entity, plus BlockTraits.CHEST_BLOCK - which supplies the loot
    // table, the c:chests[/wooden] tags, the block-entity type and (outside datagen) the client chest
    // renderer. Both the renderer and the item model resolve their textures from the block's id, so this
    // picks up assets/betternether/textures/entity/chest/<name>{,_left,_right}.png.
    private static Block registerTrimmedChest(String name, Block planks, Block chest, Block trim) {
        final var definition = getBlockRegistry()
                .<ChestBlock>defineDefaultBlock(
                        name,
                        def -> new ChestBlock(() -> BlockEntityType.CHEST, def.getProperties())
                )
                .replacePropertiesWithCopy(planks)
                .addTrait(BlockTraits.WOOD_BLOCK.withDefault())
                .addTrait(BlockTraits.CHEST_BLOCK)
                .addTrait(BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                        .crafting(key.location(), block)
                        .shapeless()
                        .addMaterial('C', chest)
                        .addMaterial('#', trim)
                        .group("chest")
                        .outputCount(1)
                        .category(RecipeCategory.DECORATIONS)
                        .build(context)));

        // The model trait lives in the client source set, so only touch it on the client (mirrors
        // SlotFromDefinition, which guards its buildModel() call the same way).
        definition.addTrait(ModelTraitLibrary.chest(() -> planks));

        return definition.buildAndRegister();
    }

    // Leaves: vanilla-style drop logic (chance-based sapling drop plus sticks).
    private static <T extends Block> T registerLeaves(
            String name,
            Block sapling,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        return registerLeaves(name, sapling, factory, null);
    }

    private static <T extends Block> T registerLeaves(
            String name,
            Block sapling,
            Function<BlockBehaviour.Properties, T> factory,
            BlockModelTrait modelTrait
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTrait(BlockTraits.LOOT_TABLE.dropLeaves(sapling))
                // BehaviourLeaves (still on these classes, for the LEAVES tags and the creative tab) extends
                // BehaviourCompostable and overrides compostingChance() to 0.3f - but that marker only ever
                // produced the c:compostable item tag, never a composter entry. The trait registers the real
                // one, at the 0.3f the marker always intended.
                .addTrait(CompostableBlockTrait.withChance(0.3f));
        if (modelTrait != null) {
            definition.addTrait(modelTrait);
        }
        return definition.buildAndRegister();
    }

    // Vine-like plants: drop themselves only when broken with silk touch, a hoe, or shears.
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    private static <T extends Block> T registerVine(
            String name,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouchOrHoeOrShears());
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    private static <T extends Block> T registerVine(
            String name,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouchOrHoeOrShears())
                .buildAndRegister();
    }

    private static void addFuel(Block source, Block result) {
        if (source.defaultBlockState().ignitedByLava()) {
            FuelRegistryEvents.BUILD.register((builder, fuelContext) -> builder.add(result, 40));
        }
    }

    @SafeVarargs
    public static Block registerStairs(
            String name,
            Block source,
            boolean fireproof,
            List<BlockTrait<?, ?>> material,
            TagKey<Block>... tags
    ) {
        // bclib's BaseStairsBlock (which generated its own models) is gone, and the vanilla StairBlock this
        // replaced it with has no model source at all - without this trait the block gets no blockstate and
        // its item falls back to a flat icon on a texture that does not exist.
        return registerStairs(name, source, fireproof, material, ModelTraitLibrary.stairs(() -> source), tags);
    }

    /**
     * @param material the material tool/tag traits (a {@link NetherMaterial} list) - the vanilla
     *                 {@code StairBlock} carries no {@code Behaviour*} marker, so without this the copied
     *                 {@code requiresCorrectToolForDrops} leaves it in no mineable tag and it drops nothing.
     * @param model    the model trait to build the stairs with; pass a {@link NetherModels} factory for
     *                 stairs whose textures are not simply their material block's.
     */
    @SafeVarargs
    public static Block registerStairs(
            String name,
            Block source,
            boolean fireproof,
            List<BlockTrait<?, ?>> material,
            BlockModelTrait model,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.StairBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.StairBlock(source.defaultBlockState(), def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                // STAIR_BLOCK.withDefault() is property-free (BlockTags.STAIRS + item tag, and its own
                // LOOT_TABLE.dropSelf()) - it restores the #minecraft:stairs membership the vanilla StairBlock
                // lost in the migration, and supplies loot so no separate LOOT_TABLE is added (two would
                // double-generate).
                .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
                .addTags(tags);

        definition.addTrait(model);
        definition.addTrait(material);

        Block stairs = definition.buildAndRegister();

        if (stairs.defaultBlockState().ignitedByLava())
            addFuel(source, stairs);
        if (ModCore.isDatagen())
            RecipesHelper.makeStairsRecipe(source, stairs);

        return stairs;
    }

    @SafeVarargs
    public static Block registerSlab(
            String name,
            Block source,
            boolean fireproof,
            List<BlockTrait<?, ?>> material,
            TagKey<Block>... tags
    ) {
        return registerSlab(name, source, fireproof, material, ModelTraitLibrary.slab(() -> source), tags);
    }

    /**
     * @param material the material tool/tag traits (a {@link NetherMaterial} list); see
     *                 {@link #registerStairs} for why the vanilla {@code SlabBlock} needs it.
     * @param model    the model trait to build the slab with; pass {@code ModelTraitLibrary.externalModel()}
     *                 for a slab whose blockstate is hand-authored in src/main/resources.
     */
    @SafeVarargs
    public static Block registerSlab(
            String name,
            Block source,
            boolean fireproof,
            List<BlockTrait<?, ?>> material,
            BlockModelTrait model,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.SlabBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.SlabBlock(def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                // See registerStairs: SLAB_BLOCK.withDefault() restores #minecraft:slabs and carries the loot.
                .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
                .addTags(tags);

        // See registerStairs: replaces the model generation bclib's BaseSlabBlock used to provide.
        definition.addTrait(model);
        definition.addTrait(material);

        Block slab = definition.buildAndRegister();

        if (slab.defaultBlockState().ignitedByLava())
            addFuel(source, slab);
        if (ModCore.isDatagen())
            RecipesHelper.makeSlabRecipe(source, slab);

        return slab;
    }

    /**
     * @param factory the concrete {@link BlockBase} subclass for this roof's material. This used to be
     *                inferred from {@code source} by {@code BehaviourHelper.from}; the material is known
     *                at every call site, so it is passed directly.
     */
    private static Block registerRoof(
            String name,
            Block source,
            Function<BlockBehaviour.Properties, BlockBase> factory,
            List<BlockTrait<?, ?>> material
    ) {
        // The roof block is a BlockBase.Stone / BlockBase.Metal that dropped its Behaviour* marker; the
        // caller passes the matching pickaxe tool tag (NetherMaterial.stone()/metal()). Tag only, no forced
        // property, so the copied source hardness is unchanged.
        final var definition = getBlockRegistry()
                .<BlockBase>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf());
        definition.addTrait(material);
        Block roof = definition.buildAndRegister();

        addFuel(source, roof);
        if (ModCore.isDatagen())
            RecipesHelper.makeRoofRecipe(source, roof);

        return roof;
    }

    public static Block registerButton(String name, Block source, BlockSetType type, List<BlockTrait<?, ?>> material) {
        return registerButton(name, source, type, material, ModelTraitLibrary.button(() -> source));
    }

    /**
     * @param material the material tool/tag traits (a {@link NetherMaterial} list); see
     *                 {@link #registerStairs} for why the vanilla {@code ButtonBlock} needs it.
     * @param model    the model trait to build the button with; pass a {@link NetherModels} factory for a
     *                 button whose texture is not simply its material block's.
     */
    public static Block registerButton(String name, Block source, BlockSetType type, List<BlockTrait<?, ?>> material, BlockModelTrait model) {
        Block button = getBlockRegistry()
                .<net.minecraft.world.level.block.ButtonBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.ButtonBlock(type, 30, def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTrait(model)
                .addTrait(material)
                .buildAndRegister();

        addFuel(source, button);
        if (ModCore.isDatagen())
            RecipesHelper.makeButtonRecipe(source, button);

        return button;
    }

    public static Block registerPlate(String name, Block source, BlockSetType type) {
        return registerPlate(name, source, type, ModelTraitLibrary.pressurePlate(() -> source));
    }

    /**
     * @param model the model trait to build the pressure plate with; pass a {@link NetherModels} factory
     *              for a plate whose texture is not simply its material block's.
     */
    public static Block registerPlate(String name, Block source, BlockSetType type, BlockModelTrait model) {
        Block plate = getBlockRegistry()
                .<BasePressurePlateBlock>defineDefaultBlock(
                        name,
                        def -> BasePressurePlateBlock.from(source, type, def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(model)
                .buildAndRegister();

        addFuel(source, plate);
        if (ModCore.isDatagen())
            RecipesHelper.makePlateRecipe(source, plate);

        return plate;
    }


    public static <T extends Block> T registerSoulBlock(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory
    ) {
        // registerSoulBlock only builds soul_sandstone_smooth (a BlockBase.Stone). That class dropped its
        // BehaviourStone marker, so the pickaxe tool tag is supplied here. Tag only, no forced property.
        return registerBlock(
                name,
                propertiesSource,
                NetherMaterial.stone(),
                factory,
                BlockTags.SOUL_FIRE_BASE_BLOCKS,
                BlockTags.SOUL_SPEED_BLOCKS
        );
    }

    @SafeVarargs
    public static <T extends Block> T registerMakeable2X2Soul(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            String group,
            RecipeCategory category,
            Block recipeSource,
            TagKey<Block>... tags
    ) {
        return registerMakeable2X2(name, propertiesSource, factory, group, category, recipeSource,
                concat(tags, BlockTags.SOUL_FIRE_BASE_BLOCKS, BlockTags.SOUL_SPEED_BLOCKS)
        );
    }

    @SafeVarargs
    public static <T extends Block> T registerMakeable2X2(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            String group,
            RecipeCategory category,
            Block recipeSource,
            TagKey<Block>... tags
    ) {
        return registerMakeable2X2(name, propertiesSource, null, factory, group, category, recipeSource, tags);
    }

    /**
     * @param model the model trait to build the block with, or {@code null} to leave the block to the
     *              legacy {@code BlockModelProvider} interface on its class
     */
    @SafeVarargs
    public static <T extends Block> T registerMakeable2X2(
            String name,
            Block propertiesSource,
            BlockModelTrait model,
            Function<BlockBehaviour.Properties, T> factory,
            String group,
            RecipeCategory category,
            Block recipeSource,
            TagKey<Block>... tags
    ) {
        // Every registerMakeable2X2 block is a stone material (BlockSoulSandstone / BlockBase.Stone), which
        // dropped its BehaviourStone marker - restore the pickaxe tool tag alongside the model. Tag only,
        // no forced property, so the copied source hardness (sandstone 0.8, basalt 1.25/4.2) is unchanged.
        T result = registerBlock(name, propertiesSource, NetherTraits.and(NetherMaterial.stone(), model), factory, tags);
        if (ModCore.isDatagen())
            RecipesHelper.makeSimpleRecipe2(recipeSource, result, 4, group, category);

        return result;
    }

    @SafeVarargs
    private static TagKey<Block>[] concat(TagKey<Block>[] tags, TagKey<Block>... extra) {
        TagKey<Block>[] result = java.util.Arrays.copyOf(tags, tags.length + extra.length);
        System.arraycopy(extra, 0, result, tags.length, extra.length);
        return result;
    }

    public static Block registerWall(String name, Block source, List<BlockTrait<?, ?>> material) {
        return registerWall(name, source, material, ModelTraitLibrary.wall(() -> source));
    }

    /**
     * @param material the material tool/tag traits (a {@link NetherMaterial} list); see
     *                 {@link #registerStairs} for why the vanilla {@code WallBlock} needs it.
     * @param model    the model trait to build the wall with; see {@link #registerSlab} for when to pass
     *                 {@code ModelTraitLibrary.externalModel()}.
     */
    public static Block registerWall(String name, Block source, List<BlockTrait<?, ?>> material, BlockModelTrait model) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.WallBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.WallBlock(def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                // See registerStairs: WALL_BLOCK.withDefault() supplies #minecraft:walls + item tag and the
                // loot, replacing the manual addTags(WALLS) + LOOT_TABLE that did the same job separately.
                .addTrait(BlockTraits.WALL_BLOCK.withDefault());

        // See registerStairs: replaces the model generation bclib's BaseWallBlock used to provide.
        definition.addTrait(model);
        definition.addTrait(material);

        Block wall = definition.buildAndRegister();

        if (ModCore.isDatagen())
            RecipesHelper.makeWallRecipe(source, wall);

        return wall;
    }


    public static Block registerTaburet(String name, Block source, BlockModelTrait model) {
        Block block = getBlockRegistry()
                .<BaseTaburet>defineDefaultBlock(name, def -> BaseTaburet.from(source, def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTrait(model)
                .addTags(BlockTags.MINEABLE_WITH_AXE)
                .addTrait(BlockTraits.RECIPE.with((key, b, context) -> RecipeBuilder
                        .crafting(key.location(), b)
                        .shape("##", "II")
                        .addMaterial('#', source)
                        .addMaterial('I', Items.STICK)
                        .group("taburet")
                        .outputCount(1)
                        .category(RecipeCategory.DECORATIONS)
                        .build(context)))
                .buildAndRegister();

        addFuel(source, block);

        return block;
    }

    public static Block registerChair(String name, Block source, BlockModelTrait model) {
        Block block = getBlockRegistry()
                .<BaseChair>defineDefaultBlock(
                        name,
                        def -> BaseChair.from(source, NETHER_BRICK_TILE_LARGE, def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(model)
                .addTags(BlockTags.MINEABLE_WITH_AXE)
                .addTrait(BlockTraits.RECIPE.with((key, b, context) -> RecipeBuilder
                        .crafting(key.location(), b)
                        .shape("I ", "##", "II")
                        .addMaterial('#', source)
                        .addMaterial('I', Items.STICK)
                        .group("chair")
                        .outputCount(1)
                        .category(RecipeCategory.DECORATIONS)
                        .build(context)))
                .buildAndRegister();

        addFuel(source, block);

        return block;
    }

    public static Block registerBarStool(String name, Block source, BlockModelTrait model) {
        Block block = getBlockRegistry()
                .<BaseBarStool>defineDefaultBlock(
                        name,
                        def -> BaseBarStool.from(source, NETHER_BRICK_TILE_LARGE, def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(model)
                .addTags(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTrait(BlockTraits.RECIPE.with((key, b, context) -> RecipeBuilder
                        .crafting(key.location(), b)
                        .shape("##", "II", "II")
                        .addMaterial('#', source)
                        .addMaterial('I', Items.STICK)
                        .group("bar_stool")
                        .outputCount(1)
                        .category(RecipeCategory.DECORATIONS)
                        .build(context)))
                .buildAndRegister();

        addFuel(source, block);

        return block;
    }

    public static Block registerFurnace(String name, Block source) {
        // BlockNetherFurnace dropped its BehaviourStone marker; restore the pickaxe tool tag (all furnaces
        // are stone). Tag only, no forced property (see registerStalactite).
        final var definition = getBlockRegistry()
                .<BlockNetherFurnace>defineDefaultBlock(name, def -> new BlockNetherFurnace(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTags(CommonPoiTags.ARMORER_WORKSTATION);
        definition.addTrait(NetherMaterial.stone());
        // BlockNetherFurnace no longer implements DropSelfLootProvider; carry its plain self-drop (no
        // survives_explosion) as a trait so the table stays byte-identical.
        definition.addTrait(NetherLoot.dropSelfNoExplosion());
        Block block = definition.buildAndRegister();

        if (ModCore.isDatagen())
            RecipesHelper.makeRoundRecipe(source, block, "nether_furnace", RecipeCategory.DECORATIONS);


        return block;
    }

    @SafeVarargs
    private static Block registerStalactite(String name, Block source, BlockTrait<?, ?>... extra) {
        // BlockStalactite dropped its BehaviourStone marker; the pickaxe tool tag it used to contribute is
        // restored here (all stalactites are stone). NetherMaterial.stone() is the tag only - no property is
        // forced, so a source like glowstone (which is not requiresCorrectToolForDrops) is unchanged.
        final var definition = getBlockRegistry()
                .<BlockStalactite>defineDefaultBlock(name, def -> new BlockStalactite(def.getProperties()))
                .replacePropertiesWithCopy(source);
        definition.addTrait(NetherMaterial.stone());
        for (BlockTrait<?, ?> t : extra) definition.addTrait(t);
        Block block = definition.buildAndRegister();

        if (ModCore.isDatagen())
            RecipesHelper.makeSimpleRecipe2(block, source, 1, "nether_stalactite", RecipeCategory.DECORATIONS);


        return block;
    }

    /**
     * @param factory the concrete {@link BlockFireBowl} subclass for this bowl's material. This used to be
     *                inferred from {@code source} by {@code BehaviourHelper.from} (with netherite already
     *                special-cased by name); every call site knows its material, so it is passed directly.
     */
    private static Block registerFireBowl(
            String name,
            Block source,
            Block inside,
            Item leg,
            Function<BlockBehaviour.Properties, BlockFireBowl> factory,
            List<BlockTrait<?, ?>> material
    ) {
        // BlockFireBowl.Stone / BlockFireBowl.Metal dropped their Behaviour* markers; the caller passes the
        // matching pickaxe tool tag. Tag only, no forced property (see registerStalactite).
        final boolean isNetherite = name.startsWith("netherite");
        final var definition = getBlockRegistry()
                .<BlockFireBowl>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTrait(NetherRender.cutout());
        definition.addTrait(material);
        Block block = definition.buildAndRegister();

        if (!isNetherite) {
            RecipesHelper.makeFireBowlRecipe(source, inside, leg, block);
        }


        return block;
    }

    @ApiStatus.Internal
    public static void register() {
        //NO-OP
    }
}
