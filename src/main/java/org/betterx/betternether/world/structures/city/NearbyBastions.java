package org.betterx.betternether.world.structures.city;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Predicts the bastions {@code minecraft:nether_complexes} will place, without generating any chunk.
 * <p>
 * The set picks fortress or bastion per start chunk, so its placement grid alone cannot tell them apart.
 * {@link #selectedAt} replays {@code ChunkGenerator.createStructures} for one start chunk: the same
 * weighted draw from {@code setLargeFeatureSeed(seed, x, z)}, then each pick's
 * {@code findValidGenerationPoint}, which for fortresses and bastions only computes the start position and
 * checks its biome - the pieces are built lazily. The answer is exactly the structure vanilla places, for
 * any seed and independent of chunk generation order, and building the returned stub's pieces yields the
 * very pieces vanilla will build, because it continues on the same freshly seeded random.
 */
public final class NearbyBastions {
    private static final Identifier BASTION = Identifier.withDefaultNamespace("bastion_remnant");

    private NearbyBastions() {
    }

    /**
     * @return the generation stubs of every bastion starting within {@code range} chunks (Chebyshev) of
     * {@code center}
     */
    public static List<Structure.GenerationStub> startsNear(
            Structure.GenerationContext context,
            ChunkPos center,
            int range
    ) {
        final Optional<Holder.Reference<StructureSet>> setHolder = context
                .registryAccess()
                .lookupOrThrow(Registries.STRUCTURE_SET)
                .get(BuiltinStructureSets.NETHER_COMPLEXES);
        if (setHolder.isEmpty()) return List.of();
        final StructureSet set = setHolder.get().value();
        if (!(set.placement() instanceof RandomSpreadStructurePlacement placement)) return List.of();

        final long seed = context.seed();
        final int spacing = placement.spacing();
        final List<Structure.GenerationStub> result = new ArrayList<>(1);
        for (int gx = Math.floorDiv(center.x() - range, spacing); gx <= Math.floorDiv(center.x() + range, spacing); gx++) {
            for (int gz = Math.floorDiv(center.z() - range, spacing); gz <= Math.floorDiv(center.z() + range, spacing); gz++) {
                final ChunkPos start = placement.getPotentialStructureChunk(seed, gx * spacing, gz * spacing);
                if (Math.abs(start.x() - center.x()) > range || Math.abs(start.z() - center.z()) > range) continue;
                if (!placement.applyAdditionalChunkRestrictions(start.x(), start.z(), seed)) continue;
                final Selected selected = selectedAt(context, set, start);
                if (selected != null && selected.structure.is(key -> key.identifier().equals(BASTION))) {
                    result.add(selected.stub);
                }
            }
        }
        return result;
    }

    public record Selected(Holder<Structure> structure, Structure.GenerationStub stub) {
    }

    /**
     * The structure {@code set} will start at {@code start}, or {@code null} if every option fails.
     * Mirrors the selection in {@code ChunkGenerator.createStructures}.
     */
    public static Selected selectedAt(Structure.GenerationContext context, StructureSet set, ChunkPos start) {
        final List<StructureSet.StructureSelectionEntry> options = new ArrayList<>(set.structures());
        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        random.setLargeFeatureSeed(context.seed(), start.x(), start.z());
        int total = 0;
        for (StructureSet.StructureSelectionEntry option : options) total += option.weight();

        while (!options.isEmpty()) {
            int choice = set.structures().size() == 1 ? 0 : random.nextInt(total);
            int index = 0;
            for (StructureSet.StructureSelectionEntry option : options) {
                choice -= option.weight();
                if (choice < 0) break;
                index++;
            }
            final StructureSet.StructureSelectionEntry selected = options.get(index);
            final Holder<Structure> structure = selected.structure();
            final Optional<Structure.GenerationStub> stub = structure.value().findValidGenerationPoint(
                    new Structure.GenerationContext(
                            context.registryAccess(),
                            context.chunkGenerator(),
                            context.biomeSource(),
                            context.climateSampler(),
                            context.randomState(),
                            context.structureTemplateManager(),
                            context.seed(),
                            start,
                            context.heightAccessor(),
                            structure.value().biomes()::contains
                    )
            );
            if (stub.isPresent()) return new Selected(structure, stub.get());
            options.remove(index);
            total -= selected.weight();
        }
        return null;
    }
}
