package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherTerrain;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.LAKES;
import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.RAW_GENERATION;

public class NetherTerrainPlaced {
    private static final ModCore C = BetterNether.C;


    public static final PlacedFeatureKey MAGMA_BLOBS = PlacedFeatureManager
            .createKey(C.id("magma_blob"))
            .setDecoration(LAKES);

    public static final PlacedConfiguredFeatureKey LAVA_PIT = PlacedFeatureManager.createKey(
            NetherTerrain.LAVA_PITS.key.location(),
            NetherTerrain.LAVA_PITS
    ).setDecoration(LAKES);

    public static final PlacedFeatureKey BASALT_OR_AIR = PlacedFeatureManager
            .createKey(C.id("basalt_or_air"))
            .setDecoration(LAKES);
    public static final PlacedFeatureKey MARK = PlacedFeatureManager
            .createKey(C.id("mark"))
            .setDecoration(Decoration.RAW_GENERATION);

    public static final PlacedFeatureKey EXTEND_BASALT = PlacedFeatureManager
            .createKey(C.id("extend_basalt"))
            .setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_PITS_SPARSE = PlacedFeatureManager.createKey(C.id(
            "lava_pits_sparse"), NetherTerrain.LAVA_PITS).setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_PITS_DENSE = PlacedFeatureManager.createKey(C.id(
            "lava_pits_dense"), NetherTerrain.LAVA_PITS).setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_SWAMP = PlacedFeatureManager.createKey(
            C.id("lava_swamp"),
            NetherTerrain.LAVA_PITS
    ).setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_TERRACE = PlacedFeatureManager.createKey(
            C.id("lava_terrace"),
            NetherTerrain.LAVA_PITS
    ).setDecoration(LAKES);

    public static final PlacedFeatureKey FLOODED_LAVA_PIT_SURFACE = PlacedFeatureManager
            .createKey(C.id("flooded_lava_pit_surface"))
            .setDecoration(LAKES);

    public static final PlacedFeatureKey FLOODED_LAVA_PIT = PlacedFeatureManager
            .createKey(C.id("flooded_lava_pit"))
            .setDecoration(LAKES);
    public static final PlacedFeatureKey REPLACE_SOUL_SANDSTONE = PlacedFeatureManager
            .createKey(C.id("replace_soul_sandstone"))
            .setDecoration(RAW_GENERATION);
}
