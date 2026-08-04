package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherDecorBlocks;
import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;
import org.betterx.betternether.registry.block.NetherGlassBlocks;
import org.betterx.betternether.registry.block.NetherLightBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherWallPlantBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherFoodItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import de.ambertation.wover.sets.api.blocks.SlotType;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class NetherBlockRecipesProvider extends WoverRecipeProvider {
    public NetherBlockRecipesProvider(ModCore modCore) {
        super(modCore, "BetterNether - Block Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(
                             BetterNether.C.id("whispering_gourd_seeds"),
                             NetherVineBlocks.WHISPERING_GOURD_VINE
                     )
                     .shape("##")
                     .addMaterial('#', NetherCropBlocks.WHISPERING_GOURD)
                     .group("seeds")
                     .outputCount(1)
                     .category(RecipeCategory.MISC)
                     .build(context);

        // The trimmed mushroom-fir chest builds its recipe from a BlockTraits.RECIPE trait on its block
        // definition (see NetherBlocks.registerTrimmedChest), the same way the wood sets' own chests do.

        RecipeBuilder.crafting(BetterNether.C.id("activator_rail"), Items.ACTIVATOR_RAIL)
                     .shape("XSX", "X#X", "XSX")
                     .addMaterial('#', Items.REDSTONE_TORCH)
                     .addMaterial('S', Items.STICK)
                     .addMaterial('X', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("activator_rail")
                     .outputCount(6)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("black_apple_seed"), NetherCropBlocks.BLACK_APPLE_SEED)
                     .shapeless()
                     .addMaterial('#', NetherFoodItems.BLACK_APPLE)
                     .group("nether_black_apple_seed")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bn_bone_block"), NetherStoneBlocks.BONE_BLOCK)
                     .shape("##", "##")
                     .addMaterial('#', Items.BONE_BLOCK)
                     .group("nether_bn_bone_block")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("bone_cin_door"), NetherStoneBlocks.BONE_CINCINNASITE_DOOR)
                     .shape("AB", "BB", "BA")
                     .addMaterial('A', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', NetherStoneBlocks.BONE_BLOCK)
                     .group("nether_bone_cin_door")
                     .outputCount(3)
                     .category(RecipeCategory.REDSTONE)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bone_tile"), NetherStoneBlocks.BONE_TILE)
                     .shape("#", "#")
                     .addMaterial('#', NetherStoneBlocks.BONE_SLAB)
                     .group("nether_bone_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("brick_pot"), NetherDecorBlocks.BRICK_POT)
                     .shape("#N#", " # ")
                     .addMaterial('#', Items.NETHER_BRICK)
                     .addMaterial('N', Items.SOUL_SAND)
                     .group("nether_brick_pot")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_bars"), NetherMetalBlocks.CINCINNASITE_BARS)
                     .shape("###", "###")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_bars")
                     .outputCount(16)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_block"), NetherMetalBlocks.CINCINNASITE_BLOCK)
                     .shape("##", "##")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE)
                     .group("nether_cincinnasite_block")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_brick_plate"),
                             NetherMetalBlocks.CINCINNASITE_BRICK_PLATE
                     )
                     .shape(" # ", "BBB", " # ")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICK)
                     .group("nether_cincinnasite_brick_plate")
                     .outputCount(5)
                     .category(RecipeCategory.REDSTONE)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_bricks"), NetherMetalBlocks.CINCINNASITE_BRICKS)
                     .shape("#B", "B#")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICK)
                     .group("nether_cincinnasite_bricks")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_bricks_pillar"),
                             NetherMetalBlocks.CINCINNASITE_BRICKS_PILLAR
                     )
                     .shape("#", "#")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_BRICKS)
                     .group("nether_cincinnasite_bricks_pillar")
                     .outputCount(2)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_button"), NetherMetalBlocks.CINCINNASITE_BUTTON)
                     .shapeless()
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_button")
                     .category(RecipeCategory.REDSTONE)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_carved"), NetherMetalBlocks.CINCINNASITE_CARVED)
                     .shape("##", "##")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_carved")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_forge"), NetherFunctionalBlocks.CINCINNASITE_FORGE)
                     .shape("B#B", "# #", "B#B")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICKS)
                     .group("nether_cincinnasite_forge")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_forged_from_ingot"),
                             NetherMetalBlocks.CINCINNASITE_FORGED
                     )
                     .shape("##", "##")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_forged_from_ingot")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_frame"), NetherDecorBlocks.CINCINNASITE_FRAME)
                     .shape("# #", "   ", "# #")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_frame")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(16)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_lantern"), NetherLightBlocks.CINCINNASITE_LANTERN)
                     .shape(" # ", "#G#", " # ")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .addMaterial('G', Items.GLOWSTONE)
                     .group("nether_cincinnasite_lantern")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_lantern_small"),
                             NetherLightBlocks.CINCINNASITE_LANTERN_SMALL
                     )
                     .shape("I", "L")
                     .addMaterial('I', NetherResourceItems.CINCINNASITE_INGOT)
                     .addMaterial('L', NetherLightBlocks.CINCINNASITE_LANTERN)
                     .group("nether_cincinnasite_lantern_small")
                     .outputCount(4)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pedestal"), NetherFunctionalBlocks.CINCINNASITE_PEDESTAL)
                     .shape("##", "##", "##")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_pedestal")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pillar"), NetherMetalBlocks.CINCINNASITE_PILLAR)
                     .shape("#", "#")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_pillar")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pot"), NetherDecorBlocks.CINCINNASITE_POT)
                     .shape("#N#", " # ")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .addMaterial('N', Items.SOUL_SAND)
                     .group("nether_cincinnasite_pot")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_tile_large"), NetherMetalBlocks.CINCINNASITE_TILE_LARGE)
                     .shape("#", "#")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_SLAB)
                     .group("nether_cincinnasite_tile_large")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_tile_small"), NetherMetalBlocks.CINCINNASITE_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_TILE_LARGE)
                     .group("nether_cincinnasite_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("nether_brewing_stand"), NetherFunctionalBlocks.NETHER_BREWING_STAND)
                     .shape(" I ", " S ", "###")
                     .addMaterial('I', NetherResourceItems.CINCINNASITE_INGOT)
                     .addMaterial('S', Items.BLAZE_ROD)
                     .addMaterial('#', Items.NETHER_BRICKS)
                     .group("nether_nether_brewing_stand")
                     .category(RecipeCategory.BREWING)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("nether_ruby_block"), NetherMetalBlocks.NETHER_RUBY_BLOCK)
                     .shape("###", "###", "###")
                     .addMaterial('#', NetherResourceItems.NETHER_RUBY)
                     .group("nether_nether_ruby_block")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("nether_tile_large"), NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                     .shape("##", "##")
                     .addMaterial('#', Items.NETHER_BRICK_SLAB)
                     .group("nether_nether_tile_large")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("nether_tile_small"), NetherStoneBlocks.NETHER_BRICK_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                     .group("nether_nether_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("quartz_glass_framed"), NetherGlassBlocks.QUARTZ_GLASS_FRAMED)
                     .shape("G#G", "# #", "G#G")
                     .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                     .addMaterial('G', NetherGlassBlocks.QUARTZ_GLASS)
                     .group("nether_quartz_glass_framed")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(8)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("quartz_glass_framed_pane"),
                             NetherGlassBlocks.QUARTZ_GLASS_FRAMED_PANE
                     )
                     .shape("###", "###")
                     .addMaterial('#', NetherGlassBlocks.QUARTZ_GLASS_FRAMED)
                     .group("nether_quartz_glass_framed_pane")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("quartz_glass_pane"), NetherGlassBlocks.QUARTZ_GLASS_PANE)
                     .shape("###", "###")
                     .addMaterial('#', NetherGlassBlocks.QUARTZ_GLASS)
                     .group("nether_quartz_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("wall_moss"), NetherWallPlantBlocks.WALL_MOSS)
                     .shapeless()
                     .addMaterial('#', NetherPlantBlocks.NETHER_GRASS)
                     .group("nether_wall_moss")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        RecipeBuilder.crafting(
                             BetterNether.C.id("whispering_gourd_lantern"),
                             NetherLightBlocks.WHISPERING_GOURD_LANTERN
                     )
                     .shape("#", "T")
                     .addMaterial('#', NetherCropBlocks.WHISPERING_GOURD)
                     .addMaterial('T', Items.TORCH)
                     .group("nether_whispering_gourd_lantern")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_bricks"), NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                     .shape("##", "##")
                     .addMaterial('#', NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                     .group("nether_blue_obsidian_bricks")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("blue_obsidian_glass_pane"),
                             NetherObsidianBlocks.BLUE_OBSIDIAN_GLASS_PANE
                     )
                     .shape("###", "###")
                     .addMaterial('#', NetherObsidianBlocks.BLUE_OBSIDIAN_GLASS)
                     .group("nether_blue_obsidian_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_rod_tiles"), NetherObsidianBlocks.BLUE_OBSIDIAN_ROD_TILES)
                     .shape(" ##", "## ")
                     .addMaterial('#', NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                     .group("nether_blue_obsidian_rod_tiles")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_tile"), NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                     .shape("##", "##")
                     .addMaterial('#', NetherObsidianBlocks.BLUE_OBSIDIAN)
                     .group("nether_blue_obsidian_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("blue_obsidian_tile_small"),
                             NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SMALL
                     )
                     .shape("##", "##")
                     .addMaterial('#', NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                     .group("nether_blue_obsidian_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_anvil"), NetherFunctionalBlocks.CINCINNASITE_ANVIL)
                     .shape("###", " # ", "BBB")
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICKS)
                     .group("nether_cincinnasite_anvil")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_bricks"), NetherObsidianBlocks.OBSIDIAN_BRICKS)
                     .shape("##", "##")
                     .addMaterial('#', NetherObsidianBlocks.OBSIDIAN_TILE)
                     .group("nether_obsidian_bricks")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_glass_pane"), NetherObsidianBlocks.OBSIDIAN_GLASS_PANE)
                     .shape("###", "###")
                     .addMaterial('#', NetherObsidianBlocks.OBSIDIAN_GLASS)
                     .group("nether_obsidian_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_rod_tiles"), NetherObsidianBlocks.OBSIDIAN_ROD_TILES)
                     .shape(" ##", "## ")
                     .addMaterial('#', NetherObsidianBlocks.OBSIDIAN_TILE)
                     .group("nether_obsidian_rod_tiles")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_tile"), NetherObsidianBlocks.OBSIDIAN_TILE)
                     .shape("##", "##")
                     .addMaterial('#', Items.OBSIDIAN)
                     .group("nether_obsidian_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_tile_small"), NetherObsidianBlocks.OBSIDIAN_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherObsidianBlocks.OBSIDIAN_BRICKS)
                     .group("nether_obsidian_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("farmland"), NetherTerrainBlocks.FARMLAND)
                     .shape("#S#", "#N#", "#H#")
                     .addMaterial('#', NetherWoodBlocks.MAT_STALAGNATE.getPlanks())
                     .addMaterial('H', NetherWoodBlocks.MAT_STALAGNATE.getSlab())
                     .addMaterial('N', Items.NETHERRACK)
                     .addMaterial('S', Items.SOUL_SAND)
                     .group("nether_farmland")
                     .category(RecipeCategory.MISC)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bone_reed_door"), NetherStoneBlocks.BONE_REED_DOOR)
                     .shape("AB", "BB", "BA")
                     .addMaterial('A', NetherWoodBlocks.MAT_REED.getPlanks())
                     .addMaterial('B', NetherStoneBlocks.BONE_BLOCK)
                     .group("nether_bone_reed_door")
                     .category(RecipeCategory.REDSTONE)
                     .outputCount(3)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("chest_of_drawers"), NetherFurnitureBlocks.CHEST_OF_DRAWERS)
                     .shape("C#C", "# #", "C#C")
                     .addMaterial('C', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .addMaterial('#', NetherWoodBlocks.MAT_REED.getPlanks())
                     .group("nether_chest_of_drawers")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        registerStoneCutting(context);
        registerBlasting(context);
        registerSmelting(context);
        registerSmithing(context);

        withTemplates(context);
        // Complex-material recipes are now emitted automatically via the block RECIPE traits
        // (see BlockTraits.RECIPE in the migrated complex materials); the old
        // ComplexMaterial.provideAllRecipes(...) helper was removed in the wover/bclib refactor.
    }

    private static void registerSmithing(RecipeBuilder.Context context) {
        RecipeBuilder
                .smithing(BetterNether.C.id("netherite_fire_bowl"), NetherLightBlocks.NETHERITE_FIRE_BOWL)
                .base(NetherLightBlocks.CINCINNASITE_FIRE_BOWL)
                .addon(Items.NETHERITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .template(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                .build(context);

        RecipeBuilder
                .smithing(BetterNether.C.id("netherite_fire_bowl_soul"), NetherLightBlocks.NETHERITE_FIRE_BOWL_SOUL)
                .base(NetherLightBlocks.CINCINNASITE_FIRE_BOWL_SOUL)
                .addon(Items.NETHERITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .template(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                .build(context);
    }

    private static void registerSmelting(RecipeBuilder.Context context) {
        RecipeBuilder.smelting(BetterNether.C.id("blue_obsidian_glass"), NetherObsidianBlocks.BLUE_OBSIDIAN_GLASS)
                     .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_forged"), NetherMetalBlocks.CINCINNASITE_FORGED)
                     .input(NetherMetalBlocks.CINCINNASITE_BLOCK)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("obsidian_glass"), NetherObsidianBlocks.OBSIDIAN_GLASS)
                     .input(Blocks.OBSIDIAN)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("quartz_glass"), NetherGlassBlocks.QUARTZ_GLASS)
                     .input(Items.QUARTZ)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("soul_sandstone"), NetherStoneBlocks.SOUL_SANDSTONE_SMOOTH)
                     .input(NetherStoneBlocks.SOUL_SANDSTONE)
                     .cookingTime(200)
                     .experience(0.1f)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
    }

    private static void registerBlasting(RecipeBuilder.Context context) {
        RecipeBuilder
                .blasting(BetterNether.C.id("cincinnasite_forged_blasting"), NetherMetalBlocks.CINCINNASITE_FORGED)
                .input(NetherMetalBlocks.CINCINNASITE_BLOCK)
                .cookingTime(100)
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);
    }

    private static void registerStoneCutting(RecipeBuilder.Context context) {
        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_from_tile_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_slab_from_bricks_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_slab_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stairs_from_bricks_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stairs_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_rod_tiles_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_ROD_TILES
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_rod_tiles")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_slab_from_tile_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SLAB
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_slab_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SLAB
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_small_from_bricks_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SMALL
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_small_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SMALL
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_stairs_from_tile_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_STAIRS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_stairs_stonecutter"),
                        NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_STAIRS
                )
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("blue_obsidian_tile_stonecutter"), NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                .input(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_from_tile_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_BRICKS
                )
                .input(NetherObsidianBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_slab_from_bricks_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherObsidianBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_slab_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_BRICKS_SLAB
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_stairs_from_bricks_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherObsidianBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_stairs_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_BRICKS_STAIRS
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_bricks_stonecutter"), NetherObsidianBlocks.OBSIDIAN_BRICKS)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_rod_tiles_stonecutting"), NetherObsidianBlocks.OBSIDIAN_ROD_TILES)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_rod_tiles")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_slab_from_tile_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_TILE_SLAB
                )
                .input(NetherObsidianBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_slab_stonecutter"), NetherObsidianBlocks.OBSIDIAN_TILE_SLAB)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_small_from_bricks_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_TILE_SMALL
                )
                .input(NetherObsidianBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_small_stonecutter"), NetherObsidianBlocks.OBSIDIAN_TILE_SMALL)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_stairs_from_tile_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_TILE_STAIRS
                )
                .input(NetherObsidianBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_stairs_stonecutter"),
                        NetherObsidianBlocks.OBSIDIAN_TILE_STAIRS
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_stonecutter"), NetherObsidianBlocks.OBSIDIAN_TILE)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("cincinnasite_pillar_stonecutting"), NetherMetalBlocks.CINCINNASITE_PILLAR)
                .input(NetherMetalBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_large_stonecutting"),
                        NetherMetalBlocks.CINCINNASITE_TILE_LARGE
                )
                .input(NetherMetalBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("cincinnasite_carved_stonecutting"), NetherMetalBlocks.CINCINNASITE_CARVED)
                .input(NetherMetalBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_small_stonecutting"),
                        NetherMetalBlocks.CINCINNASITE_TILE_SMALL
                )
                .input(NetherMetalBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_small_from_large_stonecutting"),
                        NetherMetalBlocks.CINCINNASITE_TILE_SMALL
                )
                .input(NetherMetalBlocks.CINCINNASITE_TILE_LARGE)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_roof_tile_stonecutting"),
                        NetherDecorBlocks.ROOF_TILE_CINCINNASITE
                )
                .input(NetherMetalBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_from_smooth_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stairs_from_smooth_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_STAIRS
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stairs_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_STAIRS
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_slabs_from_smooth_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_SLAB
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(2)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_slabs_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_SLAB
                )
                .input(Blocks.BASALT)
                .outputCount(2)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_wall_from_smooth_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_WALL
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_wall_stonecutting"),
                        NetherStoneBlocks.BASALT_BRICKS_WALL
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("soul_sandstone_cut_stonecutting"),
                        NetherStoneBlocks.SOUL_SANDSTONE_CUT
                )
                .input(NetherStoneBlocks.SOUL_SANDSTONE)
                .outputCount(1)
                .group("soul_sandstone")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("soul_sandstone_chiseled_stonecutting"),
                        NetherStoneBlocks.SOUL_SANDSTONE_CHISELED
                )
                .input(NetherStoneBlocks.SOUL_SANDSTONE_SMOOTH)
                .outputCount(1)
                .group("soul_sandstone")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("roof_tile_nether_brick_stonecutting"),
                        NetherDecorBlocks.ROOF_TILE_NETHER_BRICKS
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_large_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_LARGE
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_small_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_SMALL
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_small_from_large_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_SMALL
                )
                .input(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_wall_from_brick_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_WALL
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_wall_from_large_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_WALL
                )
                .input(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_slab_from_brick_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_SLAB
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(2)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_slab_from_large_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_SLAB
                )
                .input(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(2)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_stairs_from_brick_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_STAIRS
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_stairs_from_large_stonecutting"),
                        NetherStoneBlocks.NETHER_BRICK_TILE_STAIRS
                )
                .input(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);
    }

    private void withTemplates(RecipeBuilder.Context context) {
        final var t = new RecipeBuilder.Templates(context, BetterNether.C);
        RecipesHelper.provideRecipes(t);
    }
}
