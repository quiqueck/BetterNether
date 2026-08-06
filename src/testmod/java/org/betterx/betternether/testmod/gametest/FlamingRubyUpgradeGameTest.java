package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.test.api.gametest.RecipeCoverageSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the one gap in Flaming Ruby's otherwise-thorough coverage: {@code RubyFireGameTest} and
 * {@code ObsidianBreakerGameTest} both exercise {@code FLAMING_RUBY_SET} pieces, but only by building
 * an already-enchanted stack directly - neither drives the actual
 * {@code nether_ruby_set + FLAMING_RUBY_TEMPLATE + nether_ruby -> flaming_ruby_set} smithing upgrade.
 * The per-piece upgrade recipes are emitted automatically via {@code EquipmentSet#add}'s item RECIPE
 * traits (not hand-listed in the datagen provider, which only registers the template-copy recipe), so
 * this reuses {@code wover-test-api}'s {@link RecipeCoverageSweep} - the same mechanism that already
 * proved it can see auto-generated smithing recipes on BetterEnd's equipment tiers - rather than
 * assuming a specific recipe id.
 */
public class FlamingRubyUpgradeGameTest {
    @GameTest
    public void flamingRubyPickaxeHasAnUpgradeRecipe(GameTestHelper helper) {
        final Item pickaxe = NetherEquipmentItems.FLAMING_RUBY_SET.get(ToolSlot.PICKAXE_SLOT);
        final List<String> missing = RecipeCoverageSweep.findItemsMissingARecipe(
                helper, "betternether", item -> item == pickaxe
        );

        if (!missing.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "no recipe produces the flaming_ruby pickaxe (expected the nether_ruby_set smithing"
                            + " upgrade, emitted via EquipmentSet#add's RECIPE trait)"
            ));
        }
        helper.succeed();
    }
}
