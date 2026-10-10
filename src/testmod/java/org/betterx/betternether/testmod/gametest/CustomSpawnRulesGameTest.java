package org.betterx.betternether.testmod.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the one thing that silently killed every mob spawner BetterNether places in the Nether.
 * <p>
 * {@code SpawnData.CustomSpawnRules.isValidPosition} tests the sky limit against
 * {@code level.getEffectiveSkyBrightness(pos)}. From 1.20.5 up to the 26.3 snapshots that evaluated to a
 * <b>negative</b> number in the Nether (no sky light, minus a sky darkening of 11), outside the
 * {@code 0..15} every {@code sky_light_limit} is clamped to, so the check failed at every position and
 * spawners sat at {@code Delay: 0} forever (MC-307449). BetterNether carried a mixin for that until the
 * 26.3 release, which clamps the effective brightness at 0 and fixed it in vanilla.
 * <p>
 * The mixin is gone, so this test now asserts that vanilla keeps it fixed: a default
 * {@code custom_spawn_rules} must accept a Nether position. If it regresses, this fails immediately
 * instead of quietly disabling every city guard.
 */
public class CustomSpawnRulesGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** The range a {@code custom_spawn_rules} with no explicit limits decodes to. */
    private static final InclusiveRange<Integer> DEFAULT_LIMIT = new InclusiveRange<>(0, 15);

    @GameTest
    public void customSpawnRulesCanPassInTheNether(GameTestHelper helper) {
        final ServerLevel nether = helper.getLevel().getServer().getLevel(Level.NETHER);
        if (nether == null) {
            helper.fail("no Nether level on the test server");
            return;
        }

        final BlockPos pos = new BlockPos(0, 64, 0);
        final int effective = nether.getEffectiveSkyBrightness(pos);
        final SpawnData.CustomSpawnRules rules = new SpawnData.CustomSpawnRules(DEFAULT_LIMIT, DEFAULT_LIMIT);
        final boolean valid = rules.isValidPosition(pos, nether);
        LOGGER.warn("Nether getEffectiveSkyBrightness = {}, isValidPosition(default rules) = {}", effective, valid);

        if (!valid) {
            helper.fail("a default custom_spawn_rules rejects every Nether position again."
                    + " getEffectiveSkyBrightness there is " + effective + ", outside the 0..15 that"
                    + " checkLightBoundaries clamps every sky_light_limit to, so every spawner using"
                    + " custom_spawn_rules (all city guards) is unable to spawn in the Nether. Vanilla has"
                    + " regressed MC-307449; BetterNether needs its CustomSpawnRulesMixin back.");
            return;
        }

        helper.succeed();
    }
}
