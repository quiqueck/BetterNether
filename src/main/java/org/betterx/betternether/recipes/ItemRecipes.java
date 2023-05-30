package org.betterx.betternether.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemRecipes {
    public static void register() {
        BCLRecipeBuilder
                .crafting(BN.id("bn_glowstone_dust"), Items.GLOWSTONE_DUST)
                .setShape("###", "###", "###")
                .addMaterial('#', NetherItems.GLOWSTONE_PILE)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("cincinnasite_chains"), NetherBlocks.CINCINNASITE_CHAIN)
                .setOutputCount(3)
                .setShape("#", "#", "#")
                .addMaterial('#', NetherItems.CINCINNASITE_INGOT)
                .setCategory(RecipeCategory.DECORATIONS)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("lapis_pile_to_lapis"), Items.LAPIS_LAZULI)
                .setShape("###", "###", "###")
                .addMaterial('#', NetherItems.LAPIS_PILE)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("bn_yellow_dye"), Items.YELLOW_DYE)
                .setOutputCount(2)
                .shapeless()
                .addMaterial('#', NetherBlocks.BLOOMING_VINE)
                .setCategory(RecipeCategory.DECORATIONS)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("bn_glowstone_pile"), NetherItems.GLOWSTONE_PILE)
                .setOutputCount(2)
                .shapeless()
                .addMaterial('#', NetherBlocks.GOLDEN_VINE)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("bn_brown_mushroom"), Items.BROWN_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherBlocks.WALL_MUSHROOM_BROWN)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("wall_mushroom_brown"), NetherBlocks.WALL_MUSHROOM_BROWN)
                .shapeless()
                .addMaterial('#', Items.BROWN_MUSHROOM)
                .build();


        BCLRecipeBuilder
                .crafting(BN.id("bn_red_mushroom"), Items.RED_MUSHROOM)
                .shapeless()
                .addMaterial('#', NetherBlocks.WALL_MUSHROOM_RED)
                .build();

        BCLRecipeBuilder
                .crafting(BN.id("wall_red_brown"), NetherBlocks.WALL_MUSHROOM_RED)
                .shapeless()
                .addMaterial('#', Items.RED_MUSHROOM)
                .build();


        BCLRecipeBuilder.crafting(BetterNether.makeID("cincinnasite_ingot"), NetherItems.CINCINNASITE_INGOT)
                        .shapeless()
                        .addMaterial('#', NetherBlocks.CINCINNASITE_FORGED)
                        .setGroup("nether_cincinnasite_ingot")
                        .setOutputCount(4)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("detector_rail"), Items.DETECTOR_RAIL)
                        .setShape("X X", "X#X", "XRX")
                        .addMaterial('R', Items.REDSTONE)
                        .addMaterial('#', Items.STONE_PRESSURE_PLATE)
                        .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                        .setGroup("nether_detector_rail")
                        .setOutputCount(6)
                        .setCategory(RecipeCategory.TRANSPORTATION)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("glass_bottle"), Items.GLASS_BOTTLE)
                        .setShape("# #", " # ")
                        .addMaterial('#', NetherBlocks.QUARTZ_GLASS)
                        .setGroup("nether_glass_bottle")
                        .setOutputCount(3)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("gray_dye"), Items.GRAY_DYE)
                        .shapeless()
                        .addMaterial('#', NetherBlocks.GRAY_MOLD)
                        .setGroup("nether_gray_dye")
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("nether_ruby_from_block"), NetherItems.NETHER_RUBY)
                        .shapeless()
                        .addMaterial('#', NetherBlocks.NETHER_RUBY_BLOCK)
                        .setGroup("nether_nether_ruby_from_block")
                        .setOutputCount(9)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("paper"), Items.PAPER)
                        .setShape("###")
                        .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                        .setGroup("nether_paper")
                        .setOutputCount(3)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("rail"), Items.RAIL)
                        .setShape("X X", "X#X", "X X")
                        .addMaterial('#', Items.STICK)
                        .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                        .setGroup("nether_rail")
                        .setOutputCount(16)
                        .setCategory(RecipeCategory.TRANSPORTATION)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("red_dye"), Items.RED_DYE)
                        .shapeless()
                        .addMaterial('#', NetherBlocks.RED_MOLD)
                        .setGroup("nether_red_dye")
                        .setOutputCount(2)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("stalagnate_bowl_apple"), NetherItems.STALAGNATE_BOWL_APPLE)
                        .setShape("W", "#")
                        .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                        .addMaterial('W', NetherItems.BLACK_APPLE)
                        .setGroup("nether_stalagnate_bowl_apple")
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("stick"), Items.STICK)
                        .setShape("#", "#")
                        .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                        .setGroup("nether_stick")
                        .setOutputCount(2)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("sugar"), Items.SUGAR)
                        .shapeless()
                        .addMaterial('#', NetherBlocks.NETHER_REED_STEM)
                        .setGroup("nether_sugar")
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("shield"), Items.SHIELD)
                        .setShape("WoW", "WWW", " W ")
                        .addMaterial('W', ItemTags.PLANKS)
                        .addMaterial('o', NetherItems.CINCINNASITE_INGOT)
                        .setGroup("nether_shield")
                        .setCategory(RecipeCategory.COMBAT)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("piston"), Items.PISTON)
                        .setShape("TTT", "#X#", "#R#")
                        .addMaterial('R', Items.REDSTONE)
                        .addMaterial('#', Items.COBBLESTONE)
                        .addMaterial('T', ItemTags.PLANKS)
                        .addMaterial('X', NetherItems.CINCINNASITE_INGOT)
                        .setGroup("nether_piston")
                        .setCategory(RecipeCategory.REDSTONE)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("stalagnate_bowl"), NetherItems.STALAGNATE_BOWL)
                        .setShape("# #", " # ")
                        .addMaterial('#', NetherBlocks.MAT_STALAGNATE.getStem())
                        .setGroup("nether_stalagnate_bowl")
                        .setOutputCount(3)
                        .setCategory(RecipeCategory.DECORATIONS)
                        .build();

        registerShapeLess();
        registerSmelting();

        BCLRecipeBuilder.copySmithingTemplate(
                BetterNether.makeID("copy_bowl_upgrade"),
                NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE,
                NetherBlocks.CINCINNASITE_BLOCK
        ).build();
    }

    private static void registerShapeLess() {
        BCLRecipeBuilder.crafting(BetterNether.makeID("agave_medicine"), NetherItems.AGAVE_MEDICINE)
                        .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                        .addMaterial('A', NetherItems.AGAVE_LEAF)
                        .addMaterial('B', NetherItems.AGAVE_LEAF)
                        .addMaterial('C', NetherItems.AGAVE_LEAF)
                        .shapeless()
                        .setGroup("nether_agave_medicine")
                        .setCategory(RecipeCategory.FOOD)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("herbal_medicine"), NetherItems.HERBAL_MEDICINE)
                        .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                        .addMaterial('A', NetherItems.AGAVE_LEAF)
                        .addMaterial('B', NetherItems.BLACK_APPLE)
                        .addMaterial('C', NetherItems.HOOK_MUSHROOM_COOKED)
                        .addMaterial('D', Items.NETHER_WART)
                        .shapeless()
                        .setGroup("nether_herbal_medicine")
                        .setCategory(RecipeCategory.FOOD)
                        .build();
        BCLRecipeBuilder.crafting(BetterNether.makeID("stalagnate_bowl_mushroom"), NetherItems.STALAGNATE_BOWL_MUSHROOM)
                        .addMaterial('#', Items.BROWN_MUSHROOM)
                        .addMaterial('A', Items.RED_MUSHROOM)
                        .addMaterial('B', NetherItems.STALAGNATE_BOWL)
                        .shapeless()
                        .setGroup("nether_stalagnate_bowl_mushroom")
                        .setCategory(RecipeCategory.FOOD)
                        .build();

        BCLRecipeBuilder.crafting(BetterNether.makeID("stalagnate_bowl_wart"), NetherItems.STALAGNATE_BOWL_WART)
                        .addMaterial('#', NetherItems.STALAGNATE_BOWL)
                        .addMaterial('A', Items.NETHER_WART)
                        .addMaterial('B', Items.NETHER_WART)
                        .addMaterial('C', Items.NETHER_WART)
                        .shapeless()
                        .setGroup("nether_stalagnate_bowl_wart")
                        .setCategory(RecipeCategory.FOOD)
                        .build();
    }

    private static void registerSmelting() {
        BCLRecipeBuilder.smelting(BetterNether.makeID("black_dye"), Items.BLACK_DYE)
                        .setPrimaryInputAndUnlock(NetherBlocks.INK_BUSH_SEED)
                        .setCookingTime(200)
                        .build();

        BCLRecipeBuilder.smelting(BetterNether.makeID("hook_mushroom_cooked"), NetherItems.HOOK_MUSHROOM_COOKED)
                        .setPrimaryInputAndUnlock(NetherBlocks.HOOK_MUSHROOM)
                        .setCookingTime(200)
                        .setExperience(0.1f)
                        .build();

        BCLRecipeBuilder.smelting(BetterNether.makeID("cincinnasite_ingot_from_shard"), NetherItems.CINCINNASITE_INGOT)
                        .setPrimaryInputAndUnlock(NetherItems.CINCINNASITE)
                        .setCookingTime(200)
                        .setExperience(0.5f)
                        .buildWithBlasting();

        BCLRecipeBuilder.smelting(BetterNether.makeID("cincinnasite_ingot_from_ore"), NetherItems.CINCINNASITE_INGOT)
                        .setPrimaryInputAndUnlock(NetherBlocks.CINCINNASITE_ORE)
                        .setCookingTime(200)
                        .setExperience(0.5f)
                        .buildWithBlasting();

    }

    private static boolean itemExists(Item item) {
        return BuiltInRegistries.ITEM.getKey(item) != BuiltInRegistries.ITEM.getDefaultKey();
    }

    private static boolean blockExists(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block) != BuiltInRegistries.BLOCK.getDefaultKey();
    }
}
