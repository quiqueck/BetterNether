package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.lake.MegaLavaLakeStructure;
import org.betterx.betternether.world.structures.templates.*;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class NetherStructures {
    /**
     * Nether City.
     * <p>
     * Generated in {@link GenerationStep.Decoration#FLUID_SPRINGS}, which is the only step that sits
     * after everything that used to cut the city apart and still before everything that has to settle
     * on top of it.
     * <p>
     * {@code ChunkGenerator.applyBiomeDecoration} walks the steps in order and, <b>within each step,
     * places structures first and features second</b>, so a structure is exposed to every feature of
     * its own step plus everything in all later steps. Counting the features the city's biomes actually
     * declare:
     * <ul>
     *     <li>at {@code SURFACE_STRUCTURES} (4) the city lost to 47 features of its own step - the
     *     {@code patch_basalt_stalagmite} / {@code _stalactite} and blackstone variants driven through
     *     the buildings - and to {@code minecraft:fortress} at {@code UNDERGROUND_DECORATION} (7),
     *     which cut nether-brick corridors through the towers;</li>
     *     <li>at {@code TOP_LAYER_MODIFICATION} (10) nothing overwrites it, but it now carves its cave
     *     <i>after</i> the 208 {@code VEGETAL_DECORATION} (9) features have already been placed on the
     *     old terrain, which leaves that vegetation floating where the city removed the ground;</li>
     *     <li>{@code FLUID_SPRINGS} (8) is after both the step-4 features and the fortress, and before
     *     vegetation - so plants settle on the finished city exactly as they did from step 4. City
     *     biomes declare <b>no</b> step-8 features at all, so nothing follows the city here.</li>
     * </ul>
     * An {@code exclusion_zone} against {@code minecraft:nether_complexes} was considered instead and
     * rejected: keeping a 13-chunk-wide city clear of a 16-chunk-wide complex needs
     * {@code chunk_count: 14}, which forbids ~76% of candidate chunks - and a forbidden cell yields no
     * city at all, there is no retry. Collisions are not rare either: complex pieces occupy ~11% of
     * Nether chunks, so a city touches one about 79% of the time. Ordering, not avoidance, is the only
     * affordable fix.
     * <p>
     * Ordering alone would only move the damage onto the fortress, so
     * {@link org.betterx.betternether.world.structures.city.StructureAvoidingProcessor} makes the city
     * decline the blocks a fortress or bastion piece already owns. Both structures generate in full;
     * the building simply has a gap where the corridor passes through.
     */
    public static final StructureKey.Simple<CityStructure> CITY_STRUCTURE = StructureManager
            .structure(BetterNether.C.id("nether_city"), CityStructure::new)
            .step(GenerationStep.Decoration.FLUID_SPRINGS);

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
