package org.betterx.betternether.testmod.gametest;

import de.ambertation.wover.test.api.gametest.RecipeCoverageSweep;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Sweeps every {@code betternether:} equipment item for a recipe that produces it, via
 * {@code wover-test-api}'s {@link RecipeCoverageSweep} - same pattern and equipment-path filter as
 * BetterEnd's {@code RecipeCoverageGameTest}.
 */
public class RecipeCoverageGameTest {
    @GameTest
    public void everyEquipmentItemHasARecipe(GameTestHelper helper) {
        final List<String> missing = RecipeCoverageSweep.findItemsMissingARecipe(
                helper, "betternether", RecipeCoverageGameTest::looksLikeEquipment
        );

        if (!missing.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betternether items with no recipe producing them:\n - " + String.join("\n - ", missing)
            ));
        }
        helper.succeed();
    }

    private static boolean looksLikeEquipment(Item item) {
        final var key = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
        if (key == null) return false;
        final String path = key.identifier().getPath();
        return path.endsWith("_sword") || path.endsWith("_pickaxe") || path.endsWith("_axe")
                || path.endsWith("_shovel") || path.endsWith("_hoe")
                || path.endsWith("_helmet") || path.endsWith("_chestplate") || path.endsWith("_leggings")
                || path.endsWith("_boots");
    }
}
