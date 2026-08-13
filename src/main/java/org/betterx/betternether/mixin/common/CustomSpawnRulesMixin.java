package org.betterx.betternether.mixin.common;

import org.betterx.betternether.registry.NetherGameRules;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Restores pre-1.20.5 behaviour of {@code custom_spawn_rules}' sky-light test, without which every mob
 * spawner that uses those rules is <b>permanently unable to spawn anything in the Nether</b>.
 * <p>
 * {@link SpawnData.CustomSpawnRules#isValidPosition} compares the authored {@code sky_light_limit}
 * against {@code level.getEffectiveSkyBrightness(pos)}, which is
 * {@code getBrightness(SKY, pos) - getSkyDarken()}. The Nether has no sky light and
 * {@code Level.updateSkyBrightness} sets {@code skyDarken = (int)(15 - SKY_LIGHT_LEVEL)}, so the
 * expression is <b>negative</b> there - measured at {@code -11}. Every limit is clamped to
 * {@code 0..15} by {@code CustomSpawnRules.checkLightBoundaries}, so no authored range can possibly
 * contain it. The check fails at every candidate position, every spawn attempt takes the
 * {@code continue} in {@code BaseSpawner.serverTick} that does <i>not</i> set {@code delay}, and the
 * spawner therefore sits at {@code Delay: 0} for the rest of the world's life, ticking constantly and
 * spawning nothing.
 * <p>
 * Up to 1.20.4 the same line read {@code getBrightness(LightLayer.SKY, blockPos)}, which is 0 in the
 * Nether and inside {@code 0..15}. That single substitution is why BetterNether's city guard spawners
 * worked with this exact NBT in 1.20 and are inert in 26.2 - the templates never changed.
 * <p>

 * <p>
 * Reported upstream as <b>MC-307449</b>. This mixin exists only until that is fixed: once vanilla
 * clamps the value (or widens {@code LIGHT_RANGE} to admit it), delete this class, its entry in
 * {@code betternether.mixins.common.json}, and
 * {@link org.betterx.betternether.registry.NetherGameRules#FIX_SKY_LIGHT_SPAWN_RULES}.
 * {@code CustomSpawnRulesGameTest} keeps passing either way - it asserts the behaviour, not the mixin -
 * so it is safe to leave in place as the check that the upstream fix actually landed.
 * <p>
 * Gated three ways: the {@code betternether:fix_sky_light_spawn_rules} gamerule, the dimension having
 * no sky light at all, and the effective value actually being negative. It is deliberately not limited to
 * BetterNether’s own
 * spawners: the defect belongs to the vanilla check and affects any spawner using custom rules in a
 * dimension without sky light. {@code CustomSpawnRulesGameTest} fails if this stops working.
 */
@Mixin(SpawnData.CustomSpawnRules.class)
public class CustomSpawnRulesMixin {
    @Inject(method = "isValidPosition", at = @At("HEAD"), cancellable = true)
    private void bn_compareRawSkyLight(
            BlockPos blockSpawnPos,
            ServerLevel level,
            CallbackInfoReturnable<Boolean> cir
    ) {
        /*
        public boolean isValidPosition(final BlockPos blockSpawnPos, final ServerLevel level) {
            return
                this.blockLightLimit.isValueInRange(level.getBrightness(LightLayer.BLOCK, blockSpawnPos))
                && this.skyLightLimit.isValueInRange(level.getEffectiveSkyBrightness(blockSpawnPos));
        }
         */
        // Three gates, narrowest first. Any one of them failing leaves vanilla completely alone.

        // 1. Opt-out. The correction touches a vanilla method, so it is a server-owner decision.
        if (!level.getGameRules().get(NetherGameRules.FIX_SKY_LIGHT_SPAWN_RULES)) {
            return;
        }

        // 2. Only dimensions with no sky light. DO NOT remove this as redundant with (3) - it is a
        //    deliberate scope decision, not a correctness one.
        //
        //    The same defect does occur in sky-lit dimensions: the overworld's `day` timeline scales
        //    gameplay/sky_light_level by 0.2667 at night, i.e. to the same 4.0 the Nether states
        //    outright, so skyDarken is 11 and any position dimmer than that yields a negative value
        //    too - an overworld cave at night included. The clamp below would repair those just as
        //    correctly.
        //
        //    BetterNether is a Nether mod and does not get to change how spawners behave in dimensions
        //    it does not own, even to fix them. Among vanilla dimension types this condition selects
        //    the Nether alone (the End has sky light, and its sky_light_level is the default 15, so its
        //    effective value never goes negative); it additionally covers any datapack or mod dimension
        //    declaring has_skylight: false, which is the right generalisation of "no sky light" without
        //    hardcoding Level.NETHER. The general case belongs upstream.
        if (level.dimensionType().hasSkyLight()) {
            return;
        }

        // 3. Only where vanilla's comparison is unsatisfiable by construction.
        //    checkLightBoundaries clamps every authored sky_light_limit to 0..15, so a negative
        //    getEffectiveSkyBrightness cannot be inside ANY range a datapack is allowed to write - the
        //    check can only return false, for every spawner, at every position. Correcting it therefore
        //    cannot change a spawner that currently works: none can be working in that state.
        if (level.getEffectiveSkyBrightness(blockSpawnPos) >= 0) {
            return;
        }

        // Clamp rather than substitute getBrightness(LightLayer.SKY, pos). Replacing the call would
        // revert the time-of-day dimming that getEffectiveSkyBrightness deliberately introduced;
        // clamping keeps those semantics and repairs only the range violation - light cannot be
        // negative, and the value is compared against a limit restricted to 0..15. In a dimension with
        // no sky light the two are identical anyway (sky light is 0 and skyDarken > 0, so the effective
        // value clamps to 0), so this is the same fix for the Nether with a smaller change in meaning.
        final SpawnData.CustomSpawnRules self = (SpawnData.CustomSpawnRules) (Object) this;
        final int skyLight = Mth.clamp(level.getEffectiveSkyBrightness(blockSpawnPos), 0, 15);
        cir.setReturnValue(
                self.blockLightLimit().isValueInRange(level.getBrightness(LightLayer.BLOCK, blockSpawnPos))
                        && self.skyLightLimit().isValueInRange(skyLight)
        );
    }
}
