package org.betterx.betternether.world.structures.lake;

import org.betterx.betternether.MHelper;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.world.structures.piece.LavaLakePiece;
import de.ambertation.wover.structure.api.structures.StructurePlacement;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.List;
import java.util.Optional;

/**
 * A lava lake a few chunks across, sunk into the floor of the gloomwood.
 * <p>
 * The nether counterpart of BetterEnd's megalake, and built on the same idea: a bowl carved out of the
 * terrain, filled to the level of the surrounding ground, and ringed by a shore carrying growth found
 * nowhere else in the biome. What the two do <em>not</em> share is the way they find their ground - an
 * End island has one surface and the world heightmap points straight at it, while a nether biome is a
 * stack of cave layers under a bedrock roof, and {@code WORLD_SURFACE_WG} in the nether means the roof.
 * So the middle of the lake is found by walking a noise column down for an air-over-solid transition,
 * the way the templated nether structures do.
 *
 * <h2>Why the rim is not checked the same way</h2>
 * The obvious gate - find each rim column's surface the same way and require the nine heights to agree -
 * does not work here, and measurably so: it rejected every candidate in a 400-chunk test region. Each
 * column reports the floor of <em>its own topmost</em> cave, and across 80 blocks of nether the topmost
 * cave is frequently not the same cave, so the heights disagree by tens of blocks even where the lake's
 * own layer is perfectly flat.
 * <p>
 * What actually matters is narrower: the lake must not hang over a drop. So the rim columns are read at
 * the height the lake will sit at instead of at their own surface, and all they have to be is solid
 * there. Terrain standing higher is fine - the lake is then walled on that side, and the piece leaves
 * standing rock alone - and a cave passing underneath is fine too, because the base column is sampled
 * before carvers run and the piece seals its own bed regardless.
 */
public class MegaLavaLakeStructure extends Structure {
    /**
     * Radius of the lake's main lobe. The piece unions one or two smaller lobes onto it, so the outline
     * reaches about {@code 1.3} of this on its long axis - the lake is wider than the number, and not the
     * same amount wider in every direction.
     */
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 18;

    /**
     * How far past {@link #MAX_RADIUS} the rim columns are sampled, covering the union's reach. The
     * structure does not know which way the piece will point its lobes, so the probe ring is simply drawn
     * wide enough to hold any of them.
     */
    private static final double RIM_PROBE_REACH = 1.3;

    /**
     * Depth at the middle of the bowl. Shallow relative to the radius - this is a lake, not a shaft, and
     * the paraboloid runs from here to zero over the radius, so the two have to be scaled together.
     */
    private static final int MIN_DEPTH = 3;
    private static final int MAX_DEPTH = 6;

    /**
     * How far below the lava surface a rim column may still be open before the placement is rejected.
     * <p>
     * Not a flatness test - a nether cave floor rolls by ten blocks inside one layer and a tight number
     * here rejects everything. It is the drop test: still open this far down means the ground genuinely
     * falls away at that point and the lake would pour out of that side. Matched to the shore apron's
     * own reach, since anything shallower than this is something the apron can fill.
     */
    private static final int MAX_RIM_DROP = 8;

    /**
     * Air needed above the middle of the lake, as a single probe at that height rather than a clear run.
     * Kept low on purpose: half of the gloomwood's floor has eight blocks of headroom or less - the
     * measurement is in {@code GloomwoodTreeFeature.Size} - and a lake is looked at from the shore, not
     * from underneath a ceiling it has to clear.
     */
    private static final int REQUIRED_HEADROOM = 5;

    public MegaLavaLakeStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<MegaLavaLakeStructure> type() {
        return NetherStructures.MEGA_LAVA_LAKE.type();
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator generator = context.chunkGenerator();

        // Both drawn before anything is read, so the shape of a lake is a function of its chunk alone.
        // A radius drawn after a rejected column read would change with how the search went.
        final int radius = MHelper.randRange(MIN_RADIUS, MAX_RADIUS, random);
        final int depth = MHelper.randRange(MIN_DEPTH, MAX_DEPTH, random);

        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        // No early biome probe. The templated nether structures open with hasValidBiomeAtRandomHeight,
        // but nether biomes are three-dimensional: a random height throws away candidates whose floor is
        // squarely in the gloomwood merely because some other layer of the same column is not.
        // Structure#findValidGenerationPoint tests the biome at the returned stub - the lake's own middle,
        // at floor level - which is both the right position to ask about and a check that cannot be
        // skipped, so the early one only ever cost accuracy.

        // Search from just under the bedrock roof: starting at sea level would miss every lake in the
        // upper half of the nether, and the roof itself is never an air-over-solid transition.
        final int surfaceY = StructurePlacement.findYDownward(
                generator.getGenDepth() - 20,
                List.of(new BlockPos(x, 0, z)),
                context,
                BlockBehaviour.BlockStateBase::isAir,
                state -> Heightmap.Types.WORLD_SURFACE_WG.isOpaque().test(state) && !state.liquid(),
                1, REQUIRED_HEADROOM
        );
        if (surfaceY == Integer.MIN_VALUE || surfaceY < generator.getMinY() + 8) {
            return Optional.empty();
        }

        final int probeY = surfaceY - MAX_RIM_DROP;
        for (BlockPos column : rimColumns(x, z, radius)) {
            final NoiseColumn noise = generator.getBaseColumn(
                    column.getX(), column.getZ(), context.heightAccessor(), context.randomState()
            );
            if (noise.getBlock(probeY).isAir()) {
                return Optional.empty();
            }
        }

        final BlockPos origin = new BlockPos(x, surfaceY, z);
        return Optional.of(new GenerationStub(
                origin,
                builder -> builder.addPiece(new LavaLakePiece(origin, radius, depth, random))
        ));
    }

    /**
     * Eight points around the rim of the lava surface. Eight rather than four because a lake this wide
     * can straddle a ravine running diagonally between two corners and pass with both of them on solid
     * ground.
     */
    private static List<BlockPos> rimColumns(int x, int z, int lobeRadius) {
        final int radius = (int) (lobeRadius * RIM_PROBE_REACH);
        final int diagonal = (int) (radius * 0.7071F);
        return List.of(
                new BlockPos(x - radius, 0, z),
                new BlockPos(x + radius, 0, z),
                new BlockPos(x, 0, z - radius),
                new BlockPos(x, 0, z + radius),
                new BlockPos(x - diagonal, 0, z - diagonal),
                new BlockPos(x + diagonal, 0, z - diagonal),
                new BlockPos(x - diagonal, 0, z + diagonal),
                new BlockPos(x + diagonal, 0, z + diagonal)
        );
    }
}
