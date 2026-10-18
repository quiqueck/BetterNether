package org.betterx.betternether.config;

import org.betterx.bclib.config.IgnoredConfigEntries;
import org.betterx.bclib.config.PathConfig;
import org.betterx.betternether.BetterNether;

public class Configs {
    static {
        // Blocks and items can no longer be disabled (quiqueck/BetterEnd#281), blocks.json and items.json are no
        // longer read.
        IgnoredConfigEntries.warnIfDisabled(BetterNether.MOD_ID, "blocks");
        IgnoredConfigEntries.warnIfDisabled(BetterNether.MOD_ID, "items");
        // recipes.json has not been read for a long time (use a datapack to remove recipes).
        IgnoredConfigEntries.warnIfDisabled(BetterNether.MOD_ID, "recipes", "recipes in this file");
    }

    public static final PathConfig MAIN = new PathConfig(BetterNether.MOD_ID, "main");
    public static final PathConfig GENERATOR = new PathConfig(BetterNether.MOD_ID, "generator");
    public static final PathConfig BIOMES = new PathConfig(BetterNether.MOD_ID, "biomes");
    public static final PathConfig MOBS = new PathConfig(BetterNether.MOD_ID, "mobs");

    public static void saveConfigs() {
        MAIN.saveChanges();
        GENERATOR.saveChanges();
        MOBS.saveChanges();
        BIOMES.saveChanges();
    }
}
