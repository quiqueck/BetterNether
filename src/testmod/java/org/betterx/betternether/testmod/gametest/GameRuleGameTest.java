package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherGameRules;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the 3 custom gamerules that can be checked without worldgen: {@code
 * gloomwisp_drops_experience}/{@code grow_large_willows}/{@code grow_large_anchor_trees} default values
 * and set/get round-tripping through {@code GameRules#set(GameRule, T, MinecraftServer)}.
 * <p>
 * {@code generate_blue_ruined_portals} is skipped - its consumer, {@code BlockAgeProcessorMixin}, gates
 * worldgen-time ruined-portal block replacement, so exercising it needs an actual ruined-portal feature
 * placement rather than a synthetic setup, per the plan this class implements.
 * <p>
 * <b>Scope note</b>: this checks the rule values themselves round-trip correctly through the real
 * vanilla {@code GameRules} API, not each rule's downstream behavioural effect (a specific willow
 * growing 2x2 vs 1x1, a gloomwisp actually shedding an experience orb - the latter's own method,
 * {@code BlockGloomwispVine#shedExperience}, is private and gated on both the rule and a
 * {@code EXPERIENCE_CHANCE} random roll, so proving it needs driving the vine's public push-through
 * interaction rather than the rule alone). The rule itself is confirmed to gate real behavior by reading
 * {@code BlockWillowSapling.java:35} and {@code BlockGloomwispVine.java:444} directly.
 */
public class GameRuleGameTest {
    @GameTest
    public void defaultsMatchDocumentedValues(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final var rules = helper.getLevel().getGameRules();

        if (!rules.get(NetherGameRules.GLOOMWISP_DROPS_EXPERIENCE)) {
            failures.add("gloomwisp_drops_experience defaults to false, expected true");
        }
        if (!rules.get(NetherGameRules.GROW_LARGE_WILLOWS)) {
            failures.add("grow_large_willows defaults to false, expected true");
        }
        if (rules.get(NetherGameRules.GROW_LARGE_ANCHOR_TREES)) {
            failures.add("grow_large_anchor_trees defaults to true, expected false (the one opt-in rule)");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Gamerule default regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void everyRuleRoundTripsThroughSet(GameTestHelper helper) {
        final var rules = helper.getLevel().getGameRules();
        final var server = helper.getLevel().getServer();
        final List<String> failures = new ArrayList<>();

        for (GameRule<Boolean> rule : List.of(
                NetherGameRules.GLOOMWISP_DROPS_EXPERIENCE,
                NetherGameRules.GROW_LARGE_WILLOWS,
                NetherGameRules.GROW_LARGE_ANCHOR_TREES
        )) {
            final boolean original = rules.get(rule);
            rules.set(rule, !original, server);
            if (rules.get(rule) == original) {
                failures.add(rule + ": value did not change after set()");
            }
            rules.set(rule, original, server); // restore
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Gamerule set/get regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
