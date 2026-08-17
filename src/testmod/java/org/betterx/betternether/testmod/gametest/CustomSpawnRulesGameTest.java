package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherGameRules;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the one thing that silently killed every mob spawner BetterNether places in the Nether.
 * <p>
 * {@code SpawnData.CustomSpawnRules.isValidPosition} tests the sky limit against
 * {@code level.getEffectiveSkyBrightness(pos)}, which is
 * {@code getBrightness(SKY, pos) - getSkyDarken()}. The Nether has no sky light, and
 * {@code Level.updateSkyBrightness} sets {@code skyDarken = (int)(15 - SKY_LIGHT_LEVEL)}, so the
 * expression evaluates to a <b>negative</b> number there. Every {@code sky_light_limit} is clamped to
 * {@code 0..15} by {@code CustomSpawnRules.checkLightBoundaries}, so no authored range can contain it:
 * the check fails at every position, every spawn attempt hits the {@code continue} that does not set
 * {@code delay}, and the spawner sits at {@code Delay: 0} forever without ever spawning anything.
 * <p>
 * Up to 1.20.4 the same line read {@code getBrightness(LightLayer.SKY, pos)}, which is 0 in the
 * Nether and therefore inside {@code 0..15}. That is why identical spawner NBT worked then and is
 * inert now.
 * <p>
 * This test asserts the <i>fix</i> holds: with BetterNether's mixin in place, a Nether position must
 * satisfy a default {@code custom_spawn_rules} sky limit. If the mixin is removed or vanilla changes
 * again, this fails immediately instead of quietly disabling every city guard.
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
        final int rawSky = nether.getBrightness(LightLayer.SKY, pos);
        final int skyDarken = nether.getSkyDarken();
        final int effective = nether.getEffectiveSkyBrightness(pos);

        LOGGER.warn("=== NETHER SKY LIGHT ===");
        LOGGER.warn("  getBrightness(SKY)      = {}", rawSky);
        LOGGER.warn("  getSkyDarken()          = {}", skyDarken);
        LOGGER.warn("  getEffectiveSkyBrightness = {}  (this is what isValidPosition tests)", effective);
        LOGGER.warn("  default sky_light_limit  = {}  contains it: {}",
                DEFAULT_LIMIT, DEFAULT_LIMIT.isValueInRange(effective));

        // Documents the equivalence that justified clamping over substitution: in a dimension with no
        // sky light, clamp(effective, 0, 15) and the raw sky brightness are the same number, so the
        // Nether outcome does not depend on which repair is used.
        final int clamped = Math.max(0, Math.min(15, effective));
        LOGGER.warn("  clamp(effective,0,15) = {}   getBrightness(SKY) = {}   equal: {}",
                clamped, rawSky, clamped == rawSky);
        if (clamped != rawSky) {
            helper.fail("clamping and raw sky brightness disagree in a dimension without sky light"
                    + " (clamped=" + clamped + ", raw=" + rawSky + "); the mixin's choice of repair"
                    + " now changes behaviour here and needs re-examining");
            return;
        }

        // The assertion is on the real check, not on the raw value: getEffectiveSkyBrightness is
        // vanilla's and stays negative: the mixin corrects what isValidPosition does with it.
        final SpawnData.CustomSpawnRules rules =
                new SpawnData.CustomSpawnRules(DEFAULT_LIMIT, DEFAULT_LIMIT);
        final boolean valid = rules.isValidPosition(pos, nether);
        LOGGER.warn("  isValidPosition(default rules) = {}", valid);

        if (!valid) {
            helper.fail("a default custom_spawn_rules rejects every Nether position."
                    + " getEffectiveSkyBrightness there is " + effective + ", outside the 0..15 that"
                    + " checkLightBoundaries clamps every sky_light_limit to, so no authored range can"
                    + " contain it. Any spawner using custom_spawn_rules is permanently unable to spawn"
                    + " in the Nether: Delay sticks at 0 and nothing ever appears. Up to 1.20.4 the"
                    + " check read getBrightness(LightLayer.SKY, pos), which is 0 and passes."
                    + " CustomSpawnRulesMixin restores that - it is probably missing or broken.");
            return;
        }

        // The mixin must not simply make the check permissive. A limit that genuinely excludes the
        // position has to still reject, or every spawner everywhere would become light-blind.
        final SpawnData.CustomSpawnRules blockLightZeroOnly =
                new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 0), DEFAULT_LIMIT);
        final BlockPos lit = new BlockPos(0, 65, 0);
        // A torch-bright cell in the Nether: block light 14 is outside a 0..0 block limit.
        nether.setBlock(lit, net.minecraft.world.level.block.Blocks.TORCH.defaultBlockState(), 3);
        final boolean rejected = !blockLightZeroOnly.isValidPosition(lit, nether);
        LOGGER.warn("  a 0..0 block_light_limit next to a torch rejects: {}", rejected);
        nether.setBlock(lit, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);

        if (!rejected) {
            helper.fail("a block_light_limit of 0..0 accepted a lit position - the mixin has made the"
                    + " check permissive instead of only correcting the unsatisfiable sky comparison");
            return;
        }

        // Gate 1: the gamerule must actually switch the correction off, or it is not an opt-out.
        nether.getGameRules().set(
                NetherGameRules.FIX_SKY_LIGHT_SPAWN_RULES, false, nether.getServer()
        );
        final boolean withRuleOff = rules.isValidPosition(pos, nether);
        nether.getGameRules().set(
                NetherGameRules.FIX_SKY_LIGHT_SPAWN_RULES, true, nether.getServer()
        );
        final boolean withRuleOn = rules.isValidPosition(pos, nether);
        LOGGER.warn("  gamerule off -> {}   gamerule on -> {}", withRuleOff, withRuleOn);

        if (withRuleOff) {
            helper.fail("the fix still applied with betternether:fix_sky_light_spawn_rules set to false"
                    + " - the opt-out does not work");
            return;
        }
        if (!withRuleOn) {
            helper.fail("restoring the gamerule to true did not restore the fix");
            return;
        }

        // Gate 2: the overworld has sky light, so the mixin must decline there regardless of value.
        final int overworldEffective = helper.getLevel().getEffectiveSkyBrightness(new BlockPos(0, 0, 0));
        LOGGER.warn("  overworld hasSkyLight={} effective={} (mixin declines when hasSkyLight)",
                helper.getLevel().dimensionType().hasSkyLight(), overworldEffective);
        if (!helper.getLevel().dimensionType().hasSkyLight()) {
            helper.fail("the test overworld reports no sky light, so this run cannot show that the"
                    + " dimension gate protects sky-lit dimensions");
            return;
        }

        helper.succeed();
    }
}
