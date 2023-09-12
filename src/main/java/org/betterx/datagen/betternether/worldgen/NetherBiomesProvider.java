package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.registry.NetherBiomes;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.*;
import org.betterx.betternether.world.biomes.util.NetherBiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;

public class NetherBiomesProvider extends WoverBiomeProvider {
    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public NetherBiomesProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        register(context, NetherBiomes.BONE_REEF, new BoneReef());
        register(context, NetherBiomes.CRIMSON_GLOWING_WOODS, new CrimsonGlowingWoods());
        register(context, NetherBiomes.CRIMSON_PINEWOOD, new CrimsonPinewood());
        register(context, NetherBiomes.FLOODED_DELTAS, new FloodedDeltas());
        register(context, NetherBiomes.GRAVEL_DESERT, new GravelDesert());
        register(context, NetherBiomes.MAGMA_LAND, new MagmaLand());
        register(context, NetherBiomes.NETHER_GRASSLANDS, new NetherGrasslands());
        register(context, NetherBiomes.NETHER_JUNGLE, new NetherJungle());
        register(context, NetherBiomes.NETHER_MUSHROOM_FORREST, new NetherMushroomForest());
        register(context, NetherBiomes.NETHER_MUSHROOM_FORREST_EDGE, new NetherMushroomForestEdge());
        register(context, NetherBiomes.NETHER_SWAMPLAND, new NetherSwampland());
        register(context, NetherBiomes.NETHER_SWAMPLAND_TERRACES, new NetherSwamplandTerraces());
        register(context, NetherBiomes.OLD_FUNGIWOODS, new OldFungiwoods());
        register(context, NetherBiomes.OLD_SWAMPLAND, new OldSwampland());
        register(context, NetherBiomes.OLD_WARPED_WOODS, new OldWarpedWoods());
        register(context, NetherBiomes.POOR_NETHER_GRASSLANDS, new PoorNetherGrasslands());
        register(context, NetherBiomes.SOUL_PLAIN, new SoulPlain());
        register(context, NetherBiomes.SULFURIC_BONE_REEF, new SulfuricBoneReef());
        register(context, NetherBiomes.UPSIDE_DOWN_FOREST, new UpsideDownForest());
        register(context, NetherBiomes.UPSIDE_DOWN_FOREST_CLEARED, new UpsideDownForestCleared());
        register(context, NetherBiomes.WART_FOREST, new WartForest());
        register(context, NetherBiomes.WART_FOREST_EDGE, new WartForestEdge());
    }

    private <C extends NetherBiomeConfig> void register(BiomeBootstrapContext context, NetherBiomeKey<C> key, C cfg) {
        key.bootstrap(context, cfg).register();
    }
}
