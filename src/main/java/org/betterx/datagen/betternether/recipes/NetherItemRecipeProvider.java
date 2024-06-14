package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;
import org.betterx.wover.complex.api.equipment.EquipmentSet;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class NetherItemRecipeProvider extends WoverRecipeProvider {
    public NetherItemRecipeProvider(ModCore modCore) {
        super(modCore, "BetterNether - Item Recipes");
    }

    @Override
    protected void bootstrap(RecipeOutput context) {
        RecipeBuilder
                .crafting(BN.id("bn_glowstone_dust"), Items.GLOWSTONE_DUST)
                .shape("###", "###", "###")
                .addMaterial('#', NetherItems.GLOWSTONE_PILE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("cincinnasite_chains"), NetherBlocks.CINCINNASITE_CHAIN)
                .outputCount(3)
                .shape("#", "#", "#")
                .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                .category(RecipeCategory.DECORATIONS)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("lapis_pile_to_lapis"), Items.LAPIS_LAZULI)
                .shape("###", "###", "###")
                .addMaterial('#', NetherItems.LAPIS_PILE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_yellow_dye"), Items.YELLOW_DYE)
                .outputCount(2)
                .shapeless()
                .addMaterial('#', NetherBlocks.BLOOMING_VINE)
                .category(RecipeCategory.DECORATIONS)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_glowstone_pile"), NetherItems.GLOWSTONE_PILE)
                .outputCount(2)
                .shapeless()
                .addMaterial('#', NetherBlocks.GOLDEN_VINE)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("bn_brown_mushroom"), Items.BROWN_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherBlocks.WALL_MUSHROOM_BROWN)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("wall_mushroom_brown"), NetherBlocks.WALL_MUSHROOM_BROWN)
                .shapeless()
                .addMaterial('#', Items.BROWN_MUSHROOM)
                .build(context);


        RecipeBuilder
                .crafting(BN.id("bn_red_mushroom"), Items.RED_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherBlocks.WALL_MUSHROOM_RED)
                .build(context);

        RecipeBuilder
                .crafting(BN.id("wall_red_brown"), NetherBlocks.WALL_MUSHROOM_RED)
                .shapeless()
                .addMaterial('#', Items.RED_MUSHROOM)
                .build(context);


        RecipeBuilder.crafting(BetterNether.C.id("cincinnasite_ingot"), NetherItems.CINCINNASITE_INGOT)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                     .group("nether_cincinnasite_ingot")
                     .outputCount(4)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("detector_rail"), Items.DETECTOR_RAIL)
                     .shape("X X", "X#X", "XRX")
                     .addMaterial('R', Items.REDSTONE)
                     .addMaterial('#', Items.STONE_PRESSURE_PLATE)
                     .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_detector_rail")
                     .outputCount(6)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("glass_bottle"), Items.GLASS_BOTTLE)
                     .shape("# #", " # ")
                     .addMaterial('#', NetherBlocks.QUARTZ_GLASS)
                     .group("nether_glass_bottle")
                     .outputCount(3)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("gray_dye"), Items.GRAY_DYE)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.GRAY_MOLD)
                     .group("nether_gray_dye")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("nether_ruby_from_block"), NetherItems.NETHER_RUBY)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.NETHER_RUBY_BLOCK)
                     .group("nether_nether_ruby_from_block")
                     .outputCount(9)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("paper"), Items.PAPER)
                     .shape("###")
                     .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                     .group("nether_paper")
                     .outputCount(3)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("rail"), Items.RAIL)
                     .shape("X X", "X#X", "X X")
                     .addMaterial('#', Items.STICK)
                     .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_rail")
                     .outputCount(16)
                     .category(RecipeCategory.TRANSPORTATION)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("red_dye"), Items.RED_DYE)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.RED_MOLD)
                     .group("nether_red_dye")
                     .outputCount(2)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_apple"), NetherItems.STALAGNATE_BOWL_APPLE)
                     .shape("W", "#")
                     .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                     .addMaterial('W', NetherItems.BLACK_APPLE)
                     .group("nether_stalagnate_bowl_apple")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stick"), Items.STICK)
                     .shape("#", "#")
                     .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                     .group("nether_stick")
                     .outputCount(2)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("sugar"), Items.SUGAR)
                     .shapeless()
                     .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                     .group("nether_sugar")
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("shield"), Items.SHIELD)
                     .shape("WoW", "WWW", " W ")
                     .addMaterial('W', ItemTags.PLANKS)
                     .addMaterial('o', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_shield")
                     .category(RecipeCategory.COMBAT)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("piston"), Items.PISTON)
                     .shape("TTT", "#X#", "#R#")
                     .addMaterial('R', Items.REDSTONE)
                     .addMaterial('#', Items.COBBLESTONE)
                     .addMaterial('T', ItemTags.PLANKS)
                     .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                     .group("nether_piston")
                     .category(RecipeCategory.REDSTONE)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl"), NetherItems.STALAGNATE_BOWL)
                     .shape("# #", " # ")
                     .addMaterial('#', NetherBlocks.MAT_STALAGNATE.getStem())
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
                NetherBlocks.CINCINNASITE_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterNether.C.id("copy_flaming_ruby_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                NetherTemplates.FLAMING_RUBY_TEMPLATE,
                NetherItems.NETHER_RUBY
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterNether.C.id("copy_cincinnasite_diamond_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE,
                NetherItems.CINCINNASITE_INGOT
        ).build(context);

        EquipmentSet.buildAllRecipes(BetterNether.C, context);
    }

    private static void registerShapeLess(RecipeOutput context) {
        RecipeBuilder.crafting(BetterNether.C.id("agave_medicine"), NetherItems.AGAVE_MEDICINE)
                     .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                     .addMaterial('A', NetherItems.AGAVE_LEAF)
                     .addMaterial('B', NetherItems.AGAVE_LEAF)
                     .addMaterial('C', NetherItems.AGAVE_LEAF)
                     .shapeless()
                     .group("nether_agave_medicine")
                     .category(RecipeCategory.FOOD)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("herbal_medicine"), NetherItems.HERBAL_MEDICINE)
                     .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                     .addMaterial('A', NetherItems.AGAVE_LEAF)
                     .addMaterial('B', NetherItems.BLACK_APPLE)
                     .addMaterial('C', NetherItems.HOOK_MUSHROOM_COOKED)
                     .addMaterial('D', Items.NETHER_WART)
                     .shapeless()
                     .group("nether_herbal_medicine")
                     .category(RecipeCategory.FOOD)
                     .build(context);
        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_mushroom"), NetherItems.STALAGNATE_BOWL_MUSHROOM)
                     .addMaterial('#', Items.BROWN_MUSHROOM)
                     .addMaterial('A', Items.RED_MUSHROOM)
                     .addMaterial('B', NetherItems.STALAGNATE_BOWL)
                     .shapeless()
                     .group("nether_stalagnate_bowl_mushroom")
                     .category(RecipeCategory.FOOD)
                     .build(context);

        RecipeBuilder.crafting(BetterNether.C.id("stalagnate_bowl_wart"), NetherItems.STALAGNATE_BOWL_WART)
                     .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                     .addMaterial('A', Items.NETHER_WART)
                     .addMaterial('B', Items.NETHER_WART)
                     .addMaterial('C', Items.NETHER_WART)
                     .shapeless()
                     .group("nether_stalagnate_bowl_wart")
                     .category(RecipeCategory.FOOD)
                     .build(context);
    }

    private static void registerSmelting(RecipeOutput context) {
        RecipeBuilder.smelting(BetterNether.C.id("black_dye"), Items.BLACK_DYE)
                     .input(NetherBlocks.INK_BUSH_SEED)
                     .cookingTime(200)
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("hook_mushroom_cooked"), NetherItems.HOOK_MUSHROOM_COOKED)
                     .input(NetherBlocks.HOOK_MUSHROOM)
                     .cookingTime(200)
                     .experience(0.1f)
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_ingot_from_shard"), NetherItems.CINCINNASITE_INGOT)
                     .input(NetherItems.CINCINNASITE)
                     .cookingTime(200)
                     .experience(0.5f)
                     .enableBlastFurnace()
                     .build(context);

        RecipeBuilder.smelting(BetterNether.C.id("cincinnasite_ingot_from_ore"), NetherItems.CINCINNASITE_INGOT)
                     .input(NetherBlocks.CINCINNASITE_ORE)
                     .cookingTime(200)
                     .experience(0.5f)
                     .enableBlastFurnace()
                     .build(context);
    }
}