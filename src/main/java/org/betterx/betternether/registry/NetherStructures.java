package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.templates.*;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureManager;

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


    public static void register() {
        NetherStructurePieces.ensureStaticLoad();
    }
}
