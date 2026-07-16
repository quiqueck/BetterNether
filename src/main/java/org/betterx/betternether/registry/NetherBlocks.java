package org.betterx.betternether.registry;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
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
            BNNetherBrick::new
    );

    // Reed //
    public static final Block NETHER_REED_STEM = registerBlock(
            "nether_reed_stem",
            NetherTraits.and(
                    NetherRender.cutout(),
                    BlockTraits.MINEABLE_WITH.needsHoe(),
                    BlockTraits.MINEABLE_WITH.needsSword()
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
            p -> new BlockOre(
                    p,
                    () -> NetherItems.CINCINNASITE,
                    1,
                    3,
                    0,
                    ToolTiers.IRON_TOOL.blockTag,
                    true
            )
    );
    public static final Block CINCINNASITE_BLOCK = registerBlock("cincinnasite_block", BlockCincinnasite::new);
    public static final Block CINCINNASITE_FORGED = registerBlock("cincinnasite_forged", BlockCincinnasite::new);
    public static final Block CINCINNASITE_PILLAR = registerBlock(
            "cincinnasite_pillar",
            CINCINNASITE_BLOCK,
            BlockCincinnasitPillar::new
    );
    public static final Block CINCINNASITE_BRICKS = registerBlock("cincinnasite_bricks", BlockCincinnasite::new);
    public static final Block CINCINNASITE_BRICK_PLATE = registerBlock(
            "cincinnasite_brick_plate",
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_STAIRS = registerStairs("cincinnasite_stairs", CINCINNASITE_FORGED, false);
    public static final Block CINCINNASITE_SLAB = registerSlab("cincinnasite_slab", CINCINNASITE_FORGED, false,
            ModelTraitLibrary.externalModel());
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
            BlockCincinnasiteLantern::new
    );
    public static final Block CINCINNASITE_TILE_LARGE = registerBlock(
            "cincinnasite_tile_large",
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_TILE_SMALL = registerBlock(
            "cincinnasite_tile_small",
            BlockCincinnasite::new
    );
    public static final Block CINCINNASITE_CARVED = registerBlock("cincinnasite_carved", BlockCincinnasite::new);
    public static final Block CINCINNASITE_WALL = registerWall("cincinnasite_wall", CINCINNASITE_FORGED,
            ModelTraitLibrary.externalModel());
    public static final Block CINCINNASITE_BRICKS_PILLAR = registerBlock(
            "cincinnasite_bricks_pillar",
            CINCINNASITE_FORGED,
            BNPillar.Metal::new
    );
    public static final Block CINCINNASITE_BARS = registerBlock(
            "cincinnasite_bars",
            CINCINNASITE_FORGED,
            NetherRender.translucent(),
            p -> new BNPane.Metal(p, true)
    );
    public static final Block CINCINNASITE_PEDESTAL = registerBlock(
            "cincinnasite_pedestal",
            CINCINNASITE_BLOCK,
            BlockCincinnasitePedestal::new
    );
    public static final Block CINCINNASITE_FRAME = registerBlock(
            "cincinnasite_frame",
            CINCINNASITE_BLOCK,
            NetherRender.cutout(),
            BlockCincinnasiteFrame::new
    );
    public static final Block CINCINNASITE_LANTERN_SMALL = registerBlock(
            "cincinnasite_lantern_small",
            CINCINNASITE_LANTERN,
            NetherRender.cutout(),
            BlockSmallLantern.Metal::new
    );
    public static final Block CINCINNASITE_CHAIN = registerBlock("cincinnasite_chain", Blocks.CHAIN, NetherRender.cutout(), BNChain::new);
    // Ruby //
    public static final Block NETHER_RUBY_ORE = registerBlock(
            "nether_ruby_ore",
            p -> new BlockOre(
                    p,
                    () -> NetherItems.NETHER_RUBY,
                    1,
                    2,
                    5,
                    ToolTiers.DIAMOND_TOOL.blockTag,
                    true
            )
    );
    public static final Block NETHER_RUBY_BLOCK = registerBlock(
            "nether_ruby_block",
            Blocks.DIAMOND_BLOCK,
            BlockNetherRuby::new
    );
    public static final Block NETHER_RUBY_STAIRS = registerStairs("nether_ruby_stairs", NETHER_RUBY_BLOCK, true);
    public static final Block NETHER_RUBY_SLAB = registerSlab("nether_ruby_slab", NETHER_RUBY_BLOCK, true,
            ModelTraitLibrary.externalModel());
    // Vanilla Ores
    public static final Block NETHER_LAPIS_ORE = registerBlock(
            "nether_lapis_ore",
            p -> new BlockOre(
                    p,
                    () -> NetherItems.LAPIS_PILE,
                    3,
                    6,
                    3,
                    ToolTiers.IRON_TOOL.blockTag,
                    false
            )
    );
    public static final Block NETHER_REDSTONE_ORE = registerBlock("nether_redstone_ore", NetherTraits.of(NetherLoot.redstoneOre(1, 3)), RedstoneOreBlock::new);
    // Bricks //
    public static final Block NETHER_BRICK_TILE_SMALL = registerBlock(
            "nether_brick_tile_small",
            Blocks.NETHER_BRICKS,
            BNNetherBrick::new
    );
    public static final Block NETHER_BRICK_WALL = registerWall("nether_brick_wall", NETHER_BRICK_TILE_LARGE,
            ModelTraitLibrary.externalModel());
    public static final Block NETHER_BRICK_TILE_SLAB = registerSlab(
            "nether_brick_tile_slab",
            NETHER_BRICK_TILE_SMALL,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block NETHER_BRICK_TILE_STAIRS = registerStairs(
            "nether_brick_tile_stairs",
            NETHER_BRICK_TILE_SMALL,
            false
    );
    // Bone //
    public static final Block BONE_BLOCK = registerBlock("bone_block", Blocks.BONE_BLOCK, BNBoneBlock::new);
    public static final Block BONE_STAIRS = registerStairs("bone_stairs", BONE_BLOCK, false);
    public static final Block BONE_SLAB = registerSlab("bone_slab", BONE_BLOCK, false,
            ModelTraitLibrary.externalModel());
    public static final Block BONE_BUTTON = registerButton(
            "bone_button",
            BONE_BLOCK,
            BlockSetType.CRIMSON,
            NetherModels.button("block/bone_button")
    );
    public static final Block BONE_PLATE = registerPlate(
            "bone_plate",
            BONE_BLOCK,
            BlockSetType.CRIMSON,
            NetherModels.pressurePlate("block/bone_block_plate")
    );
    public static final Block BONE_WALL = registerWall("bone_wall", BONE_BLOCK,
            ModelTraitLibrary.externalModel());
    public static final Block BONE_TILE = registerBlock("bone_tile", Blocks.BONE_BLOCK, BNBoneBlock::new);
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
            NetherRender.translucent(),
            p -> new BNPane.Glass(p, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_pane",
            QUARTZ_GLASS_PANE,
            p -> new BNPane.Glass(p, false)
    );
    public static final Block QUARTZ_GLASS_FRAMED_PANE = registerBlock(
            "quartz_glass_framed_pane",
            CINCINNASITE_BLOCK,
            NetherRender.translucent(),
            p -> new BNPane.Metal(p, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed_pane",
            QUARTZ_GLASS_FRAMED_PANE,
            p -> new BNPane.Metal(p, true)
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
            BlueWeepingObsidianBlock::new
    );
    public static final Block WEEPING_OBSIDIAN = registerBlockDropSelf(
            "weeping_obsidian",
            Blocks.CRYING_OBSIDIAN,
            NetherModels.obsidianVariants(),
            VanillaWeepingObsidianBlock::new
    );
    public static final Block BLUE_CRYING_OBSIDIAN = registerBlockDropSelf(
            "blue_crying_obsidian",
            Blocks.CRYING_OBSIDIAN,
            NetherModels.obsidianVariants(),
            BlueCryingObsidianBlock::new
    );
    public static final Block OBSIDIAN_BRICKS = registerObsidianCube(
            "obsidian_bricks",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_BRICKS_STAIRS = registerStairs(
            "obsidian_bricks_stairs",
            OBSIDIAN_BRICKS,
            false
    );
    public static final Block OBSIDIAN_BRICKS_SLAB = registerSlab(
            "obsidian_bricks_slab",
            OBSIDIAN_BRICKS,
            false,
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
            false
    );
    public static final Block OBSIDIAN_TILE_SLAB = registerSlab(
            "obsidian_tile_slab",
            OBSIDIAN_TILE_SMALL,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block OBSIDIAN_ROD_TILES = registerObsidianCube(
            "obsidian_rod_tiles",
            p -> new BNObsidian(p, null)
    );
    public static final Block OBSIDIAN_GLASS = registerBlock(
            "obsidian_glass",
            Blocks.OBSIDIAN,
            NetherRender.translucent(),
            BlockObsidianGlass::new
    );
    public static final Block OBSIDIAN_GLASS_PANE = registerBlock(
            "obsidian_glass_pane",
            OBSIDIAN_GLASS,
            NetherRender.translucent(),
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
            false
    );
    public static final Block BLUE_OBSIDIAN_BRICKS_SLAB = registerSlab(
            "blue_obsidian_bricks_slab",
            BLUE_OBSIDIAN_BRICKS,
            false,
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
            false
    );
    public static final Block BLUE_OBSIDIAN_TILE_SLAB = registerSlab(
            "blue_obsidian_tile_slab",
            BLUE_OBSIDIAN_TILE_SMALL,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block BLUE_OBSIDIAN_ROD_TILES = registerObsidianCube(
            "blue_obsidian_rod_tiles",
            p -> new BNObsidian(p, null)
    );
    public static final Block BLUE_OBSIDIAN_GLASS = registerBlock(
            "blue_obsidian_glass",
            Blocks.OBSIDIAN,
            NetherRender.translucent(),
            BlockObsidianGlass::new
    );
    public static final Block BLUE_OBSIDIAN_GLASS_PANE = registerBlock(
            "blue_obsidian_glass_pane",
            BLUE_OBSIDIAN_GLASS,
            NetherRender.translucent(),
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
            NetherModels.soulSandstoneCutStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_CUT_SLAB = registerSlab(
            "soul_sandstone_cut_slab",
            SOUL_SANDSTONE_CUT,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block SOUL_SANDSTONE_WALL = registerWall("soul_sandstone_wall", SOUL_SANDSTONE_CUT,
            ModelTraitLibrary.externalModel());
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
            NetherModels.soulSandstoneStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SMOOTH_STAIRS = registerStairs(
            "soul_sandstone_smooth_stairs",
            SOUL_SANDSTONE_SMOOTH,
            false,
            NetherModels.soulSandstoneSmoothStairs(),
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SLAB = registerSlab("soul_sandstone_slab", SOUL_SANDSTONE, false,
            ModelTraitLibrary.externalModel());
    public static final Block SOUL_SANDSTONE_SMOOTH_SLAB = registerSlab(
            "soul_sandstone_smooth_slab",
            SOUL_SANDSTONE_SMOOTH,
            false,
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
    public static final Block BASALT_BRICKS_STAIRS = registerStairs("basalt_bricks_stairs", BASALT_BRICKS, true);
    public static final Block BASALT_BRICKS_SLAB = registerSlab("basalt_bricks_slab", BASALT_BRICKS, true,
            ModelTraitLibrary.externalModel());
    public static final Block BASALT_BRICKS_WALL = registerWall("basalt_bricks_wall", BASALT_BRICKS,
            ModelTraitLibrary.externalModel());
    public static final Block BASALT_SLAB = registerSlab(
            "basalt_slab",
            Blocks.BASALT,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block ORANGE_MUSHROOM = registerBlock("orange_mushroom", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockOrangeMushroom::new);
    public static final Block RED_MOLD = registerBlock("red_mold", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockRedMold::new);
    public static final Block GRAY_MOLD = registerBlock("gray_mold", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockGrayMold::new);
    public static final Block LUCIS_SPORE = registerBlock("lucis_spore", NetherTraits.compostable(), BlockLucisSpore::new);
    public static final Block GIANT_LUCIS = registerBlock(
            "giant_lucis",
            NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe(), NetherLoot.giantLucis()),
            BlockGiantLucis::new
    );
    public static final Block GIANT_MOLD_SAPLING = registerBlock("giant_mold_sapling", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherMycelium())), BlockGiantMoldSapling::new);
    public static final Block JELLYFISH_MUSHROOM_SAPLING = registerBlock(
            "jellyfish_mushroom_sapling",
            NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.nylium())),
            BlockJellyfishMushroomSapling::new
    );
    public static final Block EYE_SEED = registerBlock("eye_seed", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherrack())), BlockEyeSeed::new);
    // Grass //
    public static final Block NETHER_GRASS = registerBlock("nether_grass", NetherTraits.compostable(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.netherGrass(), NetherLoot.netherGrass())), BlockNetherGrass.NetherGrass::new);
    public static final Block SWAMP_GRASS = registerBlock("swamp_grass", NetherTraits.compostable(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.grass("swamp_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.SwampGrass::new);
    public static final Block SOUL_GRASS = registerBlock("soul_grass", NetherTraits.compostable(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("soul_grass", 2), NetherLoot.netherGrass())), BlockSoulGrass::new);
    public static final Block JUNGLE_PLANT = registerBlock("jungle_plant", NetherTraits.compostable(NetherTraits.and(NetherSurvival.netherrackNyliumAndSculk(), NetherModels.junglePlant(), NetherLoot.netherGrass())), BlockNetherGrass.JunglePlant::new);
    public static final Block BONE_GRASS = registerBlock("bone_grass", NetherTraits.compostable(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("bone_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.BoneGrass::new);
    public static final Block SEPIA_BONE_GRASS = registerBlock("sepia_bone_grass", NetherTraits.compostable(NetherTraits.and(NetherSurvival.soilOrLogs(), NetherModels.grass("sepia_bone_grass", 3), NetherLoot.netherGrass())), BlockNetherGrass.SepiaBoneGrass::new);
    // Vines //
    public static final Block BLACK_VINE = registerBlock("black_vine", NetherTraits.compostable(NetherTraits.of(ModelTraitLibrary.externalModel(), NetherLoot.blackVine())), BlockBlackVine::new);
    public static final Block BLOOMING_VINE = registerBlock("blooming_vine", NetherTraits.compostable(NetherTraits.of(ModelTraitLibrary.externalModel(), NetherLoot.blackVine())), BlockBlackVine::new);
    public static final Block GOLDEN_VINE = registerVine("golden_vine", NetherTraits.compostable(NetherTraits.of(ModelTraitLibrary.externalModel())), BlockGoldenVine::new);

    public static final BlockLumabusVine LUMABUS_VINE = registerBlockNI(
            "lumabus_vine",
            NetherTraits.compostable(NetherTraits.of(
                    ModelTraitLibrary.externalModelDelegatedItem(),
                    NetherLoot.lumabusVine()
            )),
            p -> new BlockLumabusVine(p, MapColor.COLOR_CYAN)
    );
    public static final BlockLumabusVine GOLDEN_LUMABUS_VINE = registerBlockNI(
            "golden_lumabus_vine",
            NetherTraits.compostable(NetherTraits.of(
                    ModelTraitLibrary.externalModelDelegatedItem(),
                    NetherLoot.lumabusVine()
            )),
            p -> new BlockLumabusVine(p, MapColor.COLOR_YELLOW)
    );

    // Small Plants
    public static final Block SOUL_VEIN = registerBlock("soul_vein", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherSand())), BlockSoulVein::new);
    public static final Block BONE_MUSHROOM = registerBlock("bone_mushroom", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.boneBlocks())), BlockBoneMushroom::new);
    public static final Block BLACK_BUSH = registerBlock("black_bush", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackBush::new);
    public static final Block INK_BUSH = registerBlockNI("ink_bush", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockInkBush::new);
    public static final Block INK_BUSH_SEED = registerBlock("ink_bush_seed", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockInkBushSeed::new);
    public static final Block SMOKER = registerBlock(
            "smoker",
            NetherTraits.and(NetherSurvival.netherGround(), BlockTraits.MINEABLE_WITH.needsAxe()),
            BlockSmoker::new
    );
    public static final Block EGG_PLANT = registerBlock("egg_plant", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockEggPlant::new);
    public static final Block BLACK_APPLE = registerBlockNI("black_apple", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackApple::new);
    public static final Block BLACK_APPLE_SEED = registerBlock("black_apple_seed", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockBlackAppleSeed::new);
    public static final Block MAGMA_FLOWER = registerBlock(
            "magma_flower",
            NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.magmaBlockOrSand()),
                    BlockTraits.MINEABLE_WITH.needsHoe()
            ),
            BlockMagmaFlower::new
    );
    public static final Block FEATHER_FERN = registerBlock("feather_fern", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherGround())), BlockFeatherFern::new);
    public static final Block MOSS_COVER = registerBlock("moss_cover", NetherTraits.compostable(NetherRender.cutout()), BlockMossCover::new);
    public static final Block NEON_EQUISETUM = registerVine(
            "neon_equisetum",
            NetherTraits.compostable(NetherTraits.and(
                    NetherSurvival.netherrack(),
                    ModelTraitLibrary.externalModel()
            )),
            BlockNeonEquisetum::new
    );
    public static final Block HOOK_MUSHROOM = registerBlock("hook_mushroom", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.netherrack())), BlockHookMushroom::new);
    public static final Block WHISPERING_GOURD_VINE = registerBlock(
            "whispering_gourd_vine",
            NetherTraits.compostable(NetherTraits.of(
                    ModelTraitLibrary.externalModel(),
                    NetherLoot.whisperingGourdVine()
            )),
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
            NetherTraits.compostable(NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.gravel()),
                    BlockTraits.MINEABLE_WITH.needsShears()
            )),
            BlockAgave::new
    );
    public static final Block BARREL_CACTUS = registerBlock(
            "barrel_cactus",
            NetherTraits.compostable(NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.gravel()),
                    BlockTraits.MINEABLE_WITH.needsShears()
            )),
            BlockBarrelCactus::new
    );
    public static final Block NETHER_CACTUS = registerBlock("nether_cactus", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.gravel())), BlockNetherCactus::new);
    // Wall plants
    public static final Block WALL_MOSS = registerBlock("wall_moss", NetherTraits.compostable(NetherRender.cutout()), p -> new BlockPlantWall(p, MapColor.COLOR_RED));
    public static final Block WALL_MUSHROOM_BROWN = registerBlock(
            "wall_mushroom_brown",
            NetherTraits.compostable(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_BROWN)
    );
    public static final Block WALL_MUSHROOM_RED = registerBlock(
            "wall_mushroom_red",
            NetherTraits.compostable(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_RED)
    );
    public static final Block JUNGLE_MOSS = registerBlock(
            "jungle_moss",
            NetherTraits.compostable(NetherRender.cutout()),
            p -> new BlockPlantWall(p, MapColor.COLOR_LIGHT_GREEN)
    );
    // Decorations //
    public static final Block PIG_STATUE_RESPAWNER = registerBlock(
            "pig_statue_respawner",
            CINCINNASITE_BLOCK,
            NetherRender.cutout(),
            BlockStatueRespawner::new
    );
    public static final Block CINCINNASITE_POT = registerBlock(
            "cincinnasite_pot",
            CINCINNASITE_BLOCK,
            p -> new BlockBNPot.Metal(p)
    );
    public static final Block BRICK_POT = registerBlock(
            "brick_pot",
            Blocks.NETHER_BRICKS,
            p -> new BlockBNPot.Stone(p)
    );
    public static final Block GEYSER = registerBlock("geyser", Blocks.NETHERRACK, BlockGeyser::new);
    public static final Block NETHERRACK_STALACTITE = registerStalactite("netherrack_stalactite", Blocks.NETHERRACK);
    public static final Block GLOWSTONE_STALACTITE = registerStalactite("glowstone_stalactite", Blocks.GLOWSTONE);
    public static final Block BLACKSTONE_STALACTITE = registerStalactite("blackstone_stalactite", Blocks.BLACKSTONE);
    public static final Block BASALT_STALACTITE = registerStalactite("basalt_stalactite", Blocks.BASALT);
    public static final Block BONE_STALACTITE = registerStalactite("bone_stalactite", BONE_BLOCK);
    // Fire Bowls
    public static final Block CINCINNASITE_FIRE_BOWL = registerFireBowl(
            "cincinnasite_fire_bowl",
            CINCINNASITE_FORGED,
            Blocks.NETHERRACK,
            NetherItems.CINCINNASITE_INGOT,
            BlockFireBowl.Metal::new
    );
    public static final Block BRICKS_FIRE_BOWL = registerFireBowl(
            "bricks_fire_bowl",
            NETHER_BRICK_TILE_LARGE,
            Blocks.NETHERRACK,
            Items.NETHER_BRICK,
            BlockFireBowl.Stone::new
    );
    public static final Block NETHERITE_FIRE_BOWL = registerFireBowl(
            "netherite_fire_bowl",
            Blocks.NETHERITE_BLOCK,
            Blocks.NETHERRACK,
            Items.NETHERITE_INGOT,
            BlockFireBowl.Metal::new
    );
    public static final Block CINCINNASITE_FIRE_BOWL_SOUL = registerFireBowl(
            "cincinnasite_fire_bowl_soul",
            CINCINNASITE_FORGED,
            Blocks.SOUL_SAND,
            NetherItems.CINCINNASITE_INGOT,
            BlockFireBowl.Metal::new
    );
    public static final Block BRICKS_FIRE_BOWL_SOUL = registerFireBowl(
            "bricks_fire_bowl_soul",
            NETHER_BRICK_TILE_LARGE,
            Blocks.SOUL_SAND,
            Items.NETHER_BRICK,
            BlockFireBowl.Stone::new
    );
    public static final Block NETHERITE_FIRE_BOWL_SOUL = registerFireBowl(
            "netherite_fire_bowl_soul",
            Blocks.NETHERITE_BLOCK,
            Blocks.SOUL_SAND,
            Items.NETHERITE_INGOT,
            BlockFireBowl.Metal::new
    );
    // Terrain //
    public static final BlockTerrain NETHERRACK_MOSS = registerBlock(
            "netherrack_moss",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockNetherMycelium NETHER_MYCELIUM = registerBlock(
            "nether_mycelium",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
            BlockNetherMycelium::new,
            CommonBlockTags.MYCELIUM,
            CommonBlockTags.NETHER_MYCELIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK,
            org.betterx.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM
    );
    public static final BlockTerrain JUNGLE_GRASS = registerBlock(
            "jungle_grass",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain MUSHROOM_GRASS = registerBlock(
            "mushroom_grass",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SEPIA_MUSHROOM_GRASS = registerBlock(
            "sepia_mushroom_grass",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SWAMPLAND_GRASS = registerBlock(
            "swampland_grass",
            Blocks.NETHERRACK,
            NetherTraits.of(NetherLoot.terrain()),
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
            NetherTraits.of(NetherLoot.terrain()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // Roofs //
    public static final Block ROOF_TILE_NETHER_BRICKS = registerRoof("roof_tile_nether_bricks", Blocks.NETHER_BRICKS, BlockBase.Stone::new);
    public static final Block ROOF_TILE_NETHER_BRICKS_STAIRS = registerStairs(
            "roof_tile_nether_bricks_stairs",
            ROOF_TILE_NETHER_BRICKS,
            false
    );
    public static final Block ROOF_TILE_NETHER_BRICKS_SLAB = registerSlab(
            "roof_tile_nether_bricks_slab",
            ROOF_TILE_NETHER_BRICKS,
            false,
            ModelTraitLibrary.externalModel()
    );
    public static final Block ROOF_TILE_CINCINNASITE = registerRoof("roof_tile_cincinnasite", CINCINNASITE_FORGED, BlockBase.Metal::new);
    public static final Block ROOF_TILE_CINCINNASITE_STAIRS = registerStairs(
            "roof_tile_cincinnasite_stairs",
            ROOF_TILE_CINCINNASITE,
            false
    );
    public static final Block ROOF_TILE_CINCINNASITE_SLAB = registerSlab(
            "roof_tile_cincinnasite_slab",
            ROOF_TILE_CINCINNASITE,
            false,
            ModelTraitLibrary.externalModel()
    );
    // Craft Stations //
    public static final Block BLACKSTONE_FURNACE = registerFurnace("blackstone_furnace", Blocks.BLACKSTONE);
    public static final Block BASALT_FURNACE = registerFurnace("basalt_furnace", Blocks.BASALT);
    public static final Block NETHERRACK_FURNACE = registerFurnace("netherrack_furnace", Blocks.NETHERRACK);
    public static final Block CINCINNASITE_FORGE = registerBlock(
            "cincinnasite_forge",
            CINCINNASITE_BLOCK,
            BlockCincinnasiteForge::new
    );
    public static final Block NETHER_BREWING_STAND = registerBlock(
            "nether_brewing_stand",
            Blocks.NETHER_BRICKS,
            NetherRender.cutout(),
            BNBrewingStand::new,
            CommonPoiTags.CLERIC_WORKSTATION
    );
    public static final Block CINCINNASITE_ANVIL = registerBlock(
            "cincinnasite_anvil",
            CINCINNASITE_BLOCK,
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
            NetherTraits.compostable(NetherRender.cutout()),
            BlockAnchorTreeVine::new
    );
    // Nether Sakura
    public static final NetherSakuraMaterial MAT_NETHER_SAKURA = new NetherSakuraMaterial().init();
    public static final Block NETHER_SAKURA_LEAVES = registerLeaves(
            "nether_sakura_leaves",
            MAT_NETHER_SAKURA.getSapling(),
            p -> new BlockNetherSakuraLeaves(MAT_NETHER_SAKURA.getSapling(), p)
    );
    // Soul lily //
    public static final Block SOUL_LILY = registerBlockNI(
            "soul_lily",
            NetherTraits.and(
                    NetherRender.cutoutAnd(NetherSurvival.soulGround()),
                    BlockTraits.MINEABLE_WITH.needsAxe()
            ),
            BlockSoulLily::new
    );
    public static final Block SOUL_LILY_SAPLING = registerBlock("soul_lily_sapling", NetherTraits.compostable(NetherRender.cutoutAnd(NetherSurvival.soulGroundOrFarmland())), BlockSoulLilySapling::new);
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
    public static final Block EYE_VINE = registerBlockNI("eye_vine", NetherTraits.compostable(NetherTraits.of(ModelTraitLibrary.externalModelDelegatedItem())), BlockEyeVine::new);

    public static final Block POTTED_PLANT = registerBlockNI(
            "potted_plant",
            NetherTraits.and(NetherRender.cutout(), BlockTraits.MINEABLE_WITH.needsHoe()),
            BlockPottedPlant::new
    );
    public static final Block VEINED_SAND = registerBlockNI(
            "veined_sand",
            Blocks.SAND,
            BlockVeinedSand::new,
            NetherTags.NETHER_SAND
    );

    public static final Block NETHERRACK_SLAB = registerSlab("netherrack_slab", Blocks.NETHERRACK, true);
    public static final Block NETHERRACK_STAIR = registerStairs("netherrack_stairs", Blocks.NETHERRACK, true);
    public static final Block NETHERRACK_WALLS = registerWall("netherrack_wall", Blocks.NETHERRACK);


    // DEFERED BLOCKS //
    public static final Block LUMABUS_SEED = registerBlock(
            "lumabus_seed",
            NetherTraits.compostable(NetherRender.cutout()),
            p -> new BlockLumabusSeed(p, LUMABUS_VINE, () -> NetherVines.LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    );

    public static final Block GOLDEN_LUMABUS_SEED = registerBlock(
            "golden_lumabus_seed",
            NetherTraits.compostable(NetherRender.cutout()),
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
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTrait(model)
                .addTags(tags)
                .buildAndRegister();
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
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTrait(BlockTraits.LOOT_TABLE.dropLeaves(sapling))
                // BehaviourLeaves (still on these classes, for the LEAVES tags and the creative tab) extends
                // BehaviourCompostable and overrides compostingChance() to 0.3f - but that marker only ever
                // produced the c:compostable item tag, never a composter entry. The trait registers the real
                // one, at the 0.3f the marker always intended.
                .addTrait(CompostableBlockTrait.withChance(0.3f))
                .buildAndRegister();
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
            TagKey<Block>... tags
    ) {
        // bclib's BaseStairsBlock (which generated its own models) is gone, and the vanilla StairBlock this
        // replaced it with has no model source at all - without this trait the block gets no blockstate and
        // its item falls back to a flat icon on a texture that does not exist.
        return registerStairs(name, source, fireproof, ModelTraitLibrary.stairs(() -> source), tags);
    }

    /**
     * @param model the model trait to build the stairs with; pass a {@link NetherModels} factory for
     *              stairs whose textures are not simply their material block's.
     */
    @SafeVarargs
    public static Block registerStairs(
            String name,
            Block source,
            boolean fireproof,
            BlockModelTrait model,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.StairBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.StairBlock(source.defaultBlockState(), def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTags(tags);

        definition.addTrait(model);

        Block stairs = definition.buildAndRegister();

        if (stairs.defaultBlockState().ignitedByLava())
            addFuel(source, stairs);
        if (ModCore.isDatagen())
            RecipesHelper.makeStairsRecipe(source, stairs);

        return stairs;
    }

    @SafeVarargs
    public static Block registerSlab(String name, Block source, boolean fireproof, TagKey<Block>... tags) {
        return registerSlab(name, source, fireproof, ModelTraitLibrary.slab(() -> source), tags);
    }

    /**
     * @param model the model trait to build the slab with; pass {@code ModelTraitLibrary.externalModel()}
     *              for a slab whose blockstate is hand-authored in src/main/resources.
     */
    public static Block registerSlab(
            String name,
            Block source,
            boolean fireproof,
            BlockModelTrait model,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.SlabBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.SlabBlock(def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTags(tags);

        // See registerStairs: replaces the model generation bclib's BaseSlabBlock used to provide.
        definition.addTrait(model);

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
            Function<BlockBehaviour.Properties, BlockBase> factory
    ) {
        Block roof = getBlockRegistry()
                .<BlockBase>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .buildAndRegister();

        addFuel(source, roof);
        if (ModCore.isDatagen())
            RecipesHelper.makeRoofRecipe(source, roof);

        return roof;
    }

    public static Block registerButton(String name, Block source, BlockSetType type) {
        return registerButton(name, source, type, ModelTraitLibrary.button(() -> source));
    }

    /**
     * @param model the model trait to build the button with; pass a {@link NetherModels} factory for a
     *              button whose texture is not simply its material block's.
     */
    public static Block registerButton(String name, Block source, BlockSetType type, BlockModelTrait model) {
        Block button = getBlockRegistry()
                .<net.minecraft.world.level.block.ButtonBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.ButtonBlock(type, 30, def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTrait(model)
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
        return registerBlock(
                name,
                propertiesSource,
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
        T result = registerBlock(name, propertiesSource, NetherTraits.of(model), factory, tags);
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

    public static Block registerWall(String name, Block source) {
        return registerWall(name, source, ModelTraitLibrary.wall(() -> source));
    }

    /**
     * @param model the model trait to build the wall with; see {@link #registerSlab} for when to pass
     *              {@code ModelTraitLibrary.externalModel()}.
     */
    public static Block registerWall(String name, Block source, BlockModelTrait model) {
        final var definition = getBlockRegistry()
                .<net.minecraft.world.level.block.WallBlock>defineDefaultBlock(
                        name,
                        def -> new net.minecraft.world.level.block.WallBlock(def.getProperties())
                )
                .replacePropertiesWithCopy(source)
                .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                .addTags(BlockTags.WALLS);

        // See registerStairs: replaces the model generation bclib's BaseWallBlock used to provide.
        definition.addTrait(model);

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
        Block block = getBlockRegistry()
                .<BlockNetherFurnace>defineDefaultBlock(name, def -> new BlockNetherFurnace(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTags(CommonPoiTags.ARMORER_WORKSTATION)
                .buildAndRegister();

        if (ModCore.isDatagen())
            RecipesHelper.makeRoundRecipe(source, block, "nether_furnace", RecipeCategory.DECORATIONS);


        return block;
    }

    private static Block registerStalactite(String name, Block source) {
        Block block = getBlockRegistry()
                .<BlockStalactite>defineDefaultBlock(name, def -> new BlockStalactite(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .buildAndRegister();

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
            Function<BlockBehaviour.Properties, BlockFireBowl> factory
    ) {
        final boolean isNetherite = name.startsWith("netherite");
        Block block = getBlockRegistry()
                .<BlockFireBowl>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(source)
                .addTrait(NetherRender.cutout())
                .buildAndRegister();

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
