package org.betterx.betternether.testmod.gametest;

import de.ambertation.wover.test.api.gametest.PottableSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Mirrors BetterEnd's {@code FlowerPotGameTest} for {@code cincinnasite_pot} - checks every
 * {@code betternether:} plant's declared soil tag is actually satisfiable by some registered soil, via
 * {@code wover-test-api}'s {@link PottableSweep}. See that class's javadoc for why this is a data check
 * rather than a physical "plant it in my pot block" interaction.
 */
public class CincinnasitePotGameTest {
    @GameTest
    public void everyPlantsSoilTagIsSatisfiableBySomeRegisteredSoil(GameTestHelper helper) {
        final List<String> problems = PottableSweep.findPlantsWithUnsatisfiableSoilTag(helper, "betternether");

        if (!problems.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betternether pottable-plant regression:\n - " + String.join("\n - ", problems)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void atLeastOnePlantAndSoilAreRegistered(GameTestHelper helper) {
        if (PottableSweep.plantsIn(helper, "betternether").isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "no betternether plants are registered as pottable at all"
            ));
        }
        helper.succeed();
    }
}
