package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.item.NetherResourceItems;

import de.ambertation.wover.test.api.gametest.CraftingConflictSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Crafting-grid conflicts between betternether recipes and any other crafting recipe (#188, #190),
 * plus direct crafting checks for the grids those reports and #194 (bone button) were about.
 */
public class RecipeConflictGameTest {
    @GameTest
    public void noCraftingRecipeShadowsAnother(GameTestHelper helper) {
        final List<String> conflicts = CraftingConflictSweep.findConflicts(helper, "betternether");
        if (!conflicts.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "crafting grids accepted by recipes with different results:\n - " + String.join("\n - ", conflicts)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void contestedGridsCraftTheExpectedItem(GameTestHelper helper) {
        // #190: 2x2 iron ingots -> iron trapdoor, 2x2 cincinnasite ingots -> forged cincinnasite
        expect(helper, square(Items.IRON_INGOT, 2, 2), Items.IRON_TRAPDOOR);
        expect(helper, square(NetherResourceItems.CINCINNASITE_INGOT, 2, 2), NetherMetalBlocks.CINCINNASITE_FORGED);
        // #188: 3x2 iron ingots -> iron bars, 3x2 cincinnasite ingots -> cincinnasite bars
        expect(helper, square(Items.IRON_INGOT, 3, 2), Items.IRON_BARS);
        expect(helper, square(NetherResourceItems.CINCINNASITE_INGOT, 3, 2), NetherMetalBlocks.CINCINNASITE_BARS);
        // #194: a single bone block -> bone button
        expect(helper, square(NetherStoneBlocks.BONE_BLOCK, 1, 1), NetherStoneBlocks.BONE_BUTTON);
        helper.succeed();
    }

    private static CraftingInput square(ItemLike item, int width, int height) {
        return CraftingInput.of(width, height, Collections.nCopies(width * height, new ItemStack(item)));
    }

    private static void expect(GameTestHelper helper, CraftingInput input, ItemLike expected) {
        final var level = helper.getLevel();
        final ItemStack result = level.recipeAccess()
                                      .getRecipeFor(RecipeType.CRAFTING, input, level)
                                      .map(h -> h.value().assemble(input, level.registryAccess()))
                                      .orElse(ItemStack.EMPTY);
        final Item want = expected.asItem();
        if (!result.is(want)) {
            throw helper.assertionException(Component.literal(
                    "expected " + want + " but crafting produced " + (result.isEmpty() ? "nothing" : result.getItem())
            ));
        }
    }
}
