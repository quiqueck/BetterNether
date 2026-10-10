package org.betterx.betternether.testmod.gametest;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether #119/#144: recipes listed in a datapack's {@code data/bclib/config/recipes.json} must be gone
 * from the game, not just reported as disabled in the log.
 * <p>
 * The testmod ships two probe recipes and a {@code recipes.json} that disables one of them. The disabled one
 * must be unknown to the recipe manager (so neither crafting table, crafter nor recipe book can use it) and
 * its grid must craft nothing; the other one, loaded the same way, must still craft.
 */
public class RecipeDisableConfigGameTest {
    private static final Identifier DISABLED = Identifier.fromNamespaceAndPath("betternether-testmod", "disabled_probe");
    private static final Identifier ENABLED = Identifier.fromNamespaceAndPath("betternether-testmod", "enabled_probe");

    @GameTest
    public void disabledRecipeIsNotCraftable(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final RecipeManager recipes = level.getServer().getRecipeManager();

        helper.assertTrue(
                recipes.byKey(key(ENABLED)).isPresent(),
                Component.literal("control recipe " + ENABLED + " was not loaded, the test datapack is missing")
        );
        helper.assertTrue(
                recipes.byKey(key(DISABLED)).isEmpty(),
                Component.literal(DISABLED + " is disabled in recipes.json but still known to the recipe manager")
        );
        helper.assertTrue(
                recipes.getRecipes().stream().noneMatch(h -> h.id().identifier().equals(DISABLED)),
                Component.literal(DISABLED + " is disabled in recipes.json but still listed with all recipes")
        );

        final Optional<RecipeHolder<?>> disabled = craft(level, List.of(Items.DEAD_BUSH, Items.POISONOUS_POTATO));
        helper.assertTrue(
                disabled.isEmpty(),
                Component.literal("the grid of the disabled recipe still crafts via "
                        + disabled.map(h -> h.id().identifier().toString()).orElse(""))
        );

        final Optional<RecipeHolder<?>> enabled = craft(level, List.of(Items.DEAD_BUSH, Items.DEAD_BUSH, Items.POISONOUS_POTATO));
        helper.assertTrue(
                enabled.map(h -> h.id().identifier().equals(ENABLED)).orElse(false),
                Component.literal("the grid of the enabled recipe crafted via "
                        + enabled.map(h -> h.id().identifier().toString()).orElse("nothing") + ", expected " + ENABLED)
        );
        helper.succeed();
    }

    private static ResourceKey<Recipe<?>> key(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    private static Optional<RecipeHolder<?>> craft(ServerLevel level, List<Item> items) {
        final CraftingInput input = CraftingInput.of(items.size(), 1, items.stream().map(ItemStack::new).toList());
        return level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level).map(h -> (RecipeHolder<?>) h);
    }
}
