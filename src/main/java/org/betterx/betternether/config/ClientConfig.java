package org.betterx.betternether.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.config.api.MainConfig;

public class ClientConfig extends ConfigFile {
    public final BooleanValue lavafallParticles = new BooleanValue(
            org.betterx.wover.config.api.MainConfig.PERFORMANCE_GROUP.title(),
            "lavafall_particles",
            true
    ).setGroup(org.betterx.wover.config.api.MainConfig.PERFORMANCE_GROUP);

    public final BooleanValue weepingParticles = new BooleanValue(
            org.betterx.wover.config.api.MainConfig.PERFORMANCE_GROUP.title(),
            "weeping_obsidian_particles",
            true
    ).setGroup(org.betterx.wover.config.api.MainConfig.PERFORMANCE_GROUP);

    public final FloatValue fogDensity = new FloatValue(
            MainConfig.COSMETIC_GROUP.title(),
            "fog_density",
            0.75f
    ).setGroup(org.betterx.wover.config.api.MainConfig.COSMETIC_GROUP)
     .min(0.0f)
     .max(1.0f);

    public final BooleanValue thinArmor = new BooleanValue(
            org.betterx.wover.config.api.MainConfig.COSMETIC_GROUP.title(),
            "thin_armor",
            true
    ).setGroup(org.betterx.wover.config.api.MainConfig.COSMETIC_GROUP);

    public ClientConfig() {
        super(BetterNether.C, "client");
    }
}
