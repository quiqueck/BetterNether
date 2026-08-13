package org.betterx.betternether.world.structures.city;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Drops any city block that would land inside another structure's piece, so the two interleave
 * instead of one bulldozing the other.
 * <p>
 * Cities and nether complexes collide often - complex pieces occupy roughly 11% of Nether chunks and
 * reach up to 8 chunks from their start, so a 13x13-chunk city touches one about 79% of the time -
 * and neither ordering is acceptable on its own. Placed before the fortress the city gets
 * nether-brick corridors cut through its buildings (measured at 26% of one tower's volume, which
 * walled in its guard spawner); placed after, the city erases the fortress. Keeping them apart with
 * an {@code exclusion_zone} is not affordable either: the {@code chunk_count: 14} needed to guarantee
 * clearance forbids ~76% of candidate chunks, and a forbidden cell yields no city at all.
 * <p>
 * So the city generates last (see {@code NetherStructures.CITY_STRUCTURE}) and simply declines the
 * blocks the fortress already owns. {@code StructureTemplate.processBlockInfos} drops a block whose
 * processor returns {@code null}, which leaves a gap in the building where the corridor runs through
 * rather than a destroyed corridor.
 * <p>
 * The overlapping boxes are gathered once per chunk by {@link #collect} rather than queried per
 * block: {@code StructureManager.getStructureWithPieceAt} walks every piece of every start, and a
 * fortress can have 170 of them.
 */
public class StructureAvoidingProcessor implements StructureProcessor {
    /**
     * Structures a city yields to. Both live in the {@code minecraft:nether_complexes} set and are
     * built of solid masonry that a city cannot absorb gracefully.
     */
    private static final Set<Identifier> AVOIDED = Set.of(
            Identifier.withDefaultNamespace("fortress"),
            Identifier.withDefaultNamespace("bastion_remnant")
    );

    /** Never serialized - this processor is built in Java per chunk and holds live bounding boxes. */
    public static final MapCodec<StructureAvoidingProcessor> CODEC =
            MapCodec.unit(() -> new StructureAvoidingProcessor(List.of()));

    private final List<BoundingBox> avoid;

    public StructureAvoidingProcessor(List<BoundingBox> avoid) {
        this.avoid = avoid;
    }

    /**
     * The bounding boxes of every avoided structure's pieces that reach {@code within}.
     *
     * @return an empty list when nothing overlaps, in which case the processor is a no-op
     */
    public static List<BoundingBox> collect(
            WorldGenLevel world,
            StructureManager structureManager,
            BoundingBox within
    ) {
        final Registry<net.minecraft.world.level.levelgen.structure.Structure> structures =
                world.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        final List<BoundingBox> result = new ArrayList<>();
        final var chunkPos = new net.minecraft.world.level.ChunkPos(
                within.minX() >> 4, within.minZ() >> 4
        );

        for (StructureStart start : structureManager.startsForStructure(chunkPos, structure -> {
            final Identifier id = structures.getKey(structure);
            return id != null && AVOIDED.contains(id);
        })) {
            for (StructurePiece piece : start.getPieces()) {
                final BoundingBox box = piece.getBoundingBox();
                if (box.intersects(within)) result.add(box);
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return avoid.isEmpty();
    }

    /**
     * @return {@code true} if {@code pos} belongs to an avoided structure
     */
    public boolean claimed(BlockPos pos) {
        for (int i = 0; i < avoid.size(); i++) {
            if (avoid.get(i).isInside(pos)) return true;
        }
        return false;
    }

    @Override
    public StructureBlockInfo processBlock(
            LevelReader worldView,
            BlockPos pos,
            BlockPos blockPos,
            BlockPos originalPos,
            StructureBlockInfo current,
            StructurePlaceSettings settings
    ) {
        // current.pos() is already the world position, which is what the fortress boxes are in.
        return claimed(current.pos()) ? null : current;
    }

    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
