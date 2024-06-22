package org.betterx.datagen.betternether.recipes;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class NetherBlockRecipesProvider extends WoverRecipeProvider {
    public NetherBlockRecipesProvider(ModCore modCore) {
        super(modCore, "BetterNether - Block Recipes");
    }

    @Override
    protected void bootstrap(HolderLookup.Provider provider, RecipeOutput context) {
        RecipeBuilder.crafting(
                             BetterNether.C.id("mushroom_fir_trimmed_chest"),
                             NetherBlocks.TRIMMED_MUSHROOM_FIR_CHEST
                     )
                     .shapeless()
                     .addMaterial('#', NetherBlocks.MAT_MUSHROOM_FIR.getBlock(WoodSlots.CHEST))
                     .addMaterial('S', NetherBlocks.MAT_MUSHROOM_FIR.getBlock(WoodSlots.STRIPPED_LOG))
                     .group("chest")
                     .outputCount(1)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("activator_rail"), Items.ACTIVATOR_RAIL)
                     .shape("XSX", "X#X", "XSX")
                     .addMaterial('#', Items.REDSTONE_TORCH)
                     .addMaterial('S', Items.STICK)
                     .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                     .group("activator_rail")
                     .outputCount(6)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("black_apple_seed"), NetherBlocks.BLACK_APPLE_SEED)
                     .shapeless()
                     .addMaterial('#', NetherItems.BLACK_APPLE)
                     .group("nether_black_apple_seed")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bn_bone_block"), NetherBlocks.BONE_BLOCK)
                     .shape("##", "##")
                     .addMaterial('#', Items.BONE_BLOCK)
                     .group("nether_bn_bone_block")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("bone_cin_door"), NetherBlocks.BONE_CINCINNASITE_DOOR)
                     .shape("AB", "BB", "BA")
                     .addMaterial('A', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', NetherBlocks.BONE_BLOCK)
                     .group("nether_bone_cin_door")
                     .outputCount(3)
                     .category(RecipeCategory.REDSTONE)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bone_tile"), NetherBlocks.BONE_TILE)
                     .shape("#", "#")
                     .addMaterial('#', NetherBlocks.BONE_SLAB)
                     .group("nether_bone_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("brick_pot"), NetherBlocks.BRICK_POT)
                     .shape("#N#", " # ")
                     .addMaterial('#', Items.NETHER_BRICK)
                     .addMaterial('N', Items.SOUL_SAND)
                     .group("nether_brick_pot")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_bars"), NetherBlocks.CINCINNASITE_BARS)
                     .shape("###", "###")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_bars")
                     .outputCount(16)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_block"), NetherBlocks.CINCINNASITE_BLOCK)
                     .shape("##", "##")
                     .addMaterial('#', NetherItems.CINCINNASITE)
                     .group("nether_cincinnasite_block")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_brick_plate"),
                             NetherBlocks.CINCINNASITE_BRICK_PLATE
                     )
                     .shape(" # ", "BBB", " # ")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICK)
                     .group("nether_cincinnasite_brick_plate")
                     .outputCount(5)
                     .category(RecipeCategory.REDSTONE)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_bricks"), NetherBlocks.CINCINNASITE_BRICKS)
                     .shape("#B", "B#")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICK)
                     .group("nether_cincinnasite_bricks")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_bricks_pillar"),
                             NetherBlocks.CINCINNASITE_BRICKS_PILLAR
                     )
                     .shape("#", "#")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_BRICKS)
                     .group("nether_cincinnasite_bricks_pillar")
                     .outputCount(2)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_button"), NetherBlocks.CINCINNASITE_BUTTON)
                     .shapeless()
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_button")
                     .category(RecipeCategory.REDSTONE)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_carved"), NetherBlocks.CINCINNASITE_CARVED)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_carved")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_forge"), NetherBlocks.CINCINNASITE_FORGE)
                     .shape("B#B", "# #", "B#B")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICKS)
                     .group("nether_cincinnasite_forge")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_forged_from_ingot"),
                             NetherBlocks.CINCINNASITE_FORGED
                     )
                     .shape("##", "##")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_forged_from_ingot")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_frame"), NetherBlocks.CINCINNASITE_FRAME)
                     .shape("# #", "   ", "# #")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_cincinnasite_frame")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(16)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_lantern"), NetherBlocks.CINCINNASITE_LANTERN)
                     .shape(" # ", "#G#", " # ")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .addMaterial('G', Items.GLOWSTONE)
                     .group("nether_cincinnasite_lantern")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("cincinnasite_lantern_small"),
                             NetherBlocks.CINCINNASITE_LANTERN_SMALL
                     )
                     .shape("I", "L")
                     .addMaterial('I', NetherItems.CINCINNASITE_INGOT)
                     .addMaterial('L', NetherBlocks.CINCINNASITE_LANTERN)
                     .group("nether_cincinnasite_lantern_small")
                     .outputCount(4)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pedestal"), NetherBlocks.CINCINNASITE_PEDESTAL)
                     .shape("##", "##", "##")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_pedestal")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pillar"), NetherBlocks.CINCINNASITE_PILLAR)
                     .shape("#", "#")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_pillar")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_pot"), NetherBlocks.CINCINNASITE_POT)
                     .shape("#N#", " # ")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .addMaterial('N', Items.SOUL_SAND)
                     .group("nether_cincinnasite_pot")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_tile_large"), NetherBlocks.CINCINNASITE_TILE_LARGE)
                     .shape("#", "#")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_SLAB)
                     .group("nether_cincinnasite_tile_large")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_tile_small"), NetherBlocks.CINCINNASITE_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_TILE_LARGE)
                     .group("nether_cincinnasite_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("nether_brewing_stand"), NetherBlocks.NETHER_BREWING_STAND)
                     .shape(" I ", " S ", "###")
                     .addMaterial('I', NetherItems.CINCINNASITE_INGOT)
                     .addMaterial('S', Items.BLAZE_ROD)
                     .addMaterial('#', Items.NETHER_BRICKS)
                     .group("nether_nether_brewing_stand")
                     .category(RecipeCategory.BREWING)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("nether_ruby_block"), NetherBlocks.NETHER_RUBY_BLOCK)
                     .shape("###", "###", "###")
                     .addMaterial('#', NetherItems.NETHER_RUBY)
                     .group("nether_nether_ruby_block")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("nether_tile_large"), NetherBlocks.NETHER_BRICK_TILE_LARGE)
                     .shape("##", "##")
                     .addMaterial('#', Items.NETHER_BRICK_SLAB)
                     .group("nether_nether_tile_large")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("nether_tile_small"), NetherBlocks.NETHER_BRICK_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.NETHER_BRICK_TILE_LARGE)
                     .group("nether_nether_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("quartz_glass_framed"), NetherBlocks.QUARTZ_GLASS_FRAMED)
                     .shape("G#G", "# #", "G#G")
                     .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                     .addMaterial('G', NetherBlocks.QUARTZ_GLASS)
                     .group("nether_quartz_glass_framed")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(8)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("quartz_glass_framed_pane"),
                             NetherBlocks.QUARTZ_GLASS_FRAMED_PANE
                     )
                     .shape("###", "###")
                     .addMaterial('#', NetherBlocks.QUARTZ_GLASS_FRAMED)
                     .group("nether_quartz_glass_framed_pane")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("quartz_glass_pane"), NetherBlocks.QUARTZ_GLASS_PANE)
                     .shape("###", "###")
                     .addMaterial('#', NetherBlocks.QUARTZ_GLASS)
                     .group("nether_quartz_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("wall_moss"), NetherBlocks.WALL_MOSS)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.NETHER_GRASS)
                     .group("nether_wall_moss")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        RecipeBuilder.crafting(
                             BetterNether.C.id("whispering_gourd_lantern"),
                             NetherBlocks.WHISPERING_GOURD_LANTERN
                     )
                     .shape("#", "T")
                     .addMaterial('#', NetherBlocks.WHISPERING_GOURD)
                     .addMaterial('T', Items.TORCH)
                     .group("nether_whispering_gourd_lantern")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_bricks"), NetherBlocks.BLUE_OBSIDIAN_BRICKS)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.BLUE_OBSIDIAN_TILE)
                     .group("nether_blue_obsidian_bricks")
                     .outputCount(4)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("blue_obsidian_glass_pane"),
                             NetherBlocks.BLUE_OBSIDIAN_GLASS_PANE
                     )
                     .shape("###", "###")
                     .addMaterial('#', NetherBlocks.BLUE_OBSIDIAN_GLASS)
                     .group("nether_blue_obsidian_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_rod_tiles"), NetherBlocks.BLUE_OBSIDIAN_ROD_TILES)
                     .shape(" ##", "## ")
                     .addMaterial('#', NetherBlocks.BLUE_OBSIDIAN_TILE)
                     .group("nether_blue_obsidian_rod_tiles")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("blue_obsidian_tile"), NetherBlocks.BLUE_OBSIDIAN_TILE)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.BLUE_OBSIDIAN)
                     .group("nether_blue_obsidian_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(
                             BetterNether.C.id("blue_obsidian_tile_small"),
                             NetherBlocks.BLUE_OBSIDIAN_TILE_SMALL
                     )
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.BLUE_OBSIDIAN_BRICKS)
                     .group("nether_blue_obsidian_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_anvil"), NetherBlocks.CINCINNASITE_ANVIL)
                     .shape("###", " # ", "BBB")
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('B', Items.NETHER_BRICKS)
                     .group("nether_cincinnasite_anvil")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_bricks"), NetherBlocks.OBSIDIAN_BRICKS)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.OBSIDIAN_TILE)
                     .group("nether_obsidian_bricks")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_glass_pane"), NetherBlocks.OBSIDIAN_GLASS_PANE)
                     .shape("###", "###")
                     .addMaterial('#', NetherBlocks.OBSIDIAN_GLASS)
                     .group("nether_obsidian_glass_pane")
                     .category(RecipeCategory.DECORATIONS)
                     .outputCount(16)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_rod_tiles"), NetherBlocks.OBSIDIAN_ROD_TILES)
                     .shape(" ##", "## ")
                     .addMaterial('#', NetherBlocks.OBSIDIAN_TILE)
                     .group("nether_obsidian_rod_tiles")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_tile"), NetherBlocks.OBSIDIAN_TILE)
                     .shape("##", "##")
                     .addMaterial('#', Items.OBSIDIAN)
                     .group("nether_obsidian_tile")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("obsidian_tile_small"), NetherBlocks.OBSIDIAN_TILE_SMALL)
                     .shape("##", "##")
                     .addMaterial('#', NetherBlocks.OBSIDIAN_BRICKS)
                     .group("nether_obsidian_tile_small")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("farmland"), NetherBlocks.FARMLAND)
                     .shape("#S#", "#N#", "#H#")
                     .addMaterial('#', NetherBlocks.MAT_STALAGNATE.getPlanks())
                     .addMaterial('H', NetherBlocks.MAT_STALAGNATE.getSlab())
                     .addMaterial('N', Items.NETHERRACK)
                     .addMaterial('S', Items.SOUL_SAND)
                     .group("nether_farmland")
                     .category(RecipeCategory.MISC)
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("bone_reed_door"), NetherBlocks.BONE_REED_DOOR)
                     .shape("AB", "BB", "BA")
                     .addMaterial('A', NetherBlocks.MAT_REED.getPlanks())
                     .addMaterial('B', NetherBlocks.BONE_BLOCK)
                     .group("nether_bone_reed_door")
                     .category(RecipeCategory.REDSTONE)
                     .outputCount(3)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("chest_of_drawers"), NetherBlocks.CHEST_OF_DRAWERS)
                     .shape("C#C", "# #", "C#C")
                     .addMaterial('C', NetherBlocks.CINCINNASITE_FORGED)
                     .addMaterial('#', NetherBlocks.MAT_REED.getPlanks())
                     .group("nether_chest_of_drawers")
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        registerStoneCutting(context);
        registerBlasting(context);
        registerSmelting(context);
        registerSmithing(context);

        withTemplates(context);
        ComplexMaterial.provideAllRecipes(context, BetterNether.C);
    }

    private static void registerSmithing(RecipeOutput context) {
        RecipeBuilder
                .smithing(BetterNether.C.id("netherite_fire_bowl"), NetherBlocks.NETHERITE_FIRE_BOWL)
                .base(NetherBlocks.CINCINNASITE_FIRE_BOWL)
                .addon(Items.NETHERITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .template(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                .build(context);

        RecipeBuilder
                .smithing(BetterNether.C.id("netherite_fire_bowl_soul"), NetherBlocks.NETHERITE_FIRE_BOWL_SOUL)
                .base(NetherBlocks.CINCINNASITE_FIRE_BOWL_SOUL)
                .addon(Items.NETHERITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .template(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                .build(context);
    }

    private static void registerSmelting(RecipeOutput context) {
        RecipeBuilder.smelting(BetterNether.C.id("blue_obsidian_glass"), NetherBlocks.BLUE_OBSIDIAN_GLASS)
                     .input(NetherBlocks.BLUE_OBSIDIAN)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_forged"), NetherBlocks.CINCINNASITE_FORGED)
                     .input(NetherBlocks.CINCINNASITE_BLOCK)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("obsidian_glass"), NetherBlocks.OBSIDIAN_GLASS)
                     .input(Blocks.OBSIDIAN)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("quartz_glass"), NetherBlocks.QUARTZ_GLASS)
                     .input(Items.QUARTZ)
                     .cookingTime(200)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
        RecipeBuilder.smelting(BetterNether.C.id("soul_sandstone"), NetherBlocks.SOUL_SANDSTONE_SMOOTH)
                     .input(NetherBlocks.SOUL_SANDSTONE)
                     .cookingTime(200)
                     .experience(0.1f)
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
    }

    private static void registerBlasting(RecipeOutput context) {
        RecipeBuilder
                .blasting(BetterNether.C.id("cincinnasite_forged_blasting"), NetherBlocks.CINCINNASITE_FORGED)
                .input(NetherBlocks.CINCINNASITE_BLOCK)
                .cookingTime(100)
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);
    }

    private static void registerStoneCutting(RecipeOutput context) {
        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_from_tile_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_slab_from_bricks_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_slab_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stairs_from_bricks_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stairs_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_bricks_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_BRICKS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_rod_tiles_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_ROD_TILES
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_rod_tiles")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_slab_from_tile_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_SLAB
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_slab_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_SLAB
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_small_from_bricks_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_SMALL
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_BRICKS)
                .group("nether_blue_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_small_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_SMALL
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_stairs_from_tile_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_STAIRS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN_TILE)
                .group("nether_blue_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("blue_obsidian_tile_stairs_stonecutter"),
                        NetherBlocks.BLUE_OBSIDIAN_TILE_STAIRS
                )
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("blue_obsidian_tile_stonecutter"), NetherBlocks.BLUE_OBSIDIAN_TILE)
                .input(NetherBlocks.BLUE_OBSIDIAN)
                .group("nether_blue_obsidian_tile")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_from_tile_stonecutter"),
                        NetherBlocks.OBSIDIAN_BRICKS
                )
                .input(NetherBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_slab_from_bricks_stonecutter"),
                        NetherBlocks.OBSIDIAN_BRICKS_SLAB
                )
                .input(NetherBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_slab_stonecutter"),
                        NetherBlocks.OBSIDIAN_BRICKS_SLAB
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_stairs_from_bricks_stonecutter"),
                        NetherBlocks.OBSIDIAN_BRICKS_STAIRS
                )
                .input(NetherBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_bricks_stairs_stonecutter"),
                        NetherBlocks.OBSIDIAN_BRICKS_STAIRS
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_bricks_stonecutter"), NetherBlocks.OBSIDIAN_BRICKS)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_bricks")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_rod_tiles_stonecutting"), NetherBlocks.OBSIDIAN_ROD_TILES)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_rod_tiles")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_slab_from_tile_stonecutter"),
                        NetherBlocks.OBSIDIAN_TILE_SLAB
                )
                .input(NetherBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_slab_stonecutter"), NetherBlocks.OBSIDIAN_TILE_SLAB)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .outputCount(2)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_small_from_bricks_stonecutter"),
                        NetherBlocks.OBSIDIAN_TILE_SMALL
                )
                .input(NetherBlocks.OBSIDIAN_BRICKS)
                .group("nether_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_small_stonecutter"), NetherBlocks.OBSIDIAN_TILE_SMALL)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_small")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_stairs_from_tile_stonecutter"),
                        NetherBlocks.OBSIDIAN_TILE_STAIRS
                )
                .input(NetherBlocks.OBSIDIAN_TILE)
                .group("nether_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("obsidian_tile_stairs_stonecutter"),
                        NetherBlocks.OBSIDIAN_TILE_STAIRS
                )
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile_stairs")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("obsidian_tile_stonecutter"), NetherBlocks.OBSIDIAN_TILE)
                .input(Blocks.OBSIDIAN)
                .group("nether_obsidian_tile")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("cincinnasite_pillar_stonecutting"), NetherBlocks.CINCINNASITE_PILLAR)
                .input(NetherBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_large_stonecutting"),
                        NetherBlocks.CINCINNASITE_TILE_LARGE
                )
                .input(NetherBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(BetterNether.C.id("cincinnasite_carved_stonecutting"), NetherBlocks.CINCINNASITE_CARVED)
                .input(NetherBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_small_stonecutting"),
                        NetherBlocks.CINCINNASITE_TILE_SMALL
                )
                .input(NetherBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_tile_small_from_large_stonecutting"),
                        NetherBlocks.CINCINNASITE_TILE_SMALL
                )
                .input(NetherBlocks.CINCINNASITE_TILE_LARGE)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("cincinnasite_roof_tile_stonecutting"),
                        NetherBlocks.ROOF_TILE_CINCINNASITE
                )
                .input(NetherBlocks.CINCINNASITE_FORGED)
                .outputCount(1)
                .group("cincinnasite")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_from_smooth_stonecutting"),
                        NetherBlocks.BASALT_BRICKS
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stonecutting"),
                        NetherBlocks.BASALT_BRICKS
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stairs_from_smooth_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_STAIRS
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_stairs_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_STAIRS
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_slabs_from_smooth_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_SLAB
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(2)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_slabs_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_SLAB
                )
                .input(Blocks.BASALT)
                .outputCount(2)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_wall_from_smooth_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_WALL
                )
                .input(Blocks.SMOOTH_BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("basalt_brick_wall_stonecutting"),
                        NetherBlocks.BASALT_BRICKS_WALL
                )
                .input(Blocks.BASALT)
                .outputCount(1)
                .group("basalt")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);


        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("soul_sandstone_cut_stonecutting"),
                        NetherBlocks.SOUL_SANDSTONE_CUT
                )
                .input(NetherBlocks.SOUL_SANDSTONE)
                .outputCount(1)
                .group("soul_sandstone")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("soul_sandstone_chiseled_stonecutting"),
                        NetherBlocks.SOUL_SANDSTONE_CHISELED
                )
                .input(NetherBlocks.SOUL_SANDSTONE_SMOOTH)
                .outputCount(1)
                .group("soul_sandstone")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("roof_tile_nether_brick_stonecutting"),
                        NetherBlocks.ROOF_TILE_NETHER_BRICKS
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_large_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_LARGE
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_small_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_SMALL
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_small_from_large_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_SMALL
                )
                .input(NetherBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_wall_from_brick_stonecutting"),
                        NetherBlocks.NETHER_BRICK_WALL
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_wall_from_large_stonecutting"),
                        NetherBlocks.NETHER_BRICK_WALL
                )
                .input(NetherBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_slab_from_brick_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_SLAB
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(2)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_slab_from_large_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_SLAB
                )
                .input(NetherBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(2)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_stairs_from_brick_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_STAIRS
                )
                .input(Blocks.NETHER_BRICKS)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterNether.C.id("nether_brick_tile_stairs_from_large_stonecutting"),
                        NetherBlocks.NETHER_BRICK_TILE_STAIRS
                )
                .input(NetherBlocks.NETHER_BRICK_TILE_LARGE)
                .outputCount(1)
                .group("nether_brick")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);
    }

    private void withTemplates(RecipeOutput context) {
        final var t = new RecipeBuilder.Templates(context, BetterNether.C);
        RecipesHelper.provideRecipes(t);
    }
}
