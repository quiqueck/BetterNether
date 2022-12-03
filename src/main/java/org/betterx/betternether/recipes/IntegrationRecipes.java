package org.betterx.betternether.recipes;

import org.betterx.betternether.integrations.VanillaExcavatorsIntegration;
import org.betterx.betternether.integrations.VanillaHammersIntegration;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

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
                String[] shape = new String[]{"#I#"};
                Map<String, ItemStack> materials = ImmutableMap.of(
                        "#", new ItemStack(Items.DIAMOND),
                        "I", new ItemStack(NetherItems.CINCINNASITE_HAMMER)
                );
                ItemStack result = new ItemStack(NetherItems.CINCINNASITE_HAMMER_DIAMOND);
                BNRecipeManager.addCraftingRecipe("cincinnasite_hammer_diamond", shape, materials, result);
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
                String[] shape = new String[]{"#I#"};
                Map<String, ItemStack> materials = ImmutableMap.of(
                        "#", new ItemStack(Items.DIAMOND),
                        "I", new ItemStack(NetherItems.CINCINNASITE_EXCAVATOR)
                );
                ItemStack result = new ItemStack(NetherItems.CINCINNASITE_EXCAVATOR_DIAMOND);
                BNRecipeManager.addCraftingRecipe("cincinnasite_excavator_diamond", shape, materials, result);
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
            Map<String, ItemStack> materials = ImmutableMap.of(
                    "#", new ItemStack(block),
                    "I", new ItemStack(item),
                    "S", new ItemStack(NetherBlocks.MAT_REED.getStem())
            );
            ItemStack result = new ItemStack(hammer);
            BNRecipeManager.addCraftingRecipe(id.getPath(), new String[]{"#I#", " S ", " S "}, materials, result);
        }
    }

    private static void makeExcavatorRecipe(Item excavator, Block block, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(excavator);
        boolean register = id != BuiltInRegistries.ITEM.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(NetherBlocks.MAT_REED.getStem()) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.BLOCK.getKey(block) != BuiltInRegistries.BLOCK.getDefaultKey() &&
                BuiltInRegistries.ITEM.getKey(item) != BuiltInRegistries.ITEM.getDefaultKey();
        if (register) {
            Map<String, ItemStack> materials = ImmutableMap.of(
                    "#", new ItemStack(block),
                    "I", new ItemStack(item),
                    "S", new ItemStack(NetherBlocks.MAT_REED.getStem())
            );
            ItemStack result = new ItemStack(excavator);
            BNRecipeManager.addCraftingRecipe(id.getPath(), new String[]{" I ", "#S#", " S "}, materials, result);
        }
    }
}
