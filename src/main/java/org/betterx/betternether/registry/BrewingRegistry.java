package org.betterx.betternether.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

public class BrewingRegistry {
    private static final List<BrewingRecipe> RECIPES = new ArrayList<BrewingRecipe>();

    public static void register() {
        register(
                new ItemStack(NetherBlocks.BARREL_CACTUS),
                new ItemStack(Items.GLASS_BOTTLE),
                makePotion(Potions.WATER)
        );
        register(new ItemStack(NetherBlocks.HOOK_MUSHROOM), makePotion(Potions.AWKWARD), makePotion(Potions.HEALING));
    }

    private static void register(ItemStack source, ItemStack bottle, ItemStack result) {
        RECIPES.add(new BrewingRecipe(source, bottle, result));
    }

    private static ItemStack makePotion(Holder<Potion> potion) {
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    public static class BrewingRecipe {
        private final ItemStack source;
        private final ItemStack bottle;
        private final ItemStack result;

        public BrewingRecipe(ItemStack source, ItemStack bottle, ItemStack result) {
            this.source = source;
            this.bottle = bottle;
            this.result = result;
        }

        public boolean isValid(ItemStack source, ItemStack bottle) {
            return ItemStack.isSameItem(this.source, source) && ItemStack.isSameItem(this.bottle, bottle);
        }

        public boolean isValid(ItemStack source) {
            return ItemStack.isSameItem(this.source, source);
        }

        public ItemStack getResult() {
            return result;
        }
    }

    public static ItemStack getResult(ItemStack source, ItemStack bottle) {
        for (BrewingRecipe recipe : RECIPES)
            if (recipe.isValid(source, bottle))
                return recipe.getResult();
        return null;
    }

    public static boolean isValidIngridient(ItemStack source) {
        for (BrewingRecipe recipe : RECIPES)
            if (recipe.isValid(source))
                return true;
        return false;
    }
}
