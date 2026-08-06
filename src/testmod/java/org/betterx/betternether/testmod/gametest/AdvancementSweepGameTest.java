package org.betterx.betternether.testmod.gametest;

import de.ambertation.wover.test.api.gametest.AdvancementSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Sweeps every currently-loaded {@code betternether:} advancement for at least one criterion, via
 * {@code wover-test-api}'s {@link AdvancementSweep}. Doesn't hand-list them for the same reason
 * BetterEnd's version doesn't - datagen adds/removes generated "unlocked by crafting" entries over time.
 * <p>
 * {@code betternether:obsidian_blocks} in particular is a plain {@code minecraft:inventory_changed}
 * criterion with no mod code behind it ({@code ObsidianAdvancementGameTest}'s own doc comment already
 * explains why it isn't covered by a trigger test) - this sweep still checks it is well-formed, just not
 * that it can be awarded.
 */
public class AdvancementSweepGameTest {
    @GameTest
    public void everyAdvancementHasAtLeastOneCriterion(GameTestHelper helper) {
        final List<String> problems = AdvancementSweep.findMalformedAdvancements(helper, "betternether");

        if (!problems.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betternether advancement regression:\n - " + String.join("\n - ", problems)
            ));
        }
        helper.succeed();
    }
}
