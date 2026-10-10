package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.structures.city.AvoidNetherCity;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.city.StructureCityBuilding;
import org.betterx.betternether.world.structures.city.palette.Palettes;
import org.betterx.betternether.world.structures.piece.CityPiece;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Anchor trees and the Upside Down Forest litter must keep out of nether city buildings (#142), also
 * when the building stands in a neighbouring chunk.
 * <p>
 * A chunk references a city only when the city's overall box - its cave - reaches it, and the cave ends
 * 8 blocks past the outermost building while an anchor tree reaches up to 23. The tree check used to read
 * only the references of the tree's own chunk, so a tree one chunk outside the cave saw no city and grew
 * into the buildings at its edge. Here a real city building is registered as a piece of
 * {@code betternether:nether_city} that only the neighbouring chunk references, exactly that situation.
 */
public class CityTreeClearanceGameTest {
    /** The root's reach, the smallest one the anchor tree features check. */
    private static final int ROOT_REACH = 15;

    @GameTest
    public void treeSeesBuildingInNeighbouringChunk(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        withBuildingInNeighbourChunk(helper, (treeChunk, box) -> {
            // The tree sits in the middle of its chunk, 10 blocks from the building - inside the root's reach.
            final BlockPos tree = new BlockPos(treeChunk.getBlockX(8), box.minY() + 20, treeChunk.getBlockZ(8));
            final int gap = box.minX() - tree.getX();
            if (!CityStructure.buildingsWithin(helper.getLevel(), tree, ROOT_REACH)) {
                failures.add("a tree " + gap + " blocks from a city building in the neighbouring chunk does "
                        + "not see it - it would grow into the building");
            }
            // Control: with a reach that stops short of the building the same tree is allowed, so the pass
            // above is the building's doing and not a blanket refusal.
            if (CityStructure.buildingsWithin(helper.getLevel(), tree, gap - 1)) {
                failures.add("control: a tree reaching " + (gap - 1) + " blocks is refused although the "
                        + "building is " + gap + " blocks away");
            }
        });
        GameTestAssertions.failIfAny(helper, "Anchor tree city clearance regression", failures);
        helper.succeed();
    }

    /**
     * The forest litter's fallen anchor trees are templates placed in {@code SURFACE_STRUCTURES}; when a
     * neighbouring chunk's city already stands they land in its rooms. Its placement has to drop them.
     */
    @GameTest
    public void forestLitterSkipsCityBuildings(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final PlacedFeature litter = level.registryAccess()
                                          .lookupOrThrow(Registries.PLACED_FEATURE)
                                          .getValueOrThrow(ResourceKey.create(
                                                  Registries.PLACED_FEATURE,
                                                  BetterNether.C.id("forest_litter")
                                          ));
        final List<String> failures = new ArrayList<>();
        final List<PlacementModifier> filters = litter.placement()
                                                      .stream()
                                                      .filter(m -> m instanceof AvoidNetherCity)
                                                      .toList();
        if (filters.isEmpty()) {
            failures.add("betternether:forest_litter has no betternether:avoid_nether_city placement");
        } else {
            final PlacementModifier filter = filters.getFirst();
            final PlacementContext context = new PlacementContext(
                    level,
                    level.getChunkSource().getGenerator(),
                    Optional.of(litter)
            );
            withBuildingInNeighbourChunk(helper, (treeChunk, box) -> {
                // On the chunk border, 3 blocks from the building.
                final BlockPos near = new BlockPos(treeChunk.getMaxBlockX(), box.minY(), treeChunk.getBlockZ(8));
                if (places(filter, context, near)) {
                    failures.add("forest litter " + (box.minX() - near.getX()) + " blocks from a city building "
                            + "in the neighbouring chunk is still placed");
                }
                // Control: in the middle of the chunk, 10 blocks away, the litter is out of reach.
                final BlockPos far = new BlockPos(treeChunk.getBlockX(8), box.minY(), treeChunk.getBlockZ(8));
                if (!places(filter, context, far)) {
                    failures.add("control: forest litter " + (box.minX() - far.getX()) + " blocks from the "
                            + "building is dropped as well");
                }
            });
        }
        GameTestAssertions.failIfAny(helper, "Forest litter city clearance regression", failures);
        helper.succeed();
    }

    /**
     * Registers a real city building as a {@code betternether:nether_city} piece that starts 2 blocks into
     * the chunk east of the test's chunk and is referenced only there, runs {@code test} with the test's
     * chunk and the building's box, and restores both chunks' structure data afterwards.
     */
    private static void withBuildingInNeighbourChunk(
            GameTestHelper helper,
            BiConsumer<ChunkPos, BoundingBox> test
    ) {
        final ServerLevel level = helper.getLevel();
        final Structure city = level.registryAccess()
                                    .lookupOrThrow(Registries.STRUCTURE)
                                    .getValueOrThrow(ResourceKey.create(
                                            Registries.STRUCTURE,
                                            BetterNether.C.id("nether_city")
                                    ));

        final BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1));
        final ChunkPos treeChunk = new ChunkPos(base);
        final ChunkPos buildingChunk = new ChunkPos(treeChunk.x + 1, treeChunk.z);
        final CityPiece building = new CityPiece(
                new StructureCityBuilding("city/city_building_06"),
                new BlockPos(buildingChunk.getMinBlockX() + 2, base.getY(), treeChunk.getMinBlockZ()),
                0,
                Palettes.EMPTY
        );
        final BoundingBox box = building.getBoundingBox();
        if (box.minX() >> 4 != buildingChunk.x) {
            helper.fail(Component.literal("setup: the building does not start in the neighbouring chunk (" + box + ")"));
        }

        final ChunkAccess startChunk = level.getChunk(buildingChunk.x, buildingChunk.z);
        final ChunkAccess ownChunk = level.getChunk(treeChunk.x, treeChunk.z);
        final Map<Structure, StructureStart> oldStarts = new HashMap<>(startChunk.getAllStarts());
        final Map<Structure, LongSet> oldStartChunkRefs = copy(startChunk.getAllReferences());
        final Map<Structure, LongSet> oldOwnRefs = copy(ownChunk.getAllReferences());
        try {
            startChunk.setStartForStructure(city, new StructureStart(
                    city, buildingChunk, 0,
                    new PiecesContainer(List.of(building))
            ));
            startChunk.addReferenceForStructure(city, buildingChunk.toLong());
            // The test's own chunk is outside the city's box and holds no reference.
            final Map<Structure, LongSet> ownWithoutCity = copy(ownChunk.getAllReferences());
            ownWithoutCity.remove(city);
            ownChunk.setAllReferences(ownWithoutCity);

            test.accept(treeChunk, box);
        } finally {
            startChunk.setAllStarts(oldStarts);
            startChunk.setAllReferences(oldStartChunkRefs);
            ownChunk.setAllReferences(oldOwnRefs);
        }
    }

    private static boolean places(PlacementModifier filter, PlacementContext context, BlockPos pos) {
        return filter.getPositions(context, RandomSource.create(1), pos).findAny().isPresent();
    }

    private static Map<Structure, LongSet> copy(Map<Structure, LongSet> references) {
        final Map<Structure, LongSet> result = new HashMap<>();
        references.forEach((s, refs) -> result.put(s, new LongOpenHashSet(refs)));
        return result;
    }
}
