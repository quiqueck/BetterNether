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
     * {@code /gamerule generateBlueRuinedPortals false}, or override the default via a datapack.
     */
    public static final GameRule<Boolean> GENERATE_BLUE_RUINED_PORTALS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(BetterNether.C.id("generate_blue_ruined_portals"));

    public static void ensureStaticallyLoaded() {
    }
}
