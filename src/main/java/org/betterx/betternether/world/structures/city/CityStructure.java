package org.betterx.betternether.world.structures.city;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.world.structures.city.palette.Palettes;
import org.betterx.betternether.world.structures.piece.CavePiece;
import org.betterx.betternether.world.structures.piece.CityPiece;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.LongSet;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.structure.api.StructureUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class CityStructure extends Structure {
    private static CityGenerator generator;
    public static final int RADIUS = 8 * 8;

    public CityStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    public static void initGenerator() {
        generator = new CityGenerator();
    }

    private static final int DEFAULT_HEIGHT = 40;

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (StructureUtils.isValidBiome(context, DEFAULT_HEIGHT)) {
            final ChunkPos cPos = context.chunkPos();
            final ChunkGenerator chunkGenerator = context.chunkGenerator();
            final LevelHeightAccessor heightAccessor = context.heightAccessor();

            final BlockPos center = getCenter(context, cPos, chunkGenerator, heightAccessor);
            final List<GenerationStub> bastions = NearbyBastions.startsNear(context, cPos, BASTION_REACH);
            if (bastions.isEmpty()) {
                return Optional.of(new Structure.GenerationStub(center, (structurePiecesBuilder) -> {
                    generatePieces(structurePiecesBuilder, context);
                }));
            }

            // Build the city now rather than lazily: its footprint decides whether it may exist at all.
            // The pieces come out identical, since nothing else draws from context.random() first.
            final StructurePiecesBuilder pieces = new StructurePiecesBuilder();
            generatePieces(pieces, context);
            final BoundingBox footprint = pieces.getBoundingBox();
            for (GenerationStub bastion : bastions) {
                for (StructurePiece piece : bastion.getPiecesBuilder().build().pieces()) {
                    if (piece.getBoundingBox().intersects(footprint)) return Optional.empty();
                }
            }
            return Optional.of(new Structure.GenerationStub(center, Either.right(pieces)));
        }
        return Optional.empty();
    }

    /**
     * How far (in chunks) a bastion start can be from the city's start and still reach into the city.
     * <p>
     * A city's footprint (its cave) reaches about 6 chunks from its centre and a bastion's pieces stay
     * within 80 blocks (5 chunks) of its start; the largest start distance measured for a real overlap was
     * 10 chunks across 1,101 cities on two seeds.
     * <p>
     * The city now generates after the nether complexes and yields to their pieces (see
     * {@link StructureAvoidingProcessor}), so an overlap no longer destroys either structure - but a
     * bastion embedded in the city's cave, with the buildings stepping around it, is not a city or a
     * bastion anyone would recognise. Skipping the city costs about 10.5% of city sites (57 of 545 and 60
     * of 556 on the two seeds). Fortresses are deliberately not checked: their pieces reach about 38% of
     * city sites, and their thin corridors pass through a city without hiding it.
     */
    private static final int BASTION_REACH = 12;

    private static void generatePieces(
            StructurePiecesBuilder structurePiecesBuilder,
            Structure.GenerationContext context
    ) {
        final ChunkPos cPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final LevelHeightAccessor heightAccessor = context.heightAccessor();
        final WorldgenRandom random = context.random();

        final BlockPos center = getCenter(context, cPos, chunkGenerator, heightAccessor);

        //CityPalette palette = Palettes.getRandom(random);
        final List<CityPiece> buildings = generator.generate(center, random, Palettes.EMPTY);

        BoundingBox cityBox = new BoundingBox(center);
        for (CityPiece p : buildings)
            cityBox = cityBox.encapsulate(p.getBoundingBox());

        final int d1 = Math.max((center.getX() - cityBox.minX()), (cityBox.maxX() - center.getX()));
        final int d2 = Math.max((center.getZ() - cityBox.minZ()), (cityBox.maxZ() - center.getZ()));
        int radius = Math.max(d1, d2);
        if (radius / 2 + center.getY() < cityBox.maxY()) {
            radius = (cityBox.maxY() - center.getY()) / 2;
        }

        if (!(chunkGenerator instanceof FlatLevelSource)) {
            CavePiece cave = new CavePiece(
                    center,
                    radius + 8,
                    random,
                    cityBox
            );
            structurePiecesBuilder.addPiece(cave);
        }
        buildings.forEach(b -> structurePiecesBuilder.addPiece(b));

        BetterNether.C.log.verbose("BBox:" + structurePiecesBuilder.getBoundingBox().toString());
    }

    @NotNull
    private static BlockPos getCenter(
            GenerationContext context,
            ChunkPos cPos,
            ChunkGenerator chunkGenerator,
            LevelHeightAccessor heightAccessor
    ) {
        final int px = cPos.getBlockX(8);
        final int pz = cPos.getBlockZ(8);
        final int y = chunkGenerator instanceof FlatLevelSource
                ? chunkGenerator.getBaseHeight(px, pz, Types.WORLD_SURFACE, heightAccessor, context.randomState())
                : DEFAULT_HEIGHT;
        final BlockPos center = new BlockPos(px, y, pz);
        return center;
    }


    /**
     * {@link #buildingsIntersect(ServerLevelAccessor, BlockPos, BoundingBox)} for a feature that reaches
     * at most {@code reach} blocks sideways from {@code origin}, at any height.
     */
    public static boolean buildingsWithin(ServerLevelAccessor level, BlockPos origin, int reach) {
        return buildingsIntersect(level, origin, new BoundingBox(
                origin.getX() - reach, level.getMinY(), origin.getZ() - reach,
                origin.getX() + reach, level.getMaxY(), origin.getZ() + reach
        ));
    }

    /**
     * Whether any city building could be hit by a feature that writes into {@code footprint}.
     * <p>
     * The city generates in {@code FLUID_SPRINGS}, before the {@code VEGETAL_DECORATION} trees, so a
     * tree that lands in a city grows straight through the buildings: it writes into their rooms and
     * replaces their brick. Measured on an Upside Down Forest city, anchor trees put 2,782 blocks into
     * the rooms of its buildings and replaced 104 of their own blocks. Skipping the whole tree is the
     * only clean answer - clipping it to the building boxes would leave half a trunk hanging in the
     * street.
     * <p>
     * The starts are collected from every chunk the feature can actually write into: {@code footprint}
     * clipped to the {@link WriteZone} (the 3x3 chunks around the one being decorated). Reading only
     * {@code origin}'s own chunk was not enough. A chunk references a city only when the city's overall
     * box reaches it, and that box is the cave, which reaches just 8 blocks past the outermost building -
     * an anchor tree reaches up to 23. A tree a chunk outside the cave saw no city at all and grew into
     * the buildings at its edge. Every chunk of the write zone is guaranteed to be at
     * {@code STRUCTURE_REFERENCES} or later during {@code FEATURES}, and a building piece the tree could
     * write into lies in one of those chunks, so that chunk references its city. Only the references of
     * the zone and the starts they point to are read - exactly what {@code FEATURES} guarantees to be
     * there - so this never reads unfinished terrain or a chunk the region does not hold.
     *
     * @param origin    the feature's origin; kept for callers, the chunks come from {@code footprint}
     * @param footprint everything the feature may write to
     */
    public static boolean buildingsIntersect(ServerLevelAccessor level, BlockPos origin, BoundingBox footprint) {
        return intersects(level, footprint, false);
    }

    /**
     * Whether a feature that reaches at most {@code reach} blocks sideways from {@code origin} could
     * touch a city at all - one of its buildings or its cave.
     * <p>
     * For features placed before the city: the city carves its cave and builds its buildings in
     * {@code FLUID_SPRINGS}, but a neighbouring chunk may already have done so. Whatever such a feature
     * puts into a finished cave is left floating there, and in its own chunk the cave carves it away
     * later, so inside a city it can only do harm. Read the same way as
     * {@link #buildingsIntersect(ServerLevelAccessor, BlockPos, BoundingBox)}.
     */
    public static boolean cityWithin(ServerLevelAccessor level, BlockPos origin, int reach) {
        return intersects(level, new BoundingBox(
                origin.getX() - reach, level.getMinY(), origin.getZ() - reach,
                origin.getX() + reach, level.getMaxY(), origin.getZ() + reach
        ), true);
    }

    private static boolean intersects(ServerLevelAccessor level, BoundingBox footprint, boolean includeCave) {
        final WriteZone zone = WriteZone.of(level);
        if (zone.isDisjoint(footprint.minX(), footprint.minZ(), footprint.maxX(), footprint.maxZ())) return false;
        // Outside worldgen (a sapling, /place feature, a gametest) the zone is unbounded and this is the
        // footprint itself.
        final BoundingBox reachable = zone.clip(footprint);

        final Set<StructureStart> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        final int minChunkX = SectionPos.blockToSectionCoord(reachable.minX());
        final int maxChunkX = SectionPos.blockToSectionCoord(reachable.maxX());
        final int minChunkZ = SectionPos.blockToSectionCoord(reachable.minZ());
        final int maxChunkZ = SectionPos.blockToSectionCoord(reachable.maxZ());
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                final Map<Structure, LongSet> references = level
                        .getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES)
                        .getAllReferences();
                for (Map.Entry<Structure, LongSet> entry : references.entrySet()) {
                    if (!(entry.getKey() instanceof CityStructure)) continue;
                    for (long reference : entry.getValue()) {
                        final ChunkPos startChunk = ChunkPos.unpack(reference);
                        // A neighbour may reference a start one chunk further out than the region holds
                        // starts for; a city is far too small to reach the write zone from there.
                        if (level instanceof WorldGenRegion region && !region.hasChunk(startChunk.x(), startChunk.z()))
                            continue;
                        final StructureStart start = level
                                .getChunk(startChunk.x(), startChunk.z(), ChunkStatus.STRUCTURE_STARTS)
                                .getStartForStructure(entry.getKey());
                        if (start == null || !start.isValid() || !seen.add(start)) continue;
                        for (StructurePiece piece : start.getPieces()) {
                            if ((includeCave || piece instanceof CityPiece)
                                    && piece.getBoundingBox().intersects(reachable)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.CITY_STRUCTURE.type();
    }
}
