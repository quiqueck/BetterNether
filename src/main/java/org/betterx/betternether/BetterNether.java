package org.betterx.betternether;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.dataexchange.DataExchangeAPI;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.commands.CommandRegistry;
import org.betterx.betternether.config.Config;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.enchantments.ObsidianBreaker;
import org.betterx.betternether.loot.BNLoot;
import org.betterx.betternether.recipes.IntegrationRecipes;
import org.betterx.betternether.registry.*;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.tab.CreativeTabs;
import org.betterx.betternether.world.BNWorldGenerator;
import org.betterx.worlds.together.world.WorldConfig;
import org.betterx.wover.core.api.Logger;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.ModInitializer;

public class BetterNether implements ModInitializer {
    public static final String MOD_ID = "betternether";
    public static final ModCore C = ModCore.create(MOD_ID);
    public static final Logger LOGGER = C.log;
    public static final org.betterx.worlds.together.util.Logger LEGACY_LOGGER = new org.betterx.worlds.together.util.Logger(
            MOD_ID);
    private static boolean thinArmor = true;
    private static boolean lavafallParticles = true;
    private static float fogStart = 0.05F;
    private static float fogEnd = 0.5F;


    private void onDatagen() {
    }

    @Override
    public void onInitialize() {
        C.log.info("=^..^=    BetterNether for 1.20    =^..^=");
        //MigrationProfile.fixCustomFolder(new File("/Users/frank/Entwicklung/BetterNether/src/main/resources/data/betternether/structures/lava"));

        initOptions();
        SoundsRegistry.ensureStaticallyLoaded();
        NetherBlocks.register();
        BlockEntitiesRegistry.register();
        NetherItems.register();
        NetherTemplates.ensureStaticallyLoaded();
        NetherEntities.register();
        BNWorldGenerator.onModInit();
        NetherPoiTypes.register();
        NetherFeatures.register();
        NetherStructures.register();
        NetherBiomes.register();
        BrewingRegistry.register();
        CommandRegistry.register();
        ObsidianBreaker.register();
        Config.save();

        IntegrationRecipes.register();
        NetherTags.register();
        BNLoot.register();
        BNCriterion.register();
        NetherVegetation.setupBonemealFeatures();

        Configs.saveConfigs();
        WorldConfig.registerModCache(MOD_ID);
        DataExchangeAPI.registerMod(BetterNether.MOD_ID);
        Patcher.register();
        CreativeTabs.register();

        if (BCLib.isDatagen()) {
            onDatagen();
        }
    }

    private void initOptions() {
        thinArmor = Configs.MAIN.getBoolean("improvement", "smaller_armor_offset", true);
        lavafallParticles = Configs.MAIN.getBoolean("improvement", "lavafall_particles", true);
        float density = Configs.MAIN.getFloat("improvement", "fog_density[vanilla: 1.0]", 0.75F);
        changeFogDensity(density);
    }

    public static boolean hasThinArmor() {
        return thinArmor;
    }

    public static void setThinArmor(boolean value) {
        thinArmor = value;
    }

    public static boolean hasLavafallParticles() {
        return lavafallParticles;
    }

    public static void changeFogDensity(float density) {
        fogStart = -0.45F * density + 0.5F;
        fogEnd = -0.5F * density + 1;
    }

    public static float getFogStart() {
        return fogStart;
    }

    public static float getFogEnd() {
        return fogEnd;
    }

    public static ResourceLocation makeID(String path) {
        return C.id(path);
    }
}

