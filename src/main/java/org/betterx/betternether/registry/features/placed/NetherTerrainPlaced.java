package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherTerrain;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.LAKES;
import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.RAW_GENERATION;

public class NetherTerrainPlaced {
    private static final ModCore C = BetterNether.C;
    public static final PlacedConfiguredFeatureKey LAVA_PIT = PlacedFeatureManager
            .createKey(NetherTerrain.LAVA_PITS.key.identifier(), NetherTerrain.LAVA_PITS)
            .setDecoration(LAKES);
    public static final PlacedFeatureKey MAGMA_BLOBS = PlacedFeatureManager
            .createKey(C.id("magma_blob"))
            .setDecoration(LAKES);
    public static final PlacedFeatureKey BASALT_OR_AIR = PlacedFeatureManager
            .createKey(C.id("basalt_or_air"))
            .setDecoration(LAKES);
    public static final PlacedFeatureKey MARK = PlacedFeatureManager
            .createKey(C.id("mark"))
            .setDecoration(Decoration.RAW_GENERATION);

    public static final PlacedFeatureKey EXTEND_BASALT = PlacedFeatureManager
            .createKey(C.id("extend_basalt"))
            .setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_PITS_SPARSE = PlacedFeatureManager
            .createKey(C.id("lava_pits_sparse"), NetherTerrain.LAVA_PITS)
            .setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_PITS_DENSE = PlacedFeatureManager
            .createKey(C.id("lava_pits_dense"), NetherTerrain.LAVA_PITS)
            .setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_SWAMP = PlacedFeatureManager
            .createKey(C.id("lava_swamp"), NetherTerrain.LAVA_PITS)
            .setDecoration(LAKES);
    public static final PlacedConfiguredFeatureKey LAVA_TERRACE = PlacedFeatureManager
            .createKey(C.id("lava_terrace"), NetherTerrain.LAVA_PITS)
            .setDecoration(LAKES);

    /**
     * Sparse blobs of molten gloomsculk over the biome floor. The fissures only make sense where the rock
     * is genuinely hot, so this is deliberately thin - {@link #MOLTEN_GLOOMSCULK_NEAR_LAVA} is what puts
     * the bulk of it around the lava pits.
     */
    public static final PlacedFeatureKey MOLTEN_GLOOMSCULK = PlacedFeatureManager
            .createKey(C.id("molten_gloomsculk"))
            .setDecoration(LAKES);
    /**
     * Molten gloomsculk hugging the rim of a lava pit. Runs after {@link #LAVA_PITS_SPARSE} in the same
     * decoration step, so the pits it keys off already exist.
     */
    public static final PlacedFeatureKey MOLTEN_GLOOMSCULK_NEAR_LAVA = PlacedFeatureManager
            .createKey(C.id("molten_gloomsculk_near_lava"))
            .setDecoration(LAKES);

    /** Geodes worked into the biome's floor and ceiling, and the crystals that stud both. */
    public static final PlacedFeatureKey GLOOMSCULK_GEODE_FLOOR = PlacedFeatureManager
            .createKey(C.id("gloomsculk_geode_floor"))
            .setDecoration(LAKES);
    public static final PlacedFeatureKey GLOOMSCULK_GEODE_CEILING = PlacedFeatureManager
            .createKey(C.id("gloomsculk_geode_ceiling"))
            .setDecoration(LAKES);
    /** Geodes sitting on top of the ground rather than set into it. */
    public static final PlacedFeatureKey GLOOMSCULK_GEODE_ON_FLOOR = PlacedFeatureManager
            .createKey(C.id("gloomsculk_geode_on_floor"))
            .setDecoration(LAKES);

    public static final PlacedFeatureKey GLOOMSCULK_CRYSTAL_FLOOR = PlacedFeatureManager
            .createKey(C.id("gloomsculk_crystal_floor"))
            .setDecoration(Decoration.VEGETAL_DECORATION);
    public static final PlacedFeatureKey GLOOMSCULK_CRYSTAL_CEILING = PlacedFeatureManager
            .createKey(C.id("gloomsculk_crystal_ceiling"))
            .setDecoration(Decoration.VEGETAL_DECORATION);

    /**
     * Sculk vein over the gloomwood's floor, walls and ceiling - three placements of the one configured
     * feature, differing only in where they look for an empty position to start from.
     * <p>
     * Split rather than left to the feature's own {@code search_range} to find: the multiface feature
     * walks outwards from wherever it is dropped, so a floor-only set of origins leaves the ceiling of a
     * tall cavern untouched no matter how far it is allowed to search.
     */
    public static final PlacedConfiguredFeatureKey SCULK_VEIN_FLOOR = PlacedFeatureManager
            .createKey(C.id("sculk_vein_floor"), NetherTerrain.SCULK_VEIN)
            .setDecoration(Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey SCULK_VEIN_WALL = PlacedFeatureManager
            .createKey(C.id("sculk_vein_wall"), NetherTerrain.SCULK_VEIN)
            .setDecoration(Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey SCULK_VEIN_CEILING = PlacedFeatureManager
            .createKey(C.id("sculk_vein_ceiling"), NetherTerrain.SCULK_VEIN)
            .setDecoration(Decoration.VEGETAL_DECORATION);

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
