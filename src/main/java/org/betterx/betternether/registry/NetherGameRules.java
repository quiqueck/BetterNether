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

    /**
     * Whether {@code custom_spawn_rules}' sky-light test is corrected in dimensions that have no sky
     * light, so mob spawners using those rules can work there at all.
     * <p>
     * Since 1.20.5 {@code SpawnData.CustomSpawnRules.isValidPosition} compares the authored
     * {@code sky_light_limit} against {@code getEffectiveSkyBrightness}, which is
     * {@code getBrightness(SKY, pos) - getSkyDarken()}. The Nether's dimension type sets
     * {@code gameplay/sky_light_level} to 4, so {@code skyDarken} is 11 and the expression evaluates to
     * <b>-11</b> everywhere. Every limit is clamped to {@code 0..15} by
     * {@code CustomSpawnRules.checkLightBoundaries}, so no authored range can contain it: the check
     * fails at every position and the spawner sits at {@code Delay: 0} forever, ticking and spawning
     * nothing. Up to 1.20.4 the line read {@code getBrightness(LightLayer.SKY, pos)} - 0 in the Nether,
     * and inside {@code 0..15}.
     * <p>
     * Defaults to on, because with it off every city guard spawner in the mod is inert. It exists as a
     * rule because the correction touches a vanilla method: a server owner who would rather have stock
     * behaviour, or who is running alongside another mod that addresses the same defect, can turn it
     * off with {@code /gamerule betternether:fix_sky_light_spawn_rules false} instead of removing the
     * mixin from the jar.
     * <p>
     * Filed under MOBS beside the other spawning rules. See
     * {@code org.betterx.betternether.mixin.common.CustomSpawnRulesMixin} for the gating.
     * <p>
     * Tracked upstream as <b>MC-307449</b>; this rule and the mixin behind it should be removed once
     * that is fixed in a version the mod targets.
     */
    public static final GameRule<Boolean> FIX_SKY_LIGHT_SPAWN_RULES = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(BetterNether.C.id("fix_sky_light_spawn_rules"));

    public static void ensureStaticallyLoaded() {
    }
}
