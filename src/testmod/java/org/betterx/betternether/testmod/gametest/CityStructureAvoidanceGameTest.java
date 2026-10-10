package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherStructurePieces;
import org.betterx.betternether.world.structures.city.StructureAvoidingProcessor;
import org.betterx.betternether.world.structures.piece.CavePiece;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * A nether city's cave and buildings must leave the structures in {@code #betternether:city_avoids}
 * alone - by default exactly the fortress and the bastion. Small structures must not be in it: a
 * protected box keeps the terrain inside it, so a small structure in the middle of a city cave would
 * float there as a cube of netherrack instead of being carved.
 */
public class CityStructureAvoidanceGameTest {
    /** The nether complexes - big masonry structures the city builds and carves around. */
    private static final List<ResourceKey<Structure>> PROTECTED = List.of(
            vanilla("fortress"),
            vanilla("bastion_remnant")
    );

    /**
     * Small structures are carved like terrain - protecting them left netherrack cubes floating in the
     * cave. The city must never yield to itself.
     */
    private static final List<ResourceKey<Structure>> UNPROTECTED = List.of(
            vanilla("ruined_portal_nether"),
            own("altars"),
            own("gardens"),
            own("ghast_hive"),
            own("jungle_temples"),
            own("pillars"),
            own("portals"),
            own("pyramid"),
            own("respawn_points"),
            own("spawn_altar_ladder"),
            own("nether_city")
    );

    @GameTest
    public void cityAvoidsTheStructuresItCanOverlap(GameTestHelper helper) {
        final Registry<Structure> structures = helper.getLevel()
                                                     .registryAccess()
                                                     .lookupOrThrow(Registries.STRUCTURE);
        final List<String> failures = new ArrayList<>();
        for (ResourceKey<Structure> key : PROTECTED) {
            final Structure structure = structures.getValue(key);
            if (structure == null) failures.add(key + " is not registered");
            else if (!StructureAvoidingProcessor.isAvoided(structures, structure))
                failures.add(key + " is not in " + StructureAvoidingProcessor.AVOIDED + " - a city would carve through it");
        }
        for (ResourceKey<Structure> key : UNPROTECTED) {
            final Structure structure = structures.getValue(key);
            if (structure == null) failures.add(key + " is not registered");
            else if (StructureAvoidingProcessor.isAvoided(structures, structure))
                failures.add(key + " is in " + StructureAvoidingProcessor.AVOIDED + " but must not be"
                        + " - it would float as a block of netherrack in a city cave");
        }
        final Set<ResourceKey<Structure>> members = new HashSet<>();
        structures.getTagOrEmpty(StructureAvoidingProcessor.AVOIDED)
                  .forEach(holder -> holder.unwrapKey().ifPresent(members::add));
        if (!members.equals(new HashSet<>(PROTECTED))) {
            failures.add(StructureAvoidingProcessor.AVOIDED + " holds " + members + ", expected exactly " + PROTECTED);
        }
        GameTestAssertions.failIfAny(helper, "City structure avoidance tag regression", failures);
        helper.succeed();
    }

    /**
     * End to end: a stone block is registered in the level as a piece of {@code minecraft:fortress},
     * then a real {@link CavePiece} is carved over it. The fortress's half must survive untouched while
     * the other half is carved. The control repeats it with the structure attributed to the (unprotected)
     * {@code betternether:portals}, where the cave has to carve straight through - so a pass is the
     * tag's doing, and a small structure is not kept as a floating block.
     */
    @GameTest
    public void cityCaveKeepsBlocksOfAProtectedStructure(GameTestHelper helper) {
        final Registry<Structure> structures = helper.getLevel()
                                                     .registryAccess()
                                                     .lookupOrThrow(Registries.STRUCTURE);
        final List<String> failures = new ArrayList<>();

        final int[] fortress = carve(helper, structures.getValue(vanilla("fortress")));
        if (fortress[0] != 0) {
            failures.add(fortress[0] + " block(s) of the minecraft:fortress piece were carved by the city cave");
        }
        if (fortress[1] == 0) {
            failures.add("the cave carved nothing outside the fortress piece - the test setup is broken");
        }

        final int[] control = carve(helper, structures.getValue(own("portals")));
        if (control[0] == 0) {
            failures.add("control: the cave left an unprotected betternether:portals piece intact - "
                    + "it would float in the cave, or the protection does not come from the tag");
        }

        GameTestAssertions.failIfAny(helper, "City cave hollowed out a protected structure", failures);
        helper.succeed();
    }

    /**
     * Fills a 7x5x7 block of stone, registers its low-x half as a piece of {@code structure} in the
     * chunk, carves a city cave over the whole block and restores the chunk's structure data.
     *
     * @return {carved blocks inside the piece, carved blocks outside it}
     */
    private static int[] carve(GameTestHelper helper, Structure structure) {
        final ServerLevel level = helper.getLevel();
        final BlockPos min = helper.absolutePos(new BlockPos(1, 1, 1));
        final BlockPos max = helper.absolutePos(new BlockPos(7, 5, 7));
        final BoundingBox area = BoundingBox.fromCorners(min, max);
        final BoundingBox piece = new BoundingBox(
                area.minX(), area.minY(), area.minZ(),
                area.minX() + 2, area.maxY(), area.maxZ()
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
        }

        // StructureAvoidingProcessor.collect reads the starts referenced from the chunk of the area's
        // minimum corner, exactly as it does during worldgen.
        final ChunkPos chunkPos = new ChunkPos(new BlockPos(area.minX(), area.minY(), area.minZ()));
        final ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
        final Map<Structure, StructureStart> oldStarts = new HashMap<>(chunk.getAllStarts());
        final Map<Structure, LongSet> oldReferences = new HashMap<>();
        chunk.getAllReferences().forEach((s, refs) -> oldReferences.put(s, new LongOpenHashSet(refs)));

        try {
            chunk.setStartForStructure(structure, new StructureStart(
                    structure, chunkPos, 0,
                    new PiecesContainer(List.of(new MarkerPiece(piece)))
            ));
            chunk.addReferenceForStructure(structure, chunkPos.toLong());

            // Radius 30 so the cave's noise can never pull its surface inside this small block.
            final BlockPos center = BlockPos.containing(area.getCenter().getX(), area.getCenter().getY(), area.getCenter().getZ());
            new CavePiece(center, 30, RandomSource.create(1), area).postProcess(
                    level,
                    level.structureManager(),
                    level.getChunkSource().getGenerator(),
                    RandomSource.create(1),
                    area,
                    chunkPos,
                    center
            );
        } finally {
            chunk.setAllStarts(oldStarts);
            chunk.setAllReferences(oldReferences);
        }

        int carvedInside = 0;
        int carvedOutside = 0;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(pos).is(Blocks.STONE)) continue;
            if (piece.isInside(pos)) carvedInside++;
            else carvedOutside++;
        }
        return new int[]{carvedInside, carvedOutside};
    }

    /** A piece that only claims a box - what the processor reads off any real structure piece. */
    private static class MarkerPiece extends StructurePiece {
        MarkerPiece(BoundingBox box) {
            super(NetherStructurePieces.CAVE_PIECE, 0, box);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        }

        @Override
        public void postProcess(
                WorldGenLevel level,
                StructureManager structureManager,
                ChunkGenerator generator,
                RandomSource random,
                BoundingBox box,
                ChunkPos chunkPos,
                BlockPos pos
        ) {
        }
    }

    private static ResourceKey<Structure> vanilla(String path) {
        return ResourceKey.create(Registries.STRUCTURE, net.minecraft.resources.ResourceLocation.withDefaultNamespace(path));
    }

    private static ResourceKey<Structure> own(String path) {
        return ResourceKey.create(Registries.STRUCTURE, BetterNether.C.id(path));
    }
}
