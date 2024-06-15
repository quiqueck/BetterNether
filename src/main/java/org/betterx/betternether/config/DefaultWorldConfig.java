package org.betterx.betternether.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.config.api.MainConfig;

public class DefaultWorldConfig extends ConfigFile {
    public final static Group DAMAGE_GROUP = new Group(BetterNether.C.namespace, "damage", 500);
    public final BooleanValue eggPlantMobDamage = new BooleanValue(
            "egg_plant",
            "mob_damage",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public final BooleanValue eggPlantPlayerDamage = new BooleanValue(
            "egg_plant",
            "player_damage",
            true
    ).setGroup(DAMAGE_GROUP);

    public DefaultWorldConfig() {
        super(BetterNether.C, "default_game_rules");
    }
}
