package org.betterx.betternether.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.config.api.MainConfig;

public class WorldConfig extends ConfigFile {
    public final BooleanValue addNetherCityToOverworld = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "city_in_overworld",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public WorldConfig() {
        super(BetterNether.C, "world");
    }
}
