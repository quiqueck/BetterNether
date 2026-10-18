package org.betterx.betternether.registry;

import net.minecraft.world.level.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;

public class NetherGameRules {
    /**
     * Whether a 2x2 of willow saplings may grow the giant willow.
     * <p>
     * Gates the sapling only - natural generation in the Old Swampland is untouched, so the same seed
     * keeps producing the same terrain whatever the setting. Turning it off means a 2x2 grows four
     * ordinary willows. Defaults to on: the giant tree is the point of planting four.
     */
    public static final GameRules.Key<GameRules.BooleanValue> GROW_LARGE_WILLOWS = GameRuleRegistry.register(
            "growLargeWillows",
            GameRules.Category.UPDATES,
            GameRuleFactory.createBooleanRule(true)
    );

    /**
     * Whether a 2x2 of anchor tree saplings planted on the floor may grow the giant anchor tree.
     * <p>
     * Ships off: the giant anchor tree reaches from the floor it was planted on up to the ceiling above
     * and needs 30 blocks of clearance, which is a lot of landscape to rearrange by accident. Opt in with
     * {@code /gamerule growLargeAnchorTrees true}. Like {@link #GROW_LARGE_WILLOWS} it never affects
     * worldgen.
     */
    public static final GameRules.Key<GameRules.BooleanValue> GROW_LARGE_ANCHOR_TREES = GameRuleRegistry.register(
            "growLargeAnchorTrees",
            GameRules.Category.UPDATES,
            GameRuleFactory.createBooleanRule(false)
    );

    public static void ensureStaticallyLoaded() {
    }
}
