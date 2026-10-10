package org.betterx.betternether.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betternether.BetterNether;
import de.ambertation.wover.config.api.MainConfig;

public class ClientConfig extends ConfigFile {
    public final BooleanValue lavafallParticles = new BooleanValue(
            de.ambertation.wover.config.api.MainConfig.PERFORMANCE_GROUP.title(),
            "lavafall_particles",
            true
    ).setGroup(de.ambertation.wover.config.api.MainConfig.PERFORMANCE_GROUP);

    public final BooleanValue weepingParticles = new BooleanValue(
            de.ambertation.wover.config.api.MainConfig.PERFORMANCE_GROUP.title(),
            "weeping_obsidian_particles",
            true
    ).setGroup(de.ambertation.wover.config.api.MainConfig.PERFORMANCE_GROUP);

    // No fog renderer reads this value anymore (BN's own fog mixin was dropped in favour of BCLib's in 2022,
    // and BCLib's custom fog renderer did not survive the 1.21.2+ fog rewrite). Keep the key so existing
    // config files stay valid, but don't offer a slider that does nothing (#175, #131).
    public final FloatValue fogDensity = new FloatValue(
            MainConfig.COSMETIC_GROUP.title(),
            "fog_density",
            0.75f
    ).setGroup(de.ambertation.wover.config.api.MainConfig.COSMETIC_GROUP)
     .min(0.0f)
     .max(1.0f)
     .hideInUI();

    public final BooleanValue thinArmor = new BooleanValue(
            de.ambertation.wover.config.api.MainConfig.COSMETIC_GROUP.title(),
            "thin_armor",
            true
    ).setGroup(de.ambertation.wover.config.api.MainConfig.COSMETIC_GROUP);

    public ClientConfig() {
        super(BetterNether.C, "client");
    }
}
