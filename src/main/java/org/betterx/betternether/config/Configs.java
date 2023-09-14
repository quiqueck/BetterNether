package org.betterx.betternether.config;

import org.betterx.bclib.config.PathConfig;
import org.betterx.betternether.BetterNether;

public class Configs {
    public static final PathConfig MAIN = new PathConfig(BetterNether.C.modId, "main");
    public static final PathConfig GENERATOR = new PathConfig(BetterNether.C.modId, "generator");
    public static final PathConfig BLOCKS = new PathConfig(BetterNether.C.modId, "blocks");
    public static final PathConfig ITEMS = new PathConfig(BetterNether.C.modId, "items");
    public static final PathConfig BIOMES = new PathConfig(BetterNether.C.modId, "biomes");
    public static final PathConfig MOBS = new PathConfig(BetterNether.C.modId, "mobs");
    public static final PathConfig RECIPES = new PathConfig(BetterNether.C.modId, "recipes");

    public static final WorldConfig WORLD = org.betterx.wover.config.api.Configs.register(WorldConfig::new);

    public static void saveConfigs() {
        MAIN.saveChanges();
        GENERATOR.saveChanges();
        BLOCKS.saveChanges();
        ITEMS.saveChanges();
        MOBS.saveChanges();
        RECIPES.saveChanges();
        BIOMES.saveChanges();

        org.betterx.wover.config.api.Configs.saveConfigs();
    }
}
