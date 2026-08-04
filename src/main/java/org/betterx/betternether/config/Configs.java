package org.betterx.betternether.config;

public class Configs {
    //public static final PathConfig MAIN = new PathConfig(BetterNether.C.modId, "main");

    public static final ClientConfig CLIENT = de.ambertation.wover.config.api.Configs.register(ClientConfig::new);
    public static final DefaultWorldConfig GAME_RULES = de.ambertation.wover.config.api.Configs.register(DefaultWorldConfig::new);
    public static final WorldConfig WORLD = de.ambertation.wover.config.api.Configs.register(WorldConfig::new);

    public static void saveConfigs() {
        //MAIN.saveChanges();
        de.ambertation.wover.config.api.Configs.saveConfigs();
    }
}
