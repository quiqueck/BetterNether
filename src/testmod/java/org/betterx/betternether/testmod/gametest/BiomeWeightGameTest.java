package org.betterx.betternether.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether Biomes are picked slightly more often (x1.25) than vanilla Biomes and Biomes of other mods, while
 * vanilla and other mods share WoVer's default weight. Vanilla used to get 0.01, which made it vanish as soon
 * as another mod added Biomes to the same dimension.
 */
public class BiomeWeightGameTest {
    private static final float FACTOR = 1.25f;
    private static final float EPSILON = 1e-5f;
    private static final int SAMPLES = 40_000;
    private static final long SEED = 263L;

    private static final ResourceKey<Biome> OURS = key("betternether", "nether_grasslands");
    private static final ResourceKey<Biome> VANILLA = Biomes.NETHER_WASTES;
    // stand-in for a Biome of another mod: a bound Biome without any WoVer data
    private static final ResourceKey<Biome> FOREIGN = Biomes.PLAINS;

    @GameTest
    public void ourBiomesWeighSlightlyMoreThanVanillaAndForeign(GameTestHelper helper) {
        final Registry<BiomeData> data = dataRegistry(helper);
        final float foreign = BiomeData.tempOf(FOREIGN).genChance();
        final float vanilla = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, VANILLA).genChance();

        if (Math.abs(vanilla - foreign) > EPSILON) {
            helper.fail(Component.literal(VANILLA.location() + " has genChance " + vanilla + ", Biomes of other mods " + foreign));
            return;
        }
        for (String id : List.of("nether_grasslands", "nether_jungle", "wart_forest", "gravel_desert", "magma_land", "bone_reef")) {
            final BiomeData d = BiomeDataRegistryImpl.getFromRegistryOrTemp(data, key("betternether", id));
            if (d.isTemp() || Math.abs(d.genChance() - FACTOR * foreign) > EPSILON) {
                helper.fail(Component.literal("betternether:" + id + " has genChance " + d.genChance() + ", expected " + FACTOR * foreign));
                return;
            }
        }
        helper.succeed();
    }

    @GameTest
    public void pickerPrefersOurBiomesSlightly(GameTestHelper helper) {
        final Registry<BiomeData> data = dataRegistry(helper);
        final HolderLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final WoverBiomePicker picker = new WoverBiomePicker(biomes, VANILLA);
        picker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(data, OURS));
        picker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(data, VANILLA));
        picker.addBiome(BiomeData.tempOf(FOREIGN));
        picker.rebuild();

        int ours = 0, vanilla = 0, foreign = 0;
        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(SEED));
        for (int i = 0; i < SAMPLES; i++) {
            final ResourceKey<Biome> picked = picker.getBiome(random).biomeData.biomeKey;
            if (picked.equals(OURS)) ours++;
            else if (picked.equals(VANILLA)) vanilla++;
            else if (picked.equals(FOREIGN)) foreign++;
        }

        final String counts = OURS.location() + "=" + ours + ", " + VANILLA.location() + "=" + vanilla
                + ", " + FOREIGN.location() + "=" + foreign;
        // expected 1.25 : 1 : 1 (38.5% : 30.8% : 30.8%)
        if (Math.abs(vanilla - foreign) > SAMPLES * 0.03) {
            helper.fail(Component.literal("Vanilla and foreign Biomes should be picked equally often: " + counts));
            return;
        }
        final double ratio = ours / (double) Math.max(1, (vanilla + foreign) / 2);
        if (ratio < 1.15 || ratio > 1.35) {
            helper.fail(Component.literal("Our Biome should be picked ~1.25x as often as the others, got " + ratio + ": " + counts));
            return;
        }
        helper.succeed();
    }

    private static Registry<BiomeData> dataRegistry(GameTestHelper helper) {
        return helper.getLevel().registryAccess().lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
    }

    private static ResourceKey<Biome> key(String ns, String path) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(ns, path));
    }
}
