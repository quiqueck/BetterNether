package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.lake.MegaLavaLakeStructure;
import org.betterx.betternether.world.structures.templates.*;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class NetherStructures {
    // Nether City
    public static final StructureKey.Simple<CityStructure> CITY_STRUCTURE = StructureManager
            .structure(BetterNether.C.id("nether_city"), CityStructure::new)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<Pyramids> PYRAMIDS = StructureManager
            .structure(BetterNether.C.id("pyramid"), Pyramids::new, Pyramids.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<GhastHive> GHAST_HIVE = StructureManager
            .structure(BetterNether.C.id("ghast_hive"), GhastHive::new, GhastHive.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<SpawnAltarLadder> SPAWN_ALTAR_LADDER = StructureManager
            .structure(BetterNether.C.id("spawn_altar_ladder"), SpawnAltarLadder::new, SpawnAltarLadder.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<RespawnPoints> RESPAWN_POINTS = StructureManager
            .structure(BetterNether.C.id("respawn_points"), RespawnPoints::new, RespawnPoints.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<Pillars> PILLARS = StructureManager
            .structure(BetterNether.C.id("pillars"), Pillars::new, Pillars.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<Gardens> GARDENS = StructureManager
            .structure(BetterNether.C.id("gardens"), Gardens::new, Gardens.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<Portals> PORTALS = StructureManager
            .structure(BetterNether.C.id("portals"), Portals::new, Portals.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<Altars> ALTARS = StructureManager
            .structure(BetterNether.C.id("altars"), Altars::new, Altars.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
            .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS);

    public static final StructureKey.Simple<JungleTemples> JUNGLE_TEMPLES = StructureManager
            .structure(BetterNether.C.id("jungle_temples"), JungleTemples::new, JungleTemples.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    /**
     * The gloomwood's mega lava lake.
     * <p>
     * SURFACE_STRUCTURES rather than the LAKES step BetterEnd's megalake uses: the gloomwood puts its own
     * lava pits and its molten gloomsculk in LAKES, and structures run ahead of the features of the same
     * step, so a lake generated there would have pits cut into the shore it had just built. From
     * SURFACE_STRUCTURES it lands after all of that and still comfortably before VEGETAL_DECORATION, which
     * is what lets the biome's gloomgrass grow over the outer shore and tie it back into the floor.
     * <p>
     * The biome is attached in {@link org.betterx.betternether.world.biomes.Gloomwood}; no other biome
     * carries the tag, so a placement attempt that lands anywhere else simply fails.
     */
    public static final StructureKey.Simple<MegaLavaLakeStructure> MEGA_LAVA_LAKE = StructureManager
            .structure(BetterNether.C.id("mega_lava_lake"), MegaLavaLakeStructure::new)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);


    public static void register() {
        NetherStructurePieces.ensureStaticLoad();
    }
}
