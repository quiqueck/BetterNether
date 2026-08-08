package org.betterx.betternether.registry;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrewingRegistry {
    private static final List<BrewingRecipe> RECIPES = new ArrayList<BrewingRecipe>();

    public static void register() {
        register(
                new ItemStack(NetherPlantBlocks.BARREL_CACTUS),
                new ItemStack(Items.GLASS_BOTTLE),
                makePotion(Potions.WATER)
        );
        register(new ItemStack(NetherMushroomBlocks.HOOK_MUSHROOM), makePotion(Potions.AWKWARD), makePotion(Potions.HEALING));
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
            return ItemStack.isSameItem(this.source, source) && matchesBottle(bottle);
        }

        /**
         * {@code ItemStack.isSameItem} alone is not enough for the bottle slot: every potion
         * (water, awkward, swiftness, ...) is the same {@code minecraft:potion} item and only the
         * {@code potion_contents} data component tells them apart, so the awkward -> healing brew used to
         * accept <em>any</em> potion.
         * <p>
         * The comparison mirrors the generated {@code minecraft:brewing} recipes on the 26.3 branch, which
         * are the authoritative statement of the intended behaviour: the input is pinned with a
         * {@code potion_contents} predicate only where the template actually names a potion
         * (awkward for hook mushroom, nothing at all for the barrel-cactus glass bottle), and that
         * predicate - {@code PotionsPredicate} - looks at the {@code potion} field alone. Custom effects,
         * custom color and a custom name stay irrelevant, so an anvil-renamed awkward potion still brews.
         * That is also why this is deliberately not {@code ItemStack.isSameItemSameComponents}, which would
         * additionally demand identical decoration and reject those stacks.
         */
        private boolean matchesBottle(ItemStack bottle) {
            if (!ItemStack.isSameItem(this.bottle, bottle)) return false;

            final Optional<Holder<Potion>> expected = potionOf(this.bottle);
            if (expected.isEmpty()) return true;

            final Optional<Holder<Potion>> actual = potionOf(bottle);
            return actual.isPresent() && actual.get().is(expected.get());
        }

        private static Optional<Holder<Potion>> potionOf(ItemStack stack) {
            final PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            return contents == null ? Optional.empty() : contents.potion();
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
