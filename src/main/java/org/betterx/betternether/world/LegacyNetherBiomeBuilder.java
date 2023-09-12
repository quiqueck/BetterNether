package org.betterx.betternether.world;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;

import java.util.List;

@Deprecated(forRemoval = true)
public class LegacyNetherBiomeBuilder {
    public static boolean useLegacyGeneration = false;

    public static List<BCLBiome> getAllBnBiomes() {
        return BiomeAPI.getAllBiomes(BiomeAPI.BiomeType.NETHER);
    }
}
