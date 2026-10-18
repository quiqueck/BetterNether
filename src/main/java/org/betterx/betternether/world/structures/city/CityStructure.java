package org.betterx.betternether.world.structures.city;

import org.betterx.bclib.api.v2.levelgen.structures.BCLStructure;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.world.NetherBiomeBuilder;
import org.betterx.betternether.world.structures.city.palette.Palettes;
import org.betterx.betternether.world.structures.piece.CavePiece;
import org.betterx.betternether.world.structures.piece.CityPiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import it.unimi.dsi.fastutil.longs.LongSet;

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
        if (Configs.GENERATOR.getBoolean("generator.world.cities", "generate", true)
                && BCLStructure.isValidBiome(context, DEFAULT_HEIGHT)) {
            final ChunkPos cPos = context.chunkPos();
            final ChunkGenerator chunkGenerator = context.chunkGenerator();
            final LevelHeightAccessor heightAccessor = context.heightAccessor();

            final BlockPos center = getCenter(context, cPos, chunkGenerator, heightAccessor);
            return Optional.of(new Structure.GenerationStub(center, (structurePiecesBuilder) -> {
                generatePieces(structurePiecesBuilder, context);
            }));
        }
        return Optional.empty();
    }

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
                    radius + (NetherBiomeBuilder.useLegacyGeneration ? 0 : 8),
                    random,
                    cityBox
            );
            structurePiecesBuilder.addPiece(cave);
        }
        buildings.forEach(b -> structurePiecesBuilder.addPiece(b));

        BetterNether.LOGGER.info("BBox:" + structurePiecesBuilder.getBoundingBox().toString());
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
     * Whether any city building could be hit by a feature that reaches at most {@code reach} blocks
     * sideways from {@code origin}, at any height.
     * <p>
     * The city is built before the trees of the forest, so a tree that lands in a city grows straight
     * through its buildings. Skipping the whole tree is the only clean answer - clipping it to the
     * building boxes would leave half a trunk hanging in the street.
     */
    public static boolean buildingsWithin(ServerLevelAccessor level, BlockPos origin, int reach) {
        return intersects(level, around(level, origin, reach), false);
    }

    /**
     * Whether a feature that reaches at most {@code reach} blocks sideways from {@code origin} could touch
     * a city at all - one of its buildings or its cave. For features placed in the same step as the city
     * (or before it), where a neighbouring chunk may already have built its part of the city.
     */
    public static boolean cityWithin(ServerLevelAccessor level, BlockPos origin, int reach) {
        return intersects(level, around(level, origin, reach), true);
    }

    private static BoundingBox around(ServerLevelAccessor level, BlockPos origin, int reach) {
        return new BoundingBox(
                origin.getX() - reach, level.getMinBuildHeight(), origin.getZ() - reach,
                origin.getX() + reach, level.getMaxBuildHeight(), origin.getZ() + reach
        );
    }

    /**
     * The starts are collected from every chunk the feature can actually write into: {@code footprint}
     * clipped to the 3x3 chunks around the chunk being decorated. Reading only the origin's own chunk is
     * not enough: a chunk references a city only when the city's overall box (its cave, which reaches just
     * 8 blocks past the outermost building) reaches it, while an anchor tree reaches up to 23 blocks. Every
     * chunk of that write zone is at {@code STRUCTURE_REFERENCES} or later during {@code FEATURES}, and a
     * building piece the feature could write into lies in one of them, so that chunk references its city.
     */
    private static boolean intersects(ServerLevelAccessor level, BoundingBox footprint, boolean includeCave) {
        BoundingBox reachable = footprint;
        final WorldGenRegion region = level instanceof WorldGenRegion r ? r : null;
        if (region != null) {
            // Features may only write into the chunk being decorated and its direct neighbours.
            final ChunkPos center = region.getCenter();
            final int minX = Math.max(footprint.minX(), center.getMinBlockX() - 16);
            final int minZ = Math.max(footprint.minZ(), center.getMinBlockZ() - 16);
            final int maxX = Math.min(footprint.maxX(), center.getMaxBlockX() + 16);
            final int maxZ = Math.min(footprint.maxZ(), center.getMaxBlockZ() + 16);
            if (minX > maxX || minZ > maxZ) return false;
            reachable = new BoundingBox(minX, footprint.minY(), minZ, maxX, footprint.maxY(), maxZ);
        }

        final Set<StructureStart> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        final int minChunkX = SectionPos.blockToSectionCoord(reachable.minX());
        final int maxChunkX = SectionPos.blockToSectionCoord(reachable.maxX());
        final int minChunkZ = SectionPos.blockToSectionCoord(reachable.minZ());
        final int maxChunkZ = SectionPos.blockToSectionCoord(reachable.maxZ());
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (region != null && !region.hasChunk(chunkX, chunkZ)) continue;
                final ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES, false);
                if (chunk == null) continue;
                for (Map.Entry<Structure, LongSet> entry : chunk.getAllReferences().entrySet()) {
                    if (!(entry.getKey() instanceof CityStructure)) continue;
                    for (long reference : entry.getValue()) {
                        final ChunkPos startChunk = new ChunkPos(reference);
                        // A neighbour may reference a start further out than the region holds starts for.
                        if (region != null && !region.hasChunk(startChunk.x, startChunk.z)) continue;
                        final ChunkAccess startAccess = level.getChunk(
                                startChunk.x,
                                startChunk.z,
                                ChunkStatus.STRUCTURE_STARTS,
                                false
                        );
                        if (startAccess == null) continue;
                        final StructureStart start = startAccess.getStartForStructure(entry.getKey());
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
        return NetherStructures.CITY_STRUCTURE.structureType;
    }
}
