package org.betterx.betternether;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.dataexchange.DataExchangeAPI;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.commands.CommandRegistry;
import org.betterx.betternether.config.Config;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.loot.BNLoot;
import org.betterx.betternether.registry.*;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.tab.BECreativeTabs;
import org.betterx.betternether.world.BNWorldGenerator;
import org.betterx.worlds.together.world.WorldConfig;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.ModInitializer;

public class BetterNether implements ModInitializer {
    public static final ModCore C = ModCore.create("betternether");
    public static final ModCore VANILLA_HAMMERS = ModCore.create("vanilla-hammers");
    public static final ModCore VANILLA_EXCAVATORS = ModCore.create("vanillaexcavators");

    public static final ResourceLocation VANILLA_HAMMERS_PACK = C.addDatapack(VANILLA_HAMMERS);
    public static final ResourceLocation VANILLA_EXCAVATORS_PACK = C.addDatapack(VANILLA_EXCAVATORS);

    @Deprecated(forRemoval = true)
    public static final org.betterx.worlds.together.util.Logger LEGACY_LOGGER
            = new org.betterx.worlds.together.util.Logger(C.modId);
    private static boolean lavafallParticles = true;
    private static float fogStart = 0.05F;
    private static float fogEnd = 0.5F;


    private void onDatagen() {
    }

    @Override
    public void onInitialize() {
        C.log.info("=^..^=    BetterNether for 1.21    =^..^=");
        //MigrationProfile.fixCustomFolder(new File("/Users/frank/Entwicklung/BetterNether/src/main/resources/data/betternether/structures/lava"));

        initOptions();
        SoundsRegistry.ensureStaticallyLoaded();
        NetherEnchantments.ensureStaticallyLoaded();
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
        Config.save();

        NetherTags.register();
        BNLoot.register();
        BNCriterion.register();
        NetherVegetation.setupBonemealFeatures();

        Configs.saveConfigs();
        WorldConfig.registerModCache(C.modId);
        DataExchangeAPI.registerMod(C.modId);
        Patcher.register();
        BECreativeTabs.register();

        if (BCLib.isDatagen()) {
            onDatagen();
        }
    }

    private void initOptions() {
        lavafallParticles = Configs.CLIENT.lavafallParticles.get();
        float density = Configs.CLIENT.fogDensity.get();
        changeFogDensity(density);
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

}

