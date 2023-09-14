package org.betterx.betternether.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.entrypoint.WoverCore;

public class WorldConfig extends ConfigFile {
    public final static Group STRUCTURE_GROUP = new Group(WoverCore.C.namespace, "structure", 0);

    public final BooleanValue addNetherCityToOverworld = new BooleanValue(
            STRUCTURE_GROUP.title(),
            "city_in_overworld",
            true
    ).setGroup(STRUCTURE_GROUP);
    
    public WorldConfig() {
        super(BetterNether.C, "world");
    }
}
