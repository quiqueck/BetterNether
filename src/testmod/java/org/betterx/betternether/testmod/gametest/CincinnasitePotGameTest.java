package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockBNPot;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import de.ambertation.wover.test.api.gametest.FluidWashSweep;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;
import de.ambertation.wover.test.api.gametest.PottableSweep;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
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

    private static List<Block> pots() {
        final List<Block> pots = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof BlockBNPot) pots.add(block);
        }
        return pots;
    }

    /**
     * Every pot is in {@code #minecraft:flower_pots} like vanilla's {@code flower_pot}. The plant sits in the
     * separate {@code potted_plant} block above the pot - a full-collision block, not a pot - so it stays out,
     * and with it out of {@code #minecraft:washed_away_by_fluids} (which includes the pot tag on 26.3).
     */
    @GameTest
    public void potsAreFlowerPots(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final List<Block> pots = pots();
        if (pots.size() < 2) failures.add("found only " + pots.size() + " pots");
        for (Block pot : pots) {
            if (!pot.defaultBlockState().is(BlockTags.FLOWER_POTS)) {
                failures.add(FluidWashSweep.id(pot) + " is not in #minecraft:flower_pots");
            }
            if (!pot.defaultBlockState().is(BlockTags.WASHED_AWAY_BY_FLUIDS)) {
                failures.add(FluidWashSweep.id(pot) + " is not in #minecraft:washed_away_by_fluids");
            }
        }
        if (NetherPlantBlocks.POTTED_PLANT.defaultBlockState().is(BlockTags.FLOWER_POTS)) {
            failures.add("potted_plant (the plant above a pot) is in #minecraft:flower_pots");
        }
        if (NetherPlantBlocks.POTTED_PLANT.defaultBlockState().is(BlockTags.WASHED_AWAY_BY_FLUIDS)) {
            failures.add("potted_plant (full collision) is in #minecraft:washed_away_by_fluids");
        }
        GameTestAssertions.failIfAny(helper, "Pots vs #minecraft:flower_pots", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 200)
    public void waterWashesPotsAway(GameTestHelper helper) {
        wash(helper, Blocks.WATER);
    }

    @GameTest(maxTicks = 200)
    public void lavaWashesPotsAway(GameTestHelper helper) {
        wash(helper, Blocks.LAVA);
    }

    /** Vanilla's flower_pot runs alongside as the control. */
    private static void wash(GameTestHelper helper, Block fluid) {
        final List<Block> pots = new ArrayList<>();
        pots.add(Blocks.FLOWER_POT);
        pots.addAll(pots());
        final List<Block> skipped = FluidWashSweep.washEach(helper, fluid, pots, List.of(Blocks.STONE));
        if (!skipped.isEmpty()) {
            GameTestAssertions.failIfAny(helper, "Setup", List.of("could not place " + skipped.stream().map(FluidWashSweep::id).toList()));
        }
    }
}
