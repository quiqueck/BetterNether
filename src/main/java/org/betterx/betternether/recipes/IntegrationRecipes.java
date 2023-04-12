package org.betterx.betternether.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.BN;
import org.betterx.betternether.integrations.VanillaExcavatorsIntegration;
import org.betterx.betternether.integrations.VanillaHammersIntegration;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class IntegrationRecipes {
    public static void register() {
        if (VanillaHammersIntegration.hasHammers()) {
            makeHammerRecipe(
                    NetherItems.CINCINNASITE_HAMMER,
                    NetherBlocks.CINCINNASITE_FORGED,
                    NetherItems.CINCINNASITE_INGOT
            );
            makeHammerRecipe(NetherItems.NETHER_RUBY_HAMMER, NetherBlocks.NETHER_RUBY_BLOCK, NetherItems.NETHER_RUBY);

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(NetherItems.CINCINNASITE_HAMMER_DIAMOND);
            boolean register = id != BuiltInRegistries.ITEM.getDefaultKey() && BuiltInRegistries.ITEM.getKey(NetherItems.CINCINNASITE_HAMMER) != BuiltInRegistries.ITEM.getDefaultKey();
            if (register) {
                BCLRecipeBuilder
                        .crafting(BN.id("cincinnasite_hammer_diamond"), NetherItems.CINCINNASITE_HAMMER_DIAMOND)
                        .setShape("#I#")
                        .addMaterial('#', Items.DIAMOND)
                        .addMaterial('I', NetherItems.CINCINNASITE_HAMMER)
                        .setCategory(RecipeCategory.TOOLS)
                        .build();
            }
        }

        if (VanillaExcavatorsIntegration.hasExcavators()) {
            makeExcavatorRecipe(
                    NetherItems.CINCINNASITE_EXCAVATOR,
                    NetherBlocks.CINCINNASITE_FORGED,
                    NetherItems.CINCINNASITE_INGOT
            );
            makeExcavatorRecipe(
                    NetherItems.NETHER_RUBY_EXCAVATOR,
                    NetherBlocks.NETHER_RUBY_BLOCK,
                    NetherItems.NETHER_RUBY
            );

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(NetherItems.CINCINNASITE_EXCAVATOR_DIAMOND);
            boolean register = id != BuiltInRegistries.ITEM.getDefaultKey() && BuiltInRegistries.ITEM.getKey(NetherItems.CINCINNASITE_EXCAVATOR) != BuiltInRegistries.ITEM.getDefaultKey();
            if (register) {
                BCLRecipeBuilder
                        .crafting(BN.id("cincinnasite_excavator_diamond"), NetherItems.CINCINNASITE_EXCAVATOR_DIAMOND)
                        .setShape("#I#")
                        .addMaterial('#', Items.DIAMOND)
                        .addMaterial('I', NetherItems.CINCINNASITE_EXCAVATOR)
                        .setCategory(RecipeCategory.TOOLS)
                        .build();
            }
        }
    }

    private static void makeHammerRecipe(Item hammer, Block block, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(hammer);
        boolean register = id != BuiltInRegistries.ITEM.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(NetherBlocks.MAT_REED.getStem()) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(block) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.ITEM.getKey(item) != BuiltInRegistries.ITEM.getDefaultKey();
        if (register) {
            BCLRecipeBuilder
                    .crafting(BN.id(id.getPath()), hammer)
                    .setShape("#I#", " S ", " S ")
                    .addMaterial('#', block)
                    .addMaterial('I', item)
                    .addMaterial('S', NetherBlocks.MAT_REED.getStem())
                    .setCategory(RecipeCategory.TOOLS)
                    .build();
        }
    }

    private static void makeExcavatorRecipe(Item excavator, Block block, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(excavator);
        boolean register = id != BuiltInRegistries.ITEM.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(NetherBlocks.MAT_REED.getStem()) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(block) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.ITEM.getKey(item) != BuiltInRegistries.ITEM.getDefaultKey();
        if (register) {
            BCLRecipeBuilder
                    .crafting(BN.id(id.getPath()), excavator)
                    .setShape(" I ", "#S#", " S ")
                    .addMaterial('#', block)
                    .addMaterial('I', item)
                    .addMaterial('S', NetherBlocks.MAT_REED.getStem())
                    .setCategory(RecipeCategory.TOOLS)
                    .build();
        }
    }
}
