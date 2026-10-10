package org.betterx.betternether;

import org.betterx.bclib.BCLib;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.commands.CommandRegistry;
import org.betterx.betternether.config.Config;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.registry.*;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.tab.BECreativeTabs;
import org.betterx.betternether.world.BNWorldGenerator;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.state.api.WorldConfig;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BetterNether implements ModInitializer {
    public static final ModCore C = ModCore.create("betternether");
    public static final ModCore VANILLA_HAMMERS = ModCore.create("vanilla-hammers");
    public static final ModCore VANILLA_EXCAVATORS = ModCore.create("vanillaexcavators");

    public static final Identifier VANILLA_HAMMERS_PACK = C.addDatapack(VANILLA_HAMMERS);
    public static final Identifier VANILLA_EXCAVATORS_PACK = C.addDatapack(VANILLA_EXCAVATORS);

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
        warnAboutLegacyConfigs();
        SoundsRegistry.ensureStaticallyLoaded();
        NetherGameRules.ensureStaticallyLoaded();
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
        CommandRegistry.register();
        Config.save();

        NetherTags.register();
        BNCriterion.register();
        NetherVegetation.setupBonemealFeatures();

        Configs.saveConfigs();
        WorldConfig.registerMod(C);
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

    /**
     * Older versions read {@code config/betternether/{blocks,items,mobs}.json} to skip registering
     * individual entries. That option was removed when configs moved to WorldWeaver (21.0.0): every
     * block, item and entity is always registered now, because recipes, tags, loot tables and
     * world generation reference them unconditionally. Leftover files are ignored, so tell the user
     * instead of silently doing nothing.
     */
    private static void warnAboutLegacyConfigs() {
        final Path folder = FabricLoader.getInstance().getConfigDir().resolve(C.modId);
        for (String name : new String[]{"blocks", "items", "mobs"}) {
            final Path file = folder.resolve(name + ".json");
            if (!Files.isRegularFile(file)) continue;
            final List<String> disabled = new ArrayList<>();
            try (Reader reader = Files.newBufferedReader(file)) {
                collectDisabled(JsonParser.parseReader(reader), disabled);
            } catch (Exception e) {
                // unreadable leftovers are just as ignored as readable ones
            }
            if (!disabled.isEmpty()) {
                C.log.warn(
                        "{} disables {} entries ({}), but BetterNether no longer supports disabling blocks, items or mobs. "
                                + "The file is ignored and everything is registered; delete it to silence this warning.",
                        file, disabled.size(), String.join(", ", disabled)
                );
            }
        }
    }

    private static void collectDisabled(JsonElement element, List<String> disabled) {
        if (element == null || !element.isJsonObject()) return;
        for (Map.Entry<String, JsonElement> e : ((JsonObject) element).entrySet()) {
            final JsonElement value = e.getValue();
            if (value.isJsonObject()) {
                collectDisabled(value, disabled);
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean() && !value.getAsBoolean()) {
                disabled.add(e.getKey().replaceFirst("\\[def: .*]$", ""));
            }
        }
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

