package org.betterx.betternether.registry;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.complexmaterials.set.wood.BarStool;
import org.betterx.bclib.complexmaterials.set.wood.Chair;
import org.betterx.bclib.complexmaterials.set.wood.Taburet;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.furniture.block.BaseBarStool;
import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.bclib.furniture.block.BaseTaburet;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.blocks.complex.*;
import org.betterx.betternether.blocks.complex.slots.VanillaNetherWood;
import org.betterx.betternether.blocks.complex.slots.VanillaWood;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.features.configured.NetherVines;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.complex.api.equipment.ToolTiers;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;

import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class NetherBlocks {
    public static final Block NETHER_BRICK_TILE_LARGE = registerBlock("nether_brick_tile_large", new BNNetherBrick());

    // Reed //
    public static final Block NETHER_REED_STEM = registerBlock("nether_reed_stem", new BlockNetherReed());
    public static final NetherReedMaterial MAT_REED = new NetherReedMaterial().init();

    // Stalagnate //
    public static final StalagnateMaterial MAT_STALAGNATE = new StalagnateMaterial().init();

    // Willow //
    public static final WillowMaterial MAT_WILLOW = new WillowMaterial().init();
    public static final Block WILLOW_LEAVES = registerBlock(
            "willow_leaves",
            new BlockWillowLeaves(MAT_WILLOW.getSapling())
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
            new BlockOre(
                    () -> NetherItems.CINCINNASITE,
                    1,
                    3,
                    0,
                    ToolTiers.IRON_TOOL.blockTag,
                    true
            )
    );
    public static final Block CINCINNASITE_BLOCK = registerBlock("cincinnasite_block", new BlockCincinnasite());
    public static final Block CINCINNASITE_FORGED = registerBlock("cincinnasite_forged", new BlockCincinnasite());
    public static final Block CINCINNASITE_PILLAR = registerBlock("cincinnasite_pillar", new BlockCincinnasitPillar());
    public static final Block CINCINNASITE_BRICKS = registerBlock("cincinnasite_bricks", new BlockCincinnasite());
    public static final Block CINCINNASITE_BRICK_PLATE = registerBlock(
            "cincinnasite_brick_plate",
            new BlockCincinnasite()
    );
    public static final Block CINCINNASITE_STAIRS = registerStairs("cincinnasite_stairs", CINCINNASITE_FORGED, false);
    public static final Block CINCINNASITE_SLAB = registerSlab("cincinnasite_slab", CINCINNASITE_FORGED, false);
    public static final Block TABURET_CINCINNASITE = registerTaburet("taburet_cincinnasite", CINCINNASITE_SLAB);
    public static final Block CHAIR_CINCINNASITE = registerChair("chair_cincinnasite", CINCINNASITE_SLAB);
    public static final Block BAR_STOOL_CINCINNASITE = registerBarStool("bar_stool_cincinnasite", CINCINNASITE_SLAB);
    public static final Block CINCINNASITE_BUTTON = registerBlock(
            "cincinnasite_button",
            BaseButtonBlock.from(CINCINNASITE_FORGED, BlockSetType.GOLD)
    );
    public static final Block CINCINNASITE_PLATE = registerPlate(
            "cincinnasite_plate",
            CINCINNASITE_FORGED,
            BlockSetType.GOLD
    );
    public static final Block CINCINNASITE_LANTERN = registerBlock(
            "cincinnasite_lantern",
            new BlockCincinnasiteLantern()
    );
    public static final Block CINCINNASITE_TILE_LARGE = registerBlock(
            "cincinnasite_tile_large",
            new BlockCincinnasite()
    );
    public static final Block CINCINNASITE_TILE_SMALL = registerBlock(
            "cincinnasite_tile_small",
            new BlockCincinnasite()
    );
    public static final Block CINCINNASITE_CARVED = registerBlock("cincinnasite_carved", new BlockCincinnasite());
    public static final Block CINCINNASITE_WALL = registerWall("cincinnasite_wall", CINCINNASITE_FORGED);
    public static final Block CINCINNASITE_BRICKS_PILLAR = registerBlock(
            "cincinnasite_bricks_pillar",
            new BNPillar.Metal(CINCINNASITE_FORGED)
    );
    public static final Block CINCINNASITE_BARS = registerBlock(
            "cincinnasite_bars",
            new BNPane.Metal(CINCINNASITE_FORGED, true)
    );
    public static final Block CINCINNASITE_PEDESTAL = registerBlock(
            "cincinnasite_pedestal",
            new BlockCincinnasitePedestal()
    );
    public static final Block CINCINNASITE_FRAME = registerBlock("cincinnasite_frame", new BlockCincinnasiteFrame());
    public static final Block CINCINNASITE_LANTERN_SMALL = registerBlock(
            "cincinnasite_lantern_small",
            new BlockSmallLantern.Metal()
    );
    public static final Block CINCINNASITE_CHAIN = registerBlock("cincinnasite_chain", new BNChain());
    // Ruby //
    public static final Block NETHER_RUBY_ORE = registerBlock(
            "nether_ruby_ore",
            new BlockOre(
                    () -> NetherItems.NETHER_RUBY,
                    1,
                    2,
                    5,
                    ToolTiers.DIAMOND_TOOL.blockTag,
                    true
            )
    );
    public static final Block NETHER_RUBY_BLOCK = registerBlock("nether_ruby_block", new BlockNetherRuby());
    public static final Block NETHER_RUBY_STAIRS = registerStairs("nether_ruby_stairs", NETHER_RUBY_BLOCK, true);
    public static final Block NETHER_RUBY_SLAB = registerSlab("nether_ruby_slab", NETHER_RUBY_BLOCK, true);
    // Vanilla Ores
    public static final Block NETHER_LAPIS_ORE = registerBlock(
            "nether_lapis_ore",
            new BlockOre(
                    () -> NetherItems.LAPIS_PILE,
                    3,
                    6,
                    3,
                    ToolTiers.IRON_TOOL.blockTag,
                    false
            )
    );
    public static final Block NETHER_REDSTONE_ORE = registerBlock("nether_redstone_ore", new RedstoneOreBlock());
    // Bricks //
    public static final Block NETHER_BRICK_TILE_SMALL = registerBlock("nether_brick_tile_small", new BNNetherBrick());
    public static final Block NETHER_BRICK_WALL = registerWall("nether_brick_wall", NETHER_BRICK_TILE_LARGE);
    public static final Block NETHER_BRICK_TILE_SLAB = registerSlab(
            "nether_brick_tile_slab",
            NETHER_BRICK_TILE_SMALL,
            false
    );
    public static final Block NETHER_BRICK_TILE_STAIRS = registerStairs(
            "nether_brick_tile_stairs",
            NETHER_BRICK_TILE_SMALL,
            false
    );
    // Bone //
    public static final Block BONE_BLOCK = registerBlock("bone_block", new BNBoneBlock());
    public static final Block BONE_STAIRS = registerStairs("bone_stairs", BONE_BLOCK, false);
    public static final Block BONE_SLAB = registerSlab("bone_slab", BONE_BLOCK, false);
    public static final Block BONE_BUTTON = registerButton("bone_button", BONE_BLOCK, BlockSetType.CRIMSON);
    public static final Block BONE_PLATE = registerPlate("bone_plate", BONE_BLOCK, BlockSetType.CRIMSON);
    public static final Block BONE_WALL = registerWall("bone_wall", BONE_BLOCK);
    public static final Block BONE_TILE = registerBlock("bone_tile", new BNBoneBlock());
    public static final Block BONE_REED_DOOR = registerBlock(
            "bone_reed_door",
            new BNWoodlikeDoor(BONE_BLOCK, WoodType.CRIMSON)
    );
    public static final Block BONE_CINCINNASITE_DOOR = registerBlock(
            "bone_cincinnasite_door",
            new BNWoodlikeDoor(BONE_BLOCK, WoodType.CRIMSON)
    );
    // Quartz Glass //
    public static final Block QUARTZ_GLASS = registerBlock("quartz_glass", new BNGlass(Blocks.GLASS));
    public static final Block QUARTZ_GLASS_FRAMED = registerBlock(
            "quartz_glass_framed",
            new BNGlass(CINCINNASITE_BLOCK)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed",
            QUARTZ_GLASS_FRAMED
    );
    public static final Block QUARTZ_GLASS_PANE = registerBlock(
            "quartz_glass_pane",
            new BNPane.Glass(QUARTZ_GLASS, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_pane",
            QUARTZ_GLASS_PANE,
            false
    );
    public static final Block QUARTZ_GLASS_FRAMED_PANE = registerBlock(
            "quartz_glass_framed_pane",
            new BNPane.Metal(CINCINNASITE_BLOCK, true)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed_pane",
            QUARTZ_GLASS_FRAMED_PANE,
            true
    );
    // Quartz Glass Colored //
    public static final ColoredGlassMaterial QUARTZ_GLASS_COLORED = new ColoredGlassMaterial(
            "quartz_glass",
            QUARTZ_GLASS
    );
    // Obsidian //
    public static final Block BLUE_WEEPING_OBSIDIAN = registerBlock(
            "blue_weeping_obsidian",
            new BlueWeepingObsidianBlock()
    );
    public static final Block WEEPING_OBSIDIAN = registerBlock(
            "weeping_obsidian",
            new VanillaWeepingObsidianBlock()
    );
    public static final Block BLUE_CRYING_OBSIDIAN = registerBlock(
            "blue_crying_obsidian",
            new BlueCryingObsidianBlock()
    );
    public static final Block OBSIDIAN_BRICKS = registerBlock(
            "obsidian_bricks",
            new BNObsidian()
    );
    public static final Block OBSIDIAN_BRICKS_STAIRS = registerStairs(
            "obsidian_bricks_stairs",
            OBSIDIAN_BRICKS,
            false
    );
    public static final Block OBSIDIAN_BRICKS_SLAB = registerSlab(
            "obsidian_bricks_slab",
            OBSIDIAN_BRICKS,
            false
    );
    public static final Block OBSIDIAN_TILE = registerBlock(
            "obsidian_tile",
            new BNObsidian()
    );
    public static final Block OBSIDIAN_TILE_SMALL = registerBlock(
            "obsidian_tile_small",
            new BNObsidian()
    );
    public static final Block OBSIDIAN_TILE_STAIRS = registerStairs(
            "obsidian_tile_stairs",
            OBSIDIAN_TILE_SMALL,
            false
    );
    public static final Block OBSIDIAN_TILE_SLAB = registerSlab(
            "obsidian_tile_slab",
            OBSIDIAN_TILE_SMALL,
            false
    );
    public static final Block OBSIDIAN_ROD_TILES = registerBlock(
            "obsidian_rod_tiles",
            new BNObsidian()
    );
    public static final Block OBSIDIAN_GLASS = registerBlock(
            "obsidian_glass",
            new BlockObsidianGlass()
    );
    public static final Block OBSIDIAN_GLASS_PANE = registerBlock(
            "obsidian_glass_pane",
            new BNPane.Glass(OBSIDIAN_GLASS, true)
    );
    public static final Block BLUE_OBSIDIAN = registerBlock(
            "blue_obsidian",
            new BNObsidian(BLUE_CRYING_OBSIDIAN)
    );
    public static final Block BLUE_OBSIDIAN_BRICKS = registerBlock(
            "blue_obsidian_bricks",
            new BNObsidian()
    );
    public static final Block BLUE_OBSIDIAN_BRICKS_STAIRS = registerStairs(
            "blue_obsidian_bricks_stairs",
            BLUE_OBSIDIAN_BRICKS,
            false
    );
    public static final Block BLUE_OBSIDIAN_BRICKS_SLAB = registerSlab(
            "blue_obsidian_bricks_slab",
            BLUE_OBSIDIAN_BRICKS,
            false
    );
    public static final Block BLUE_OBSIDIAN_TILE = registerBlock(
            "blue_obsidian_tile",
            new BNObsidian()
    );
    public static final Block BLUE_OBSIDIAN_TILE_SMALL = registerBlock(
            "blue_obsidian_tile_small",
            new BNObsidian()
    );
    public static final Block BLUE_OBSIDIAN_TILE_STAIRS = registerStairs(
            "blue_obsidian_tile_stairs",
            BLUE_OBSIDIAN_TILE_SMALL,
            false
    );
    public static final Block BLUE_OBSIDIAN_TILE_SLAB = registerSlab(
            "blue_obsidian_tile_slab",
            BLUE_OBSIDIAN_TILE_SMALL,
            false
    );
    public static final Block BLUE_OBSIDIAN_ROD_TILES = registerBlock(
            "blue_obsidian_rod_tiles",
            new BNObsidian()
    );
    public static final Block BLUE_OBSIDIAN_GLASS = registerBlock(
            "blue_obsidian_glass",
            new BlockObsidianGlass()
    );
    public static final Block BLUE_OBSIDIAN_GLASS_PANE = registerBlock(
            "blue_obsidian_glass_pane",
            new BNPane.Glass(BLUE_OBSIDIAN_GLASS, true)
    );
    // Soul Sandstone //
    public static final Block SOUL_SANDSTONE = registerMakeable2X2Soul(
            "soul_sandstone",
            new BlockSoulSandstone(),
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            Blocks.SOUL_SAND
    );
    public static final Block SOUL_SANDSTONE_CUT = registerMakeable2X2Soul(
            "soul_sandstone_cut",
            new BlockSoulSandstone(),
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            SOUL_SANDSTONE
    );
    public static final Block SOUL_SANDSTONE_CUT_STAIRS = registerStairs(
            "soul_sandstone_cut_stairs",
            SOUL_SANDSTONE_CUT,
            false,
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_CUT_SLAB = registerSlab(
            "soul_sandstone_cut_slab",
            SOUL_SANDSTONE_CUT,
            false
    );
    public static final Block SOUL_SANDSTONE_WALL = registerWall("soul_sandstone_wall", SOUL_SANDSTONE_CUT);
    public static final Block SOUL_SANDSTONE_SMOOTH = registerSoulBlock(
            "soul_sandstone_smooth",
            new BlockBase.Stone(Blocks.SANDSTONE)
    );
    public static final Block SOUL_SANDSTONE_CHISELED = registerMakeable2X2Soul(
            "soul_sandstone_chiseled",
            new BlockBase.Stone(Blocks.SANDSTONE),
            "soul_sandstone",
            RecipeCategory.BUILDING_BLOCKS,
            SOUL_SANDSTONE_SMOOTH
    );
    public static final Block SOUL_SANDSTONE_STAIRS = registerStairs(
            "soul_sandstone_stairs",
            SOUL_SANDSTONE,
            false,
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SMOOTH_STAIRS = registerStairs(
            "soul_sandstone_smooth_stairs",
            SOUL_SANDSTONE_SMOOTH,
            false,
            BlockTags.SOUL_SPEED_BLOCKS,
            BlockTags.SOUL_FIRE_BASE_BLOCKS
    );
    public static final Block SOUL_SANDSTONE_SLAB = registerSlab("soul_sandstone_slab", SOUL_SANDSTONE, false);
    public static final Block SOUL_SANDSTONE_SMOOTH_SLAB = registerSlab(
            "soul_sandstone_smooth_slab",
            SOUL_SANDSTONE_SMOOTH,
            false
    );
    // Basalt Bricks //
    public static final Block BASALT_BRICKS = registerMakeable2X2(
            "basalt_bricks",
            new BlockBase.Stone(FabricBlockSettings.copyOf(Blocks.BASALT)),
            "basalt_bricks",
            RecipeCategory.BUILDING_BLOCKS,
            Blocks.POLISHED_BASALT
    );
    public static final Block BASALT_BRICKS_STAIRS = registerStairs("basalt_bricks_stairs", BASALT_BRICKS, true);
    public static final Block BASALT_BRICKS_SLAB = registerSlab("basalt_bricks_slab", BASALT_BRICKS, true);
    public static final Block BASALT_BRICKS_WALL = registerWall("basalt_bricks_wall", BASALT_BRICKS);
    public static final Block BASALT_SLAB = registerSlab(
            "basalt_slab",
            Blocks.BASALT,
            false
    );
    public static final Block ORANGE_MUSHROOM = registerBlock("orange_mushroom", new BlockOrangeMushroom());
    public static final Block RED_MOLD = registerBlock("red_mold", new BlockRedMold());
    public static final Block GRAY_MOLD = registerBlock("gray_mold", new BlockGrayMold());
    public static final Block LUCIS_SPORE = registerBlock("lucis_spore", new BlockLucisSpore());
    public static final Block GIANT_LUCIS = registerBlock("giant_lucis", new BlockGiantLucis());
    public static final Block GIANT_MOLD_SAPLING = registerBlock("giant_mold_sapling", new BlockGiantMoldSapling());
    public static final Block JELLYFISH_MUSHROOM_SAPLING = registerBlock(
            "jellyfish_mushroom_sapling",
            new BlockJellyfishMushroomSapling()
    );
    public static final Block EYE_SEED = registerBlock("eye_seed", new BlockEyeSeed());
    // Grass //
    public static final Block NETHER_GRASS = registerBlock("nether_grass", new BlockNetherGrass.NetherGrass());
    public static final Block SWAMP_GRASS = registerBlock("swamp_grass", new BlockNetherGrass.SwampGrass());
    public static final Block SOUL_GRASS = registerBlock("soul_grass", new BlockSoulGrass());
    public static final Block JUNGLE_PLANT = registerBlock("jungle_plant", new BlockNetherGrass.JunglePlant());
    public static final Block BONE_GRASS = registerBlock("bone_grass", new BlockNetherGrass.BoneGrass());
    public static final Block SEPIA_BONE_GRASS = registerBlock("sepia_bone_grass", new BlockNetherGrass.SepiaBoneGrass());
    // Vines //
    public static final Block BLACK_VINE = registerBlock(
            "black_vine",
            new BlockBlackVine()

    );
    public static final Block BLOOMING_VINE = registerBlock(
            "blooming_vine",
            new BlockBlackVine()
    );
    public static final Block GOLDEN_VINE = registerBlock(
            "golden_vine",
            new BlockGoldenVine()
    );

    public static final BlockLumabusVine LUMABUS_VINE = registerBlockNI("lumabus_vine", new BlockLumabusVine());
    public static final BlockLumabusVine GOLDEN_LUMABUS_VINE = registerBlockNI(
            "golden_lumabus_vine",
            new BlockLumabusVine()
    );


    // Small Plants
    public static final Block SOUL_VEIN = registerBlock("soul_vein", new BlockSoulVein());
    public static final Block BONE_MUSHROOM = registerBlock("bone_mushroom", new BlockBoneMushroom());
    public static final Block BLACK_BUSH = registerBlock("black_bush", new BlockBlackBush());
    public static final Block INK_BUSH = registerBlockNI("ink_bush", new BlockInkBush());
    public static final Block INK_BUSH_SEED = registerBlock("ink_bush_seed", new BlockInkBushSeed());
    public static final Block SMOKER = registerBlock("smoker", new BlockSmoker());
    public static final Block EGG_PLANT = registerBlock("egg_plant", new BlockEggPlant());
    public static final Block BLACK_APPLE = registerBlockNI("black_apple", new BlockBlackApple());
    public static final Block BLACK_APPLE_SEED = registerBlock("black_apple_seed", new BlockBlackAppleSeed());
    public static final Block MAGMA_FLOWER = registerBlock("magma_flower", new BlockMagmaFlower());
    public static final Block FEATHER_FERN = registerBlock("feather_fern", new BlockFeatherFern());
    public static final Block MOSS_COVER = registerBlock("moss_cover", new BlockMossCover());
    public static final Block NEON_EQUISETUM = registerBlock("neon_equisetum", new BlockNeonEquisetum());
    public static final Block HOOK_MUSHROOM = registerBlock("hook_mushroom", new BlockHookMushroom());
    public static final Block WHISPERING_GOURD_VINE = registerBlock(
            "whispering_gourd_vine",
            new BlockWhisperingGourdVine()
    );
    public static final Block WHISPERING_GOURD = registerBlock("whispering_gourd", new BlockWhisperingGourd());
    public static final Block WHISPERING_GOURD_LANTERN = registerBlock(
            "whispering_gourd_lantern",
            new BlockWhisperingGourdLantern()
    );
    // Cactuses //
    public static final Block AGAVE = registerBlock("agave", new BlockAgave());
    public static final Block BARREL_CACTUS = registerBlock("barrel_cactus", new BlockBarrelCactus());
    public static final Block NETHER_CACTUS = registerBlock("nether_cactus", new BlockNetherCactus());
    // Wall plants
    public static final Block WALL_MOSS = registerBlock("wall_moss", new BlockPlantWall(MapColor.COLOR_RED));
    public static final Block WALL_MUSHROOM_BROWN = registerBlock(
            "wall_mushroom_brown",
            new BlockPlantWall(MapColor.COLOR_BROWN)
    );
    public static final Block WALL_MUSHROOM_RED = registerBlock(
            "wall_mushroom_red",
            new BlockPlantWall(MapColor.COLOR_RED)
    );
    public static final Block JUNGLE_MOSS = registerBlock(
            "jungle_moss",
            new BlockPlantWall(MapColor.COLOR_LIGHT_GREEN)
    );
    // Decorations //
    public static final Block PIG_STATUE_RESPAWNER = registerBlock("pig_statue_respawner", new BlockStatueRespawner());
    public static final Block CINCINNASITE_POT = registerBlock(
            "cincinnasite_pot",
            new BlockBNPot.Metal(CINCINNASITE_BLOCK)
    );
    public static final Block BRICK_POT = registerBlock("brick_pot", new BlockBNPot.Stone(Blocks.NETHER_BRICKS));
    public static final Block GEYSER = registerBlock("geyser", new BlockGeyser());
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
            NetherItems.CINCINNASITE_INGOT
    );
    public static final Block BRICKS_FIRE_BOWL = registerFireBowl(
            "bricks_fire_bowl",
            NETHER_BRICK_TILE_LARGE,
            Blocks.NETHERRACK,
            Items.NETHER_BRICK
    );
    public static final Block NETHERITE_FIRE_BOWL = registerFireBowl(
            "netherite_fire_bowl",
            Blocks.NETHERITE_BLOCK,
            Blocks.NETHERRACK,
            Items.NETHERITE_INGOT
    );
    public static final Block CINCINNASITE_FIRE_BOWL_SOUL = registerFireBowl(
            "cincinnasite_fire_bowl_soul",
            CINCINNASITE_FORGED,
            Blocks.SOUL_SAND,
            NetherItems.CINCINNASITE_INGOT
    );
    public static final Block BRICKS_FIRE_BOWL_SOUL = registerFireBowl(
            "bricks_fire_bowl_soul",
            NETHER_BRICK_TILE_LARGE,
            Blocks.SOUL_SAND,
            Items.NETHER_BRICK
    );
    public static final Block NETHERITE_FIRE_BOWL_SOUL = registerFireBowl(
            "netherite_fire_bowl_soul",
            Blocks.NETHERITE_BLOCK,
            Blocks.SOUL_SAND,
            Items.NETHERITE_INGOT
    );
    // Terrain //
    public static final BlockTerrain NETHERRACK_MOSS = registerBlock(
            "netherrack_moss",
            new BlockTerrain(),
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockNetherMycelium NETHER_MYCELIUM = registerBlock(
            "nether_mycelium",
            new BlockNetherMycelium(),
            CommonBlockTags.MYCELIUM,
            CommonBlockTags.NETHER_MYCELIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK,
            org.betterx.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM
    );
    public static final BlockTerrain JUNGLE_GRASS = registerBlock(
            "jungle_grass",
            new BlockTerrain(),
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain MUSHROOM_GRASS = registerBlock(
            "mushroom_grass",
            new BlockTerrain(),
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SEPIA_MUSHROOM_GRASS = registerBlock(
            "sepia_mushroom_grass",
            new BlockTerrain(),
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SWAMPLAND_GRASS = registerBlock(
            "swampland_grass",
            new BlockTerrain(),
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final Block FARMLAND = registerBlock(
            "farmland",
            new BlockFarmland()
    );
    public static final BlockTerrain CEILING_MUSHROOMS = registerBlock(
            "ceiling_mushrooms",
            new BlockTerrain(),
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // Roofs //
    public static final Block ROOF_TILE_NETHER_BRICKS = registerRoof("roof_tile_nether_bricks", Blocks.NETHER_BRICKS);
    public static final Block ROOF_TILE_NETHER_BRICKS_STAIRS = registerStairs(
            "roof_tile_nether_bricks_stairs",
            ROOF_TILE_NETHER_BRICKS,
            false
    );
    public static final Block ROOF_TILE_NETHER_BRICKS_SLAB = registerSlab(
            "roof_tile_nether_bricks_slab",
            ROOF_TILE_NETHER_BRICKS,
            false
    );
    public static final Block ROOF_TILE_CINCINNASITE = registerRoof("roof_tile_cincinnasite", CINCINNASITE_FORGED);
    public static final Block ROOF_TILE_CINCINNASITE_STAIRS = registerStairs(
            "roof_tile_cincinnasite_stairs",
            ROOF_TILE_CINCINNASITE,
            false
    );
    public static final Block ROOF_TILE_CINCINNASITE_SLAB = registerSlab(
            "roof_tile_cincinnasite_slab",
            ROOF_TILE_CINCINNASITE,
            false
    );
    // Craft Stations //
    public static final Block BLACKSTONE_FURNACE = registerFurnace("blackstone_furnace", Blocks.BLACKSTONE);
    public static final Block BASALT_FURNACE = registerFurnace("basalt_furnace", Blocks.BASALT);
    public static final Block NETHERRACK_FURNACE = registerFurnace("netherrack_furnace", Blocks.NETHERRACK);
    public static final Block CINCINNASITE_FORGE = registerBlock("cincinnasite_forge", new BlockCincinnasiteForge());
    public static final Block NETHER_BREWING_STAND = registerBlock("nether_brewing_stand", new BNBrewingStand());
    public static final Block CINCINNASITE_ANVIL = registerBlock(
            "cincinnasite_anvil",
            new BlockCincinnasiteAnvil(),
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
    public static final Block CHEST_OF_DRAWERS = registerBlock("chest_of_drawers", new BlockChestOfDrawers());

    private static BlockRegistry BLOCKS_REGISTRY;
    // Rubeus //
    public static final RubeusMaterial MAT_RUBEUS = new RubeusMaterial().init();
    public static final Block RUBEUS_LEAVES = registerBlock(
            "rubeus_leaves",
            new BlockRubeusLeaves(MAT_RUBEUS.getSapling())
    );
    // Mushroom Fir //
    public static final MushroomFirMaterial MAT_MUSHROOM_FIR = new MushroomFirMaterial().init();
    public static final Block TRIMMED_MUSHROOM_FIR_CHEST = registerBlock(
            "mushroom_fir_trimmed_chest",
            new BaseChestBlock.Wood(MAT_MUSHROOM_FIR.getBlock(WoodSlots.PLANKS))
    );
    // Mushroom //
    public static final NetherMushroomMaterial MAT_NETHER_MUSHROOM = new NetherMushroomMaterial().init();
    // Anchor Tree
    public static final AnchorTreeMaterial MAT_ANCHOR_TREE = new AnchorTreeMaterial().init();
    public static final Block ANCHOR_TREE_LEAVES = registerBlock(
            "anchor_tree_leaves",
            new BNLeaves(
                    MAT_ANCHOR_TREE.getSapling(),
                    MapColor.COLOR_GREEN
            )
    );
    public static final Block ANCHOR_TREE_VINE = registerBlockNI(
            "anchor_tree_vine",
            new BlockAnchorTreeVine()
    );
    // Nether Sakura
    public static final NetherSakuraMaterial MAT_NETHER_SAKURA = new NetherSakuraMaterial().init();
    public static final Block NETHER_SAKURA_LEAVES = registerBlock(
            "nether_sakura_leaves",
            new BlockNetherSakuraLeaves(MAT_NETHER_SAKURA.getSapling())
    );
    // Soul lily //
    public static final Block SOUL_LILY = registerBlockNI("soul_lily", new BlockSoulLily());
    public static final Block SOUL_LILY_SAPLING = registerBlock("soul_lily_sapling", new BlockSoulLilySapling());
    // Large & Small Mushrooms //
    public static final Block RED_LARGE_MUSHROOM = registerBlockNI("red_large_mushroom", new BlockRedLargeMushroom());
    public static final Block BROWN_LARGE_MUSHROOM = registerBlockNI(
            "brown_large_mushroom",
            new BlockBrownLargeMushroom()
    );
    // Lucis //
    public static final Block LUCIS_MUSHROOM = registerBlockNI("lucis_mushroom", new BlockLucisMushroom());
    // Giant Mold //
    public static final Block GIANT_MOLD = registerBlockNI("giant_mold", new BlockGiantMold());
    public static final Block JELLYFISH_MUSHROOM = registerBlockNI("jellyfish_mushroom", new BlockJellyfishMushroom());
    // Eyes //
    public static final Block EYEBALL = registerBlockNI("eyeball", new BlockEyeball());
    public static final Block EYEBALL_SMALL = registerBlockNI("eyeball_small", new BlockEyeballSmall());
    public static final Block EYE_VINE = registerBlockNI(
            "eye_vine",
            new BlockEyeVine()
    );

    public static final Block POTTED_PLANT = registerBlockNI("potted_plant", new BlockPottedPlant());
    public static final Block VEINED_SAND = registerBlockNI(
            "veined_sand",
            new BlockVeinedSand(),
            NetherTags.NETHER_SAND
    );


    // DEFERED BLOCKS //
    public static final Block LUMABUS_SEED = registerBlock(
            "lumabus_seed",
            new BlockLumabusSeed(LUMABUS_VINE, () -> NetherVines.LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    );

    public static final Block GOLDEN_LUMABUS_SEED = registerBlock(
            "golden_lumabus_seed",
            new BlockLumabusSeed(GOLDEN_LUMABUS_VINE, () -> NetherVines.GOLDEN_LUMABUS_VINE.getHolder(WorldState.registryAccess()))
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
    public static <T extends Block> T registerBlock(String name, T block, TagKey<Block>... tags) {
        registerBlockDirectly(name, block, tags);
        return block;
    }

    @SafeVarargs
    private static <B extends Block> B registerBlockNI(String name, B block, TagKey<Block>... tags) {
        return registerBlock(name, block, false, tags);
    }

    @SafeVarargs
    public static void registerBlockDirectly(String name, Block block, TagKey<Block>... tags) {
        registerBlock(name, block, true, tags);
    }

    @SafeVarargs
    private static <B extends Block> B registerBlock(String name, B block, boolean hasItem, TagKey<Block>... tags) {
        final BlockRegistry blockRegistry = getBlockRegistry();
        if (hasItem) {
            blockRegistry.register(name, block, tags);
        } else {
            blockRegistry.registerBlockOnly(name, block, tags);
        }

        return block;
    }

    private static void addFuel(Block source, Block result) {
        if (source.defaultBlockState().ignitedByLava()) {
            FuelRegistry.INSTANCE.add(result, 40);
        }
    }

    @SafeVarargs
    public static Block registerStairs(
            String name,
            Block source,
            boolean fireproof,
            TagKey<Block>... tags
    ) {
        Block stairs = BaseStairsBlock.from(source, fireproof);

        registerBlockDirectly(name, stairs, tags);
        if (stairs.defaultBlockState().ignitedByLava())
            addFuel(source, stairs);
        if (ModCore.isDatagen())
            RecipesHelper.makeStairsRecipe(source, stairs);

        return stairs;
    }

    @SafeVarargs
    public static Block registerSlab(String name, Block source, boolean fireproof, TagKey<Block>... tags) {
        Block slab = BaseSlabBlock.from(source, fireproof);

        registerBlockDirectly(name, slab, tags);
        if (slab.defaultBlockState().ignitedByLava())
            addFuel(source, slab);
        if (ModCore.isDatagen())
            RecipesHelper.makeSlabRecipe(source, slab);

        return slab;
    }

    private static Block registerRoof(String name, Block source) {
        Block roof = BlockBase.from(source);

        registerBlockDirectly(name, roof);
        addFuel(source, roof);
        if (ModCore.isDatagen())
            RecipesHelper.makeRoofRecipe(source, roof);

        return roof;
    }

    public static Block registerButton(String name, Block source, BlockSetType type) {
        Block button = BaseButtonBlock.from(source, type);

        registerBlockDirectly(name, button);
        addFuel(source, button);
        if (ModCore.isDatagen())
            RecipesHelper.makeButtonRecipe(source, button);

        return button;
    }

    public static Block registerPlate(String name, Block source, BlockSetType type) {
        Block plate = BasePressurePlateBlock.from(source, type);

        registerBlockDirectly(name, plate);
        addFuel(source, plate);
        if (ModCore.isDatagen())
            RecipesHelper.makePlateRecipe(source, plate);

        return plate;
    }


    public static Block registerSoulBlock(String name, Block block) {
        return registerBlock(
                name,
                block,
                BlockTags.SOUL_FIRE_BASE_BLOCKS,
                BlockTags.SOUL_SPEED_BLOCKS
        );
    }

    public static Block registerMakeable2X2Soul(
            String name,
            Block result,
            String group,
            RecipeCategory category,
            Block source
    ) {
        final Block block = registerMakeable2X2(name, result, group, category, source,
                BlockTags.SOUL_FIRE_BASE_BLOCKS, BlockTags.SOUL_SPEED_BLOCKS
        );
        ;
        return block;
    }

    public static Block registerMakeable2X2(
            String name,
            Block result,
            String group,
            RecipeCategory category,
            Block source,
            TagKey<Block>... tags
    ) {

        registerBlockDirectly(name, result, tags);
        if (ModCore.isDatagen())
            RecipesHelper.makeSimpleRecipe2(source, result, 4, group, category);

        return result;
    }

    public static Block registerWall(String name, Block source) {
        Block wall = BNWall.from(source);

        registerBlockDirectly(name, wall, BlockTags.WALLS);
        if (ModCore.isDatagen())
            RecipesHelper.makeWallRecipe(source, wall);

        return wall;
    }


    public static Block registerTaburet(String name, Block source) {
        Block block = BaseTaburet.from(source);

        registerBlockDirectly(name, block, BlockTags.MINEABLE_WITH_AXE);
        addFuel(source, block);
        RecipesHelper.addProvider(ctx -> Taburet.makeTaburetRecipe(
                ctx.context,
                BetterNether.C.mk(name),
                block,
                source
        ));


        return block;
    }

    public static Block registerChair(String name, Block source) {
        Block block = BaseChair.from(source, NETHER_BRICK_TILE_LARGE);

        registerBlockDirectly(name, block, BlockTags.MINEABLE_WITH_AXE);
        addFuel(source, block);
        RecipesHelper.addProvider(ctx -> Chair.makeChairRecipe(
                ctx.context,
                BetterNether.C.mk(name),
                block,
                source
        ));


        return block;
    }

    public static Block registerBarStool(String name, Block source) {
        Block block = BaseBarStool.from(source, NETHER_BRICK_TILE_LARGE);

        registerBlockDirectly(name, block, BlockTags.MINEABLE_WITH_PICKAXE);
        addFuel(source, block);
        RecipesHelper.addProvider(ctx -> BarStool.makeBarStoolRecipe(
                ctx.context,
                BetterNether.C.mk(name),
                block,
                source
        ));


        return block;
    }

    public static Block registerFurnace(String name, Block source) {
        Block block = new BlockNetherFurnace(source);

        registerBlockDirectly(name, block);
        if (ModCore.isDatagen())
            RecipesHelper.makeRoundRecipe(source, block, "nether_furnace", RecipeCategory.DECORATIONS);


        return block;
    }

    private static Block registerStalactite(String name, Block source) {
        Block block = new BlockStalactite(source);

        registerBlockDirectly(name, block);
        if (ModCore.isDatagen())
            RecipesHelper.makeSimpleRecipe2(block, source, 1, "nether_stalactite", RecipeCategory.DECORATIONS);


        return block;
    }

    private static Block registerFireBowl(String name, Block source, Block inside, Item leg) {
        final boolean isNetherite = name.startsWith("netherite");
        Block block = isNetherite ? new BlockFireBowl.Metal(source) : BlockFireBowl.from(source);

        registerBlockDirectly(name, block);
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
