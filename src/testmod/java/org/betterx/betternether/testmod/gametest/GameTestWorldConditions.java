package org.betterx.betternether.testmod.gametest;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.gamerules.GameRules;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Pins the game test world to a clear noon before any test runs.
 * <p>
 * The game test server reuses {@code build/gametest/world}, and while it writes a fresh
 * {@code level.dat} every boot, the overworld clock ({@code data/minecraft/world_clocks.dat}) and the
 * weather ({@code weather.dat}) are saved data that survive. Each run advanced the clock by a few
 * thousand ticks, so after a handful of runs the suite started at night (and possibly in rain), and every
 * test that relies on the open arena being sky-lit failed deterministically until the world folder was
 * deleted: the oak sapling control in {@code SaplingGrowthGameTest} (oak needs brightness 9) and the
 * passive {@code Animal} mobs in {@code SpawnEggSpawnerGameTest} (their walk-target value is negative in
 * the dark, so {@code Mob#checkSpawnRules} rejects every spawner attempt).
 * <p>
 * Mirrors what vanilla does for the debug world: weather stops advancing and the overworld clock is
 * moved to noon - and paused here as well, so a long suite cannot run into the evening either.
 * {@code SERVER_STARTED} fires before the first tick, and the first tick updates the sky darkening
 * before the game test server starts its tests.
 */
public class GameTestWorldConditions implements ModInitializer {
    @Override
    public void onInitialize() {
        // Only the GameTest run sets this; the compat boot runs share the testmod's entrypoints.
        if (System.getProperty("fabric-api.gametest") == null) return;
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.getGameRules().set(GameRules.ADVANCE_WEATHER, false, server);
            server.setWeatherParameters(0, 0, false, false);
            for (ServerLevel level : server.getAllLevels()) {
                level.setRainLevel(0);
                level.setThunderLevel(0);
            }

            final var overworldClock = server.registryAccess().getOrThrow(WorldClocks.OVERWORLD);
            server.clockManager().moveToTimeMarker(overworldClock, ClockTimeMarkers.NOON);
            server.clockManager().setPaused(overworldClock, true);
        });
    }
}
