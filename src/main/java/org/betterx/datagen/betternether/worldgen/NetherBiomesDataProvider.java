package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeBuilder;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.NetherBiome;
import org.betterx.betternether.world.NetherBiomeBuilder;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.*;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import java.util.concurrent.CompletableFuture;

public class NetherBiomesDataProvider extends FabricTagProvider<Biome> {
    /**
     * Constructs a new {@link FabricTagProvider} with the default computed path.
     *
     * <p>Common implementations of this class are provided.
     *
     * @param output           the {@link FabricDataOutput} instance
     * @param registryKey
     * @param registriesFuture the backing registry for the tag type
     */
    public NetherBiomesDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, Registries.BIOME, registriesFuture);
    }

    private static class Config {
        private static final NetherBiomeConfig BIOME_GRAVEL_DESERT = new NetherGravelDesert.Config("Gravel Desert");
        private static final NetherBiomeConfig BIOME_NETHER_JUNGLE = new NetherJungle.Config("Nether Jungle");
        private static final NetherBiomeConfig BIOME_WART_FOREST = new NetherWartForest.Config("Wart Forest");
        private static final NetherBiomeConfig BIOME_GRASSLANDS = new NetherGrasslands.Config("Nether Grasslands");
        private static final NetherBiomeConfig BIOME_MUSHROOM_FOREST = new NetherMushroomForest.Config(
                "Nether Mushroom Forest");
        private static final NetherBiomeConfig BIOME_MUSHROOM_FOREST_EDGE = new NetherMushroomForestEdge.Config(
                "Nether Mushroom Forest Edge");
        private static final NetherBiomeConfig BIOME_WART_FOREST_EDGE = new NetherWartForestEdge.Config(
                "Wart Forest Edge");
        private static final NetherBiomeConfig BIOME_BONE_REEF = new NetherBoneReef.Config("Bone Reef");
        private static final NetherBiomeConfig BIOME_SULFURIC_BONE_REEF = new NetherSulfuricBoneReef.Config(
                "Sulfuric Bone Reef");
        private static final NetherBiomeConfig BIOME_POOR_GRASSLANDS = new NetherPoorGrasslands.Config(
                "Poor Nether Grasslands");
        private static final NetherBiomeConfig NETHER_SWAMPLAND = new NetherSwampland.Config("Nether Swampland");
        private static final NetherBiomeConfig NETHER_SWAMPLAND_TERRACES = new NetherSwamplandTerraces.Config(
                "Nether Swampland Terraces");
        private static final NetherBiomeConfig MAGMA_LAND = new NetherMagmaLand.Config("Magma Land");
        private static final NetherBiomeConfig SOUL_PLAIN = new NetherSoulPlain.Config("Soul Plain");
        private static final NetherBiomeConfig CRIMSON_GLOWING_WOODS = new CrimsonGlowingWoods.Config(
                "Crimson Glowing Woods");
        private static final NetherBiomeConfig OLD_WARPED_WOODS = new OldWarpedWoods.Config("Old Warped Woods");
        private static final NetherBiomeConfig CRIMSON_PINEWOOD = new CrimsonPinewood.Config("Crimson Pinewood");
        private static final NetherBiomeConfig OLD_FUNGIWOODS = new OldFungiwoods.Config("Old Fungiwoods");
        private static final NetherBiomeConfig FLOODED_DELTAS = new FloodedDeltas.Config("Flooded Deltas");
        private static final NetherBiomeConfig UPSIDE_DOWN_FOREST = new UpsideDownForest.Config("Upside Down Forest");
        private static final NetherBiomeConfig UPSIDE_DOWN_FOREST_CLEARED = new UpsideDownForestCleared.Config(
                "upside_down_forest_cleared");
        private static final NetherBiomeConfig OLD_SWAMPLAND = new OldSwampland.Config("Old Swampland");
    }

    private static final BCLBiome BIOME_GRAVEL_DESERT = registerNetherBiome(Config.BIOME_GRAVEL_DESERT);
    private static final BCLBiome BIOME_NETHER_JUNGLE = registerNetherBiome(Config.BIOME_NETHER_JUNGLE);
    private static final BCLBiome BIOME_WART_FOREST = registerNetherBiome(
            Config.BIOME_WART_FOREST,
            Config.BIOME_WART_FOREST_EDGE
    );
    private static final BCLBiome BIOME_GRASSLANDS = registerNetherBiome(Config.BIOME_GRASSLANDS);
    private static final BCLBiome BIOME_MUSHROOM_FOREST = registerNetherBiome(
            Config.BIOME_MUSHROOM_FOREST,
            Config.BIOME_MUSHROOM_FOREST_EDGE
    );
    private static final BCLBiome BIOME_MUSHROOM_FOREST_EDGE = BIOME_MUSHROOM_FOREST.getEdge();
    private static final BCLBiome BIOME_WART_FOREST_EDGE = BIOME_WART_FOREST.getEdge();
    private static final BCLBiome BIOME_BONE_REEF = registerNetherBiome(Config.BIOME_BONE_REEF);
    private static final BCLBiome BIOME_SULFURIC_BONE_REEF = registerSubBiome(
            Config.BIOME_SULFURIC_BONE_REEF,
            BIOME_BONE_REEF
    );
    private static final BCLBiome BIOME_POOR_GRASSLANDS = registerSubBiome(
            Config.BIOME_POOR_GRASSLANDS,
            BIOME_GRASSLANDS
    );
    private static final BCLBiome NETHER_SWAMPLAND = registerNetherBiome(Config.NETHER_SWAMPLAND);
    private static final BCLBiome NETHER_SWAMPLAND_TERRACES = registerSubBiome(
            Config.NETHER_SWAMPLAND_TERRACES,
            NETHER_SWAMPLAND
    );
    private static final BCLBiome MAGMA_LAND = registerNetherBiome(Config.MAGMA_LAND);
    private static final BCLBiome SOUL_PLAIN = registerSubBiome(Config.SOUL_PLAIN, BIOME_WART_FOREST);
    private static final BCLBiome CRIMSON_GLOWING_WOODS = registerSubBiome(
            Config.CRIMSON_GLOWING_WOODS,
            BiomeAPI.CRIMSON_FOREST_BIOME
    );
    private static final BCLBiome OLD_WARPED_WOODS = registerSubBiome(
            Config.OLD_WARPED_WOODS,
            BiomeAPI.WARPED_FOREST_BIOME
    );
    private static final BCLBiome CRIMSON_PINEWOOD = registerSubBiome(
            Config.CRIMSON_PINEWOOD,
            BiomeAPI.CRIMSON_FOREST_BIOME
    );
    private static final BCLBiome OLD_FUNGIWOODS = registerSubBiome(Config.OLD_FUNGIWOODS, BIOME_MUSHROOM_FOREST);
    private static final BCLBiome FLOODED_DELTAS = registerSubBiome(
            Config.FLOODED_DELTAS,
            BiomeAPI.BASALT_DELTAS_BIOME
    );
    private static final BCLBiome UPSIDE_DOWN_FOREST = registerNetherBiome(Config.UPSIDE_DOWN_FOREST);
    private static final BCLBiome UPSIDE_DOWN_FOREST_CLEARED = registerNetherBiome(Config.UPSIDE_DOWN_FOREST_CLEARED);
    private static final BCLBiome OLD_SWAMPLAND = registerSubBiome(Config.OLD_SWAMPLAND, NETHER_SWAMPLAND);

    private static NetherBiome registerNetherBiome(NetherBiomeConfig config) {
        return NetherBiomeBuilder.create(config);
    }

    private static NetherBiome registerNetherBiome(NetherBiomeConfig config, NetherBiomeConfig edgeConfig) {
        final NetherBiome edge = NetherBiomeBuilder.create(edgeConfig);
        final NetherBiome biome = NetherBiomeBuilder.create(config, edge);

        return biome;
    }

    private static NetherBiome registerSubBiome(NetherBiomeConfig config, BCLBiome mainBiome) {
        return NetherBiomeBuilder.createSubBiome(config, mainBiome);
    }

    public static void bootstrap(BootstapContext<Biome> ctx) {
        BCLBiomeBuilder.registerUnbound(ctx);
    }

    public static void ensureStaticallyLoaded() {
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        TagManager.BIOMES.forEachTag((tag, locs, tags) -> {
            final FabricTagProvider<Biome>.FabricTagBuilder builder = getOrCreateTagBuilder(tag);
            boolean modTag = tag.location().getNamespace().equals(BetterNether.MOD_ID);
            locs.stream().filter(l -> modTag || l.getNamespace().equals(BetterNether.MOD_ID)).forEach(builder::add);
            tags.stream()
                .filter(t -> modTag || t.location().getNamespace().equals(BetterNether.MOD_ID))
                .forEach(builder::addTag);
        });
    }
}
