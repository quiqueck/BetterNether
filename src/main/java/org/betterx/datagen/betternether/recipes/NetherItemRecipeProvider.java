package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.registry.block.NetherGlassBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherOreBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherWallPlantBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherFoodItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.complex.api.equipment.EquipmentSet;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class NetherItemRecipeProvider extends WoverRecipeProvider {
    public NetherItemRecipeProvider(ModCore modCore) {
        super(modCore, "BetterNether - Item Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder
                .crafting(BN.id("bn_glowstone_dust"), Items.GLOWSTONE_DUST)
                .shape("###", "###", "###")
                .addMaterial('#', NetherResourceItems.GLOWSTONE_PILE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("cincinnasite_chains"), NetherMetalBlocks.CINCINNASITE_CHAIN)
                .outputCount(3)
                .shape("#", "#", "#")
                .addMaterial('#', NetherResourceItems.CINCINNASITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("lapis_pile_to_lapis"), Items.LAPIS_LAZULI)
                .shape("###", "###", "###")
                .addMaterial('#', NetherResourceItems.LAPIS_PILE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_yellow_dye"), Items.YELLOW_DYE)
                .outputCount(2)
                .shapeless()
                .addMaterial('#', NetherVineBlocks.BLOOMING_VINE)
                .category(RecipeCategory.DECORATIONS)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_glowstone_pile"), NetherResourceItems.GLOWSTONE_PILE)
                .outputCount(2)
                .shapeless()
                .addMaterial('#', NetherVineBlocks.GOLDEN_VINE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_brown_mushroom"), Items.BROWN_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherWallPlantBlocks.WALL_MUSHROOM_BROWN)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("wall_mushroom_brown"), NetherWallPlantBlocks.WALL_MUSHROOM_BROWN)
                .shapeless()
                .addMaterial('#', Items.BROWN_MUSHROOM)
                .build(context);


        RecipeBuilder
                .crafting(BN.id("bn_red_mushroom"), Items.RED_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherWallPlantBlocks.WALL_MUSHROOM_RED)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("wall_red_brown"), NetherWallPlantBlocks.WALL_MUSHROOM_RED)
                .shapeless()
                .addMaterial('#', Items.RED_MUSHROOM)
                .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_ingot"), NetherResourceItems.CINCINNASITE_INGOT)
                     .shapeless()
                     .addMaterial('#', NetherMetalBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_ingot")
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("detector_rail"), Items.DETECTOR_RAIL)
                     .shape("X X", "X#X", "XRX")
                     .addMaterial('R', Items.REDSTONE)
                     .addMaterial('#', Items.STONE_PRESSURE_PLATE)
                     .addMaterial('X', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_detector_rail")
                     .outputCount(6)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("glass_bottle"), Items.GLASS_BOTTLE)
                     .shape("# #", " # ")
                     .addMaterial('#', NetherGlassBlocks.QUARTZ_GLASS)
                     .group("nether_glass_bottle")
                     .outputCount(3)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("gray_dye"), Items.GRAY_DYE)
                     .shapeless()
                     .addMaterial('#', NetherMushroomBlocks.GRAY_MOLD)
                     .group("nether_gray_dye")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("nether_ruby_from_block"), NetherResourceItems.NETHER_RUBY)
                     .shapeless()
                     .addMaterial('#', NetherMetalBlocks.NETHER_RUBY_BLOCK)
                     .group("nether_nether_ruby_from_block")
                     .outputCount(9)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("paper"), Items.PAPER)
                     .shape("###")
                     .addMaterial('#', NetherWoodBlocks.NETHER_REED_STEM)
                     .group("nether_paper")
                     .outputCount(3)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("rail"), Items.RAIL)
                     .shape("X X", "X#X", "X X")
                     .addMaterial('#', Items.STICK)
                     .addMaterial('X', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_rail")
                     .outputCount(16)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("red_dye"), Items.RED_DYE)
                     .shapeless()
                     .addMaterial('#', NetherMushroomBlocks.RED_MOLD)
                     .group("nether_red_dye")
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_apple"), NetherFoodItems.STALAGNATE_BOWL_APPLE)
                     .shape("W", "#")
                     .addMaterial('#', NetherFoodItems.STALAGNATE_BOWL)
                     .addMaterial('W', NetherFoodItems.BLACK_APPLE)
                     .group("nether_stalagnate_bowl_apple")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stick"), Items.STICK)
                     .shape("#", "#")
                     .addMaterial('#', NetherWoodBlocks.NETHER_REED_STEM)
                     .group("nether_stick")
                     .outputCount(2)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("sugar"), Items.SUGAR)
                     .shapeless()
                     .addMaterial('#', NetherWoodBlocks.NETHER_REED_STEM)
                     .group("nether_sugar")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("shield"), Items.SHIELD)
                     .shape("WoW", "WWW", " W ")
                     .addMaterial('W', ItemTags.PLANKS)
                     .addMaterial('o', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_shield")
                     .category(RecipeCategory.COMBAT)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("piston"), Items.PISTON)
                     .shape("TTT", "#X#", "#R#")
                     .addMaterial('R', Items.REDSTONE)
                     .addMaterial('#', Items.COBBLESTONE)
                     .addMaterial('T', ItemTags.PLANKS)
                     .addMaterial('X', NetherResourceItems.CINCINNASITE_INGOT)
                     .group("nether_piston")
                     .category(RecipeCategory.REDSTONE)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl"), NetherFoodItems.STALAGNATE_BOWL)
                     .shape("# #", " # ")
                     .addMaterial('#', NetherWoodBlocks.MAT_STALAGNATE.getStem())
                     .group("nether_stalagnate_bowl")
                     .outputCount(3)
                     .category(RecipeCategory.DECORATIONS)
                     .build(context);

        registerShapeLess(context);
        registerSmelting(context);

        RecipeBuilder.copySmithingTemplate(
                BetterNether.C.id("copy_bowl_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE,
                NetherMetalBlocks.CINCINNASITE_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterNether.C.id("copy_flaming_ruby_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                NetherTemplates.FLAMING_RUBY_TEMPLATE,
                NetherResourceItems.NETHER_RUBY
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterNether.C.id("copy_cincinnasite_diamond_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE,
                NetherResourceItems.CINCINNASITE_INGOT
        ).build(context);

        // Equipment-set recipes are now emitted automatically via item RECIPE traits
        // (attached by EquipmentSet#add); the old EquipmentSet.buildAllRecipes(...) helper was removed.
    }

    private static void registerShapeLess(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(BetterNether.C.id("agave_medicine"), NetherResourceItems.AGAVE_MEDICINE)
                     .addMaterial('#', NetherFoodItems.STALAGNATE_BOWL)
                     .addMaterial('A', NetherResourceItems.AGAVE_LEAF)
                     .addMaterial('B', NetherResourceItems.AGAVE_LEAF)
                     .addMaterial('C', NetherResourceItems.AGAVE_LEAF)
                     .shapeless()
                     .group("nether_agave_medicine")
                     .category(RecipeCategory.FOOD)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("herbal_medicine"), NetherResourceItems.HERBAL_MEDICINE)
                     .addMaterial('#', NetherFoodItems.STALAGNATE_BOWL)
                     .addMaterial('A', NetherResourceItems.AGAVE_LEAF)
                     .addMaterial('B', NetherFoodItems.BLACK_APPLE)
                     .addMaterial('C', NetherFoodItems.HOOK_MUSHROOM_COOKED)
                     .addMaterial('D', Items.NETHER_WART)
                     .shapeless()
                     .group("nether_herbal_medicine")
                     .category(RecipeCategory.FOOD)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_mushroom"), NetherFoodItems.STALAGNATE_BOWL_MUSHROOM)
                     .addMaterial('#', Items.BROWN_MUSHROOM)
                     .addMaterial('A', Items.RED_MUSHROOM)
                     .addMaterial('B', NetherFoodItems.STALAGNATE_BOWL)
                     .shapeless()
                     .group("nether_stalagnate_bowl_mushroom")
                     .category(RecipeCategory.FOOD)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_wart"), NetherFoodItems.STALAGNATE_BOWL_WART)
                     .addMaterial('#', NetherFoodItems.STALAGNATE_BOWL)
                     .addMaterial('A', Items.NETHER_WART)
                     .addMaterial('B', Items.NETHER_WART)
                     .addMaterial('C', Items.NETHER_WART)
                     .shapeless()
                     .group("nether_stalagnate_bowl_wart")
                     .category(RecipeCategory.FOOD)
                     .build(context);
    }

    private static void registerSmelting(RecipeBuilder.Context context) {
        RecipeBuilder.smelting(BetterNether.C.id("black_dye"), Items.BLACK_DYE)
                     .input(NetherPlantBlocks.INK_BUSH_SEED)
                     .cookingTime(200)
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("hook_mushroom_cooked"), NetherFoodItems.HOOK_MUSHROOM_COOKED)
                     .input(NetherMushroomBlocks.HOOK_MUSHROOM)
                     .cookingTime(200)
                     .experience(0.1f)
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_ingot_from_shard"), NetherResourceItems.CINCINNASITE_INGOT)
                     .input(NetherResourceItems.CINCINNASITE)
                     .cookingTime(200)
                     .experience(0.5f)
                     .enableBlastFurnace()
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_ingot_from_ore"), NetherResourceItems.CINCINNASITE_INGOT)
                     .input(NetherOreBlocks.CINCINNASITE_ORE)
                     .cookingTime(200)
                     .experience(0.5f)
                     .enableBlastFurnace()
                     .build(context);
    }
}