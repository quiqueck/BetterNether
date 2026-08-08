package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;

public class NetherGameRules {
    /**
     * Whether gloomwisps shed experience when something pushes through them.
     * <p>
     * Filed under DROPS beside {@code block_drops} and {@code entity_drops}, which is where a server
     * owner turning off incidental sources of experience would look for it. Defaults to on: the drip
     * is the point of the plant, and a rule that ships disabled is one nobody discovers.
     */
    public static final GameRule<Boolean> GLOOMWISP_DROPS_EXPERIENCE = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(BetterNether.C.id("gloomwisp_drops_experience"));

    /**
     * Whether some of the obsidian in generated ruined portals is replaced with blue obsidian.
     * <p>
     * Defaults to on, matching the mod's existing behavior. Server owners who want vanilla-only
     * obsidian in ruined portals (e.g. for PBR resource pack compatibility) can turn this off with
     * {@code /gamerule betternether:generate_blue_ruined_portals false}, or override the default via a datapack.
     */
    public static final GameRule<Boolean> GENERATE_BLUE_RUINED_PORTALS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(BetterNether.C.id("generate_blue_ruined_portals"));

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
    public static final GameRule<Boolean> GROW_LARGE_WILLOWS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.UPDATES)
            .buildAndRegister(BetterNether.C.id("grow_large_willows"));

    /**
     * Whether a 2x2 of anchor tree saplings may grow the giant anchor tree.
     * <p>
     * The one rule here that ships <em>off</em>. The giant anchor tree is not a big tree so much as a
     * cavern-sized structure: it reaches from the ceiling it was planted on all the way down to the floor
     * beneath it, and it needs 30 blocks of clearance before it will grow at all. Four saplings placed
     * without knowing that is a lot of landscape to rearrange by accident, so this one is opt-in -
     * {@code /gamerule betternether:grow_large_anchor_trees true} - while the willow, which grows something a player
     * can stand next to, is opt-out.
     *
     * @see #GROW_LARGE_WILLOWS for why this gates the sapling and not worldgen
     */
    public static final GameRule<Boolean> GROW_LARGE_ANCHOR_TREES = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.UPDATES)
            .buildAndRegister(BetterNether.C.id("grow_large_anchor_trees"));

    public static void ensureStaticallyLoaded() {
    }
}
