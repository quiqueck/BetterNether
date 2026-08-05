package org.betterx.betternether.registry;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

public class BrewingRegistry {
    // Built lazily on first actual use (a real brewing-stand interaction, well after mod bootstrap
    // completes) rather than eagerly from register(): both the plain ItemStack ctor and
    // PotionContents.createItemStack read item/potion data components, which are not bound yet
    // during BetterNether.onInitialize()'s registration chain ("Components not bound yet").
    private static List<BrewingRecipe> recipes = null;

    public static void register() {
        // Kept as a no-op call site for BetterNether.onInitialize() - recipe construction is
        // deferred to getRecipes() below.
    }

    private static List<BrewingRecipe> getRecipes() {
        if (recipes == null) {
            recipes = new ArrayList<>();
            recipes.add(new BrewingRecipe(
                    new ItemStack(NetherPlantBlocks.BARREL_CACTUS),
                    new ItemStack(Items.GLASS_BOTTLE),
                    makePotion(Potions.WATER)
            ));
            recipes.add(new BrewingRecipe(
                    new ItemStack(NetherMushroomBlocks.HOOK_MUSHROOM),
                    makePotion(Potions.AWKWARD),
                    makePotion(Potions.HEALING)
            ));
        }
        return recipes;
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
        for (BrewingRecipe recipe : getRecipes())
            if (recipe.isValid(source, bottle))
                return recipe.getResult();
        return null;
    }

    public static boolean isValidIngridient(ItemStack source) {
        for (BrewingRecipe recipe : getRecipes())
            if (recipe.isValid(source))
                return true;
        return false;
    }
}
