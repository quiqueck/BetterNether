package org.betterx.betternether.registry;

import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.NetherBiome;
import org.betterx.betternether.world.biomes.*;
import org.betterx.betternether.world.biomes.providers.NetherGrasslandsNumericProvider;
import org.betterx.betternether.world.biomes.providers.NetherMushroomForestEdgeNumericProvider;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.betternether.world.biomes.util.NetherBiomeKey;
import org.betterx.wover.biome.api.data.BiomeCodecRegistry;
import org.betterx.wover.surface.api.noise.NumericProviderRegistry;

import net.minecraft.core.Registry;

public class NetherBiomes {
    public static final NetherBiomeKey<BoneReef> BONE_REEF
            = NetherBiomeBuilder.createKey("Bone Reef");
    public static final NetherBiomeKey<CrimsonGlowingWoods> CRIMSON_GLOWING_WOODS
            = NetherBiomeBuilder.createKey("Crimson Glowing Woods");
    public static final NetherBiomeKey<CrimsonPinewood> CRIMSON_PINEWOOD
            = NetherBiomeBuilder.createKey("Crimson Pinewood");

    public static final NetherBiomeKey<FloodedDeltas> FLOODED_DELTAS
            = NetherBiomeBuilder.createKey("Flooded Deltas");

    public static final NetherBiomeKey<GravelDesert> GRAVEL_DESERT
            = NetherBiomeBuilder.createKey("Gravel Desert");

    public static final NetherBiomeKey<MagmaLand> MAGMA_LAND
            = NetherBiomeBuilder.createKey("Magma Land");

    public static final NetherBiomeKey<NetherGrasslands> NETHER_GRASSLANDS
            = NetherBiomeBuilder.createKey("Nether Grasslands");

    public static final NetherBiomeKey<NetherJungle> NETHER_JUNGLE
            = NetherBiomeBuilder.createKey("Nether Jungle");

    public static final NetherBiomeKey<NetherMushroomForest> NETHER_MUSHROOM_FORREST
            = NetherBiomeBuilder.createKey("Nether Mushroom Forest");

    public static final NetherBiomeKey<NetherMushroomForestEdge> NETHER_MUSHROOM_FORREST_EDGE
            = NetherBiomeBuilder.createKey("Nether Mushroom Forest Edge");

    public static final NetherBiomeKey<NetherSwampland> NETHER_SWAMPLAND
            = NetherBiomeBuilder.createKey("Nether Swampland");

    public static final NetherBiomeKey<NetherSwamplandTerraces> NETHER_SWAMPLAND_TERRACES
            = NetherBiomeBuilder.createKey("Nether Swampland Terraces");

    public static final NetherBiomeKey<OldFungiwoods> OLD_FUNGIWOODS
            = NetherBiomeBuilder.createKey("Old Fungiwoods");

    public static final NetherBiomeKey<OldSwampland> OLD_SWAMPLAND
            = NetherBiomeBuilder.createKey("Old Swampland");

    public static final NetherBiomeKey<OldWarpedWoods> OLD_WARPED_WOODS
            = NetherBiomeBuilder.createKey("Old Warped Woods");

    public static final NetherBiomeKey<PoorNetherGrasslands> POOR_NETHER_GRASSLANDS
            = NetherBiomeBuilder.createKey("Poor Nether Grasslands");

    public static final NetherBiomeKey<SoulPlain> SOUL_PLAIN
            = NetherBiomeBuilder.createKey("Soul Plain");

    public static final NetherBiomeKey<SulfuricBoneReef> SULFURIC_BONE_REEF
            = NetherBiomeBuilder.createKey("Sulfuric Bone Reef");

    public static final NetherBiomeKey<UpsideDownForest> UPSIDE_DOWN_FOREST
            = NetherBiomeBuilder.createKey("Upside Down Forest");

    public static final NetherBiomeKey<UpsideDownForestCleared> UPSIDE_DOWN_FOREST_CLEARED
            = NetherBiomeBuilder.createKey("upside_down_forest_cleared");

    public static final NetherBiomeKey<WartForest> WART_FOREST
            = NetherBiomeBuilder.createKey("Wart Forest");

    public static final NetherBiomeKey<WartForestEdge> WART_FOREST_EDGE
            = NetherBiomeBuilder.createKey("Wart Forest Edge");


    public static void register() {
        BiomeCodecRegistry.register(BetterNether.C.id("biome"), NetherBiome.KEY_CODEC);

        BiomeAPI.registerNetherBiomeModification((biomeID, biome) -> {
            if (!biomeID.getNamespace().equals(BetterNether.C.modId)) {
                NetherEntities.modifyNonBNBiome(biomeID, biome);
                NetherFeatures.modifyNonBNBiome(biomeID, biome);
            }
        });
        BiomeAPI.onFinishingNetherBiomeTags((biomeID, biome) -> {
            if (!biomeID.getNamespace().equals(BetterNether.C.modId)) {
                NetherStructures.addNonBNBiomeTags(biomeID, biome);
            }
        });
        registerNumericProviders();
    }

    private static void registerNumericProviders() {
        Registry.register(
                NumericProviderRegistry.NUMERIC_PROVIDER,
                BetterNether.C.id("nether_grasslands"),
                NetherGrasslandsNumericProvider.CODEC
        );

        Registry.register(
                NumericProviderRegistry.NUMERIC_PROVIDER,
                BetterNether.C.id("nether_mushroom_forrest_edge"),
                NetherMushroomForestEdgeNumericProvider.CODEC
        );
    }
}
