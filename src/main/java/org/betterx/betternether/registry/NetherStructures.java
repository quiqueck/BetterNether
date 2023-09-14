package org.betterx.betternether.registry;

import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.templates.*;
import org.betterx.worlds.together.tag.v3.TagManager;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureKeys;
import org.betterx.wover.structure.api.StructureManager;
import org.betterx.wover.structure.api.sets.StructureSetKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
            .structure(BetterNether.C.id("ghast_hive"), SpawnAltarLadder::new, SpawnAltarLadder.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<RespawnPoints> RESPAWN_POINTS = StructureManager
            .structure(BetterNether.C.id("respawn_points"), RespawnPoints::new, RespawnPoints.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<Pillars> PILLARS = StructureManager
            .structure(BetterNether.C.id("pillars"), Pillars::new, Pillars.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<Gardens> GARDENS = StructureManager
            .structure(BetterNether.C.id("gardens"), Gardens::new, Gardens.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<Portals> PORTALS = StructureManager
            .structure(BetterNether.C.id("portals"), Portals::new, Portals.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<Altars> ALTARS = StructureManager
            .structure(BetterNether.C.id("altars"), Altars::new, Altars.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<JungleTemples> JUNGLE_TEMPLES = StructureManager
            .structure(BetterNether.C.id("jungle_temples"), JungleTemples::new, JungleTemples.CODEC)
            .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureSetKey CITY_STRUCTURE_SET
            = StructureKeys.set(CITY_STRUCTURE);


    public static void register() {
        NetherStructurePieces.ensureStaticLoad();

        TagManager.BIOMES.add(CITY_STRUCTURE.biomeTag(), BiomeAPI.NETHER_WASTES_BIOME.getBiomeKey());
        if (Configs.GENERATOR.getBoolean("generator.world.cities", "overworld", false)) {
            BiomeAPI.registerOverworldBiomeModification((biomeID, biome) -> {
                if (!biomeID.getNamespace().equals(BetterNether.C.modId)) {
                    addNonBNBiomeTags(biomeID, biome);
                }
            });
        }
    }

    public static void addNonBNBiomeTags(ResourceLocation biomeID, Holder<Biome> biome) {
        if (biomeID != null && !biomeID.equals(BiomeAPI.BASALT_DELTAS_BIOME.getID()) && !biomeID.equals(Biomes.THE_VOID.location())) {
            TagManager.BIOMES.add(CITY_STRUCTURE.biomeTag(), biome.value());
        }
    }


}
