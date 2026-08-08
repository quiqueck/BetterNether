package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;

import net.minecraft.world.level.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;

public class NetherGameRules {
    /**
     * Whether some of the obsidian in generated ruined portals is replaced with blue obsidian.
     * <p>
     * Defaults to on, matching the mod's existing behavior. Server owners who want vanilla-only
     * obsidian in ruined portals (e.g. for PBR resource pack compatibility) can turn this off with
     * {@code /gamerule generateBlueRuinedPortals false}.
     */
    public static final GameRules.Key<GameRules.BooleanValue> GENERATE_BLUE_RUINED_PORTALS = GameRuleRegistry.register(
            "generateBlueRuinedPortals",
            GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(true)
    );

    /**
     * Whether a 2x2 of willow saplings may grow the giant willow.
     * <p>
     * Gates the sapling only. Natural generation in the Old Swampland is untouched by this rule, and
     * deliberately so: a gamerule that changed worldgen would make the same seed produce different terrain
     * depending on a setting, and a server owner flipping it would find their existing world no longer
     * matching itself at the chunk border. Turning it off means a 2x2 grows four ordinary willows.
     * <p>
     * Filed under UPDATES beside {@code random_tick_speed}, which is what drives sapling growth in the
     * first place. Defaults to on - the giant tree is the point of planting four.
     */
    public static final GameRules.Key<GameRules.BooleanValue> GROW_LARGE_WILLOWS = GameRuleRegistry.register(
            "growLargeWillows",
            GameRules.Category.UPDATES,
            GameRuleFactory.createBooleanRule(true)
    );

    /**
     * Whether a 2x2 of anchor tree saplings may grow the giant anchor tree.
     * <p>
     * The one rule here that ships <em>off</em>. The giant anchor tree is not a big tree so much as a
     * cavern-sized structure: it reaches from the ceiling it was planted on all the way down to the floor
     * beneath it, and it needs 30 blocks of clearance before it will grow at all. Four saplings placed
     * without knowing that is a lot of landscape to rearrange by accident, so this one is opt-in -
     * {@code /gamerule growLargeAnchorTrees true} - while the willow, which grows something a player
     * can stand next to, is opt-out.
     *
     * @see #GROW_LARGE_WILLOWS for why this gates the sapling and not worldgen
     */
    public static final GameRules.Key<GameRules.BooleanValue> GROW_LARGE_ANCHOR_TREES = GameRuleRegistry.register(
            "growLargeAnchorTrees",
            GameRules.Category.UPDATES,
            GameRuleFactory.createBooleanRule(false)
    );

    public static void ensureStaticallyLoaded() {
    }
}
