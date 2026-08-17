package org.betterx.betternether.world.structures.piece;

import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.blocks.BlockGloomwispVine;
import org.betterx.betternether.blocks.BlockMagmaFlower;
import org.betterx.betternether.noise.OpenSimplexNoise;
import org.betterx.betternether.registry.NetherStructurePieces;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The bowl, the lava and the shore of a
 * {@link org.betterx.betternether.world.structures.lake.MegaLavaLakeStructure}.
 *
 * <h2>Shape</h2>
 * Not one bowl but the union of two or three overlapping ones ({@link #lobes}), which is what gives the
 * outline its bean and kidney silhouettes. A single disc pushed around by noise stays recognisably a
 * disc no matter how much noise is thrown at it - the amplitude needed for a real concave bay would tear
 * the edge apart long before it dented it. Overlapping discs get the concavity for free: where two of
 * them meet at an angle the outline pinches into a waist, and the noise is then only doing what it is
 * good at, roughening an edge that already has a shape.
 * <p>
 * Every column reduces the union to two numbers - {@code t}, the distance to the nearest lobe's middle
 * as a fraction of that lobe's radius, and {@code dOut}, the blocks beyond the outline - so everything
 * downstream works exactly as it did on one circle. The depth is a paraboloid in {@code t}, falling to
 * zero exactly at the rim. That is not a stylistic choice, it is the containment: the outermost ring
 * comes out as solid ground at floor level rather than as lava, so the pool is walled by its own bed and
 * nothing has to be built around it. Lobes are derived from the piece's saved seed, and the noise is read
 * in world coordinates, so every chunk of the lake computes the same edge without consulting its
 * neighbours.
 *
 * <h2>Standing rock</h2>
 * A column with more than {@link #HEADROOM} blocks of rock above the lava level is left completely alone -
 * no carve, no lava, no shore. Those are the spurs and walls that reach up to the cavern roof, and
 * shaving {@link #HEADROOM} blocks off the bottom of one would leave the rest of it hanging in the air.
 * The lake simply flows around them, which is also the only thing that puts islands in it.
 *
 * <h2>Shore</h2>
 * The inner part of the shore is a forced apron, levelled with the lava and filled down to solid ground;
 * that is what actually seals the lake where the surrounding floor happens to fall away. Beyond the apron
 * nothing is levelled - the existing surface is only re-skinned, with the chance of doing so fading out,
 * so the shore dissolves into the biome floor instead of ending on a ring.
 * <p>
 * Nothing on that shore is foreign to the gloomwood. The skin is the biome's own three floor blocks and
 * the growth is the biome's own three plants; the heat is expressed by <em>which</em> of them, not by
 * imported ones. Molten gloomsculk comes through in patches, thickest at the water and thinning outward
 * but a minority throughout - the shore is the biome's floor with heat working into it, not a ring of a
 * material of its own. The magma flower (which stands on sculk-like ground, see
 * {@code NetherSurvival.magmaSandOrSculk}) rides that same gradient at a minority share of the cover,
 * with gloomgrass and the wisps carrying the rest. Magma block appears only under lava, never as
 * something walked on.
 */
public class LavaLakePiece extends CustomPiece {
    private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
    private static final BlockState MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();

    /**
     * Width of the shore band, measured out from the rim of the lava. Scaled with the lake: at the
     * original radius this was 13, which on a lake half that wide would have made the shore as broad as
     * the water it rings.
     */
    private static final int SHORE_WIDTH = 8;

    /**
     * The fraction of the shore band that is forced flat and level with the lava. The rest is left at
     * whatever height the terrain already had.
     */
    private static final double APRON = 0.4;

    /**
     * How far the apron will reach down for solid ground before giving up on a column. Matched to
     * {@code MegaLavaLakeStructure.MAX_RIM_DROP}, so every drop the placement was willing to accept is
     * one the apron can actually bridge.
     */
    private static final int APRON_FILL_DEPTH = 8;

    /** Thickness of the sealing shell hung under the magma bed where the bowl cut into open air. */
    private static final int BED_SHELL = 3;

    /**
     * Air kept clear above the lava. Also the cut-off for what counts as a bump to be shaved rather than
     * as standing rock to be left alone - see the class comment.
     */
    private static final int HEADROOM = 5;

    /** Fraction of the outline radius the noise may push the edge in or out. */
    private static final double OUTLINE_WOBBLE = 0.18;

    /** One shore block in this many grows a gloomwisp stalk instead of a single-block plant. */
    private static final int WISP_ONE_IN = 20;

    /** Height range of those stalks, in blocks including the head. */
    private static final int MIN_WISP_HEIGHT = 2;
    private static final int MAX_WISP_HEIGHT = 4;

    /**
     * The molten-gloomsculk patch gate: a column is molten where the heat channel clears
     * {@code MOLTEN_AT_WATER + s * MOLTEN_FADE}.
     * <p>
     * Both numbers exist to keep molten a minority, and both were set by measuring the generated shore
     * rather than by eye. The first version had a solid molten band over the inner fifth and a threshold
     * of -0.1 beyond it, which made the near shore almost entirely molten; 0.15 brought that to 43% of
     * the skin, still the largest single share; 0.20 leaves it a minority against the bleached and plain
     * sculk it is mixed into. There is no unconditional band at any setting - the bowl's own rim ring is
     * already molten, and that one block of lip is enough to meet the lava with.
     */
    private static final double MOLTEN_AT_WATER = 0.20;
    private static final double MOLTEN_FADE = 0.40;

    /**
     * Where the non-molten part of the shore turns from bleached gloomsculk to vanilla sculk.
     * <p>
     * Not a 50/50 split: the biome floor this shore has to run into is 19% sculk, and measured over the
     * noise below this threshold leaves the same 19% here. See
     * {@link org.betterx.betternether.world.biomes.Gloomwood} for the floor it is matched to - if that
     * mix is retuned, this has to be re-measured with it or the shore stops matching the floor.
     */
    private static final double SCULK_PATCH_THRESHOLD = 0.305;

    /**
     * The magma flower's share of the cover at the waterline; it falls to nothing at the outer edge of
     * the band, so across the whole shore it is well under half of this.
     * <p>
     * A minority even where it is thickest, and deliberately so. The flower is the one warm note in the
     * planting, and the shore is still gloomwood - the grass and the wisps have to carry it, with the
     * flowers showing through. Ramping from 1.0 instead, which is what this first did, produced a solid
     * red band at the water that read as a different biome's ground cover.
     */
    private static final double MAX_FLOWER_SHARE = 0.3;

    /** One disc of the union: its middle relative to {@link #center}, and its own radius. */
    private record Lobe(double offsetX, double offsetZ, double radius) {
        /** How far this lobe reaches from the lake's middle. */
        double reach() {
            return Math.sqrt(offsetX * offsetX + offsetZ * offsetZ) + radius;
        }
    }

    /** A column resolved against the union: see the class comment for what the two numbers mean. */
    private record Shape(double t, double dOut) {
    }

    private final BlockPos center;
    private final int radius;
    private final int depth;
    private final int seed;
    private final OpenSimplexNoise noise;
    private final Lobe[] lobes;

    public LavaLakePiece(BlockPos center, int radius, int depth, RandomSource random) {
        this(center, radius, depth, random.nextInt(), random.nextInt());
    }

    private LavaLakePiece(BlockPos center, int radius, int depth, int genDepth, int seed) {
        super(NetherStructurePieces.LAVA_LAKE_PIECE, genDepth,
                makeBoundingBox(center, depth, lobes(radius, seed)));
        this.center = center.immutable();
        this.radius = radius;
        this.depth = depth;
        this.seed = seed;
        this.noise = new OpenSimplexNoise(seed);
        this.lobes = lobes(radius, seed);
    }

    public LavaLakePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(NetherStructurePieces.LAVA_LAKE_PIECE, tag);
        this.center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        this.radius = tag.getIntOr("radius", 0);
        this.depth = tag.getIntOr("depth", 0);
        this.seed = tag.getIntOr("seed", 0);
        this.noise = new OpenSimplexNoise(this.seed);
        this.lobes = lobes(this.radius, this.seed);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putInt("radius", radius);
        tag.putInt("depth", depth);
        tag.putInt("seed", seed);
    }

    /**
     * The discs whose union is the lake, derived from the seed rather than stored - the seed is saved
     * anyway for the noise, and deriving keeps the two from ever disagreeing after a reload.
     * <p>
     * The first is the lake proper. The second is pushed out to one side far enough to clear the first
     * one's edge but not so far that it detaches: with an offset of {@code 0.45..0.6 R} against a radius
     * of {@code 0.55..0.7 R}, the two always overlap and the outline always pinches where they meet -
     * which is the bean. The third turns up half the time and makes that bean a clover instead; it is
     * placed at somewhere between a right angle and a straight line from the second, so it opens a
     * different bay rather than deepening the same one.
     */
    private static Lobe[] lobes(int radius, int seed) {
        final RandomSource random = RandomSource.create(seed);
        final double angle = random.nextDouble() * Math.PI * 2;

        final List<Lobe> out = new ArrayList<>(3);
        out.add(new Lobe(0, 0, radius));

        final double d1 = radius * (0.45 + random.nextDouble() * 0.15);
        final double r1 = radius * (0.55 + random.nextDouble() * 0.15);
        out.add(new Lobe(Math.cos(angle) * d1, Math.sin(angle) * d1, r1));

        if (random.nextBoolean()) {
            final double a2 = angle + Math.PI * (0.5 + random.nextDouble() * 0.5);
            final double d2 = radius * (0.4 + random.nextDouble() * 0.15);
            final double r2 = radius * (0.45 + random.nextDouble() * 0.15);
            out.add(new Lobe(Math.cos(a2) * d2, Math.sin(a2) * d2, r2));
        }
        return out.toArray(new Lobe[0]);
    }

    private static BoundingBox makeBoundingBox(BlockPos center, int depth, Lobe[] lobes) {
        double furthest = 0;
        for (Lobe lobe : lobes) furthest = Math.max(furthest, lobe.reach());
        final int reach = (int) Math.ceil(furthest * (1 + OUTLINE_WOBBLE)) + SHORE_WIDTH + 1;
        return new BoundingBox(
                center.getX() - reach,
                center.getY() - depth - BED_SHELL - APRON_FILL_DEPTH,
                center.getZ() - reach,
                center.getX() + reach,
                center.getY() + HEADROOM,
                center.getZ() + reach
        );
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager arg,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        final MutableBlockPos cursor = new MutableBlockPos();
        final int minX = Math.max(blockBox.minX(), boundingBox.minX());
        final int maxX = Math.min(blockBox.maxX(), boundingBox.maxX());
        final int minZ = Math.max(blockBox.minZ(), boundingBox.minZ());
        final int maxZ = Math.min(blockBox.maxZ(), boundingBox.maxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                final Shape shape = shapeAt(x, z);
                if (shape.dOut() >= SHORE_WIDTH) continue;
                // Standing rock is not touched at all, on either side of the rim.
                if (overburden(world, cursor, x, z) > HEADROOM) continue;

                if (shape.t() < 1.0) {
                    carveBowl(world, cursor, x, z, shape.t());
                } else {
                    buildShore(world, cursor, random, x, z, shape.dOut() / SHORE_WIDTH);
                }
            }
        }
    }

    /**
     * Resolves one column against the union of lobes.
     * <p>
     * The nearest lobe wins, measured in its own radii - which is what makes the union a union: a column
     * inside any one disc is inside the lake, and the depth it gets is the depth of the disc it is most
     * deeply inside. {@code dOut} converts the winner's overshoot back into blocks so the shore band has
     * a real width rather than one that scales with whichever lobe it happens to sit off.
     * <p>
     * One wobble value for all the lobes, not one each: sampled per lobe, the two edges would ripple
     * independently and the waist where they cross would come apart.
     */
    private Shape shapeAt(int x, int z) {
        // Frequency scaled with the lake when it was halved: on a small lake a field this coarse no
        // longer ripples the edge, it slides the whole disc sideways and leaves it a disc.
        final double wobble = noise.eval(x * 0.07, z * 0.07) * radius * OUTLINE_WOBBLE;

        double bestT = Double.MAX_VALUE;
        double bestRadius = radius;
        for (Lobe lobe : lobes) {
            final double dx = x - center.getX() - lobe.offsetX();
            final double dz = z - center.getZ() - lobe.offsetZ();
            final double t = (Math.sqrt(dx * dx + dz * dz) - wobble) / lobe.radius();
            if (t < bestT) {
                bestT = t;
                bestRadius = lobe.radius();
            }
        }
        return new Shape(bestT, (bestT - 1.0) * bestRadius);
    }

    /**
     * How many blocks of unbroken rock sit directly above the lava level in this column, counted up to
     * {@code HEADROOM + 1} and no further - the caller only needs to know whether the column is a bump or
     * a wall.
     */
    private int overburden(WorldGenLevel world, MutableBlockPos cursor, int x, int z) {
        final int surfaceY = center.getY();
        for (int i = 1; i <= HEADROOM + 1; i++) {
            cursor.set(x, surfaceY + i, z);
            if (isOpen(world.getBlockState(cursor))) return i - 1;
        }
        return HEADROOM + 1;
    }

    /** Air, plants and other blocks this piece may write straight through. */
    private static boolean isOpen(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    /**
     * One column inside the lake: a magma bed, lava on top of it up to the surrounding floor level, and
     * clear air above.
     *
     * @param t how deep into the nearest lobe this column sits - {@code 0} at that lobe's middle,
     *          {@code 1} on its rim
     */
    private void carveBowl(WorldGenLevel world, MutableBlockPos cursor, int x, int z, double t) {
        final int surfaceY = center.getY();
        final int bowl = (int) Math.round(depth * (1 - t * t));
        final int bedY = surfaceY - bowl;

        for (int y = surfaceY; y > bedY; y--) {
            cursor.set(x, y, z);
            BlocksHelper.setWithoutUpdate(world, cursor, LAVA);
        }

        // Magma only ever goes under lava. At the very rim the paraboloid has run out of depth and there
        // is no lava above this block at all - it is part of what the player walks up to - so it takes
        // the shore's own material instead, and magma never becomes a surface of the biome.
        cursor.set(x, bedY, z);
        BlocksHelper.setWithoutUpdate(
                world, cursor,
                bowl > 0 ? MAGMA : NetherTerrainBlocks.MOLTEN_GLOOMSCULK.defaultBlockState()
        );

        // Where the bowl cut into a cave the bed would hang over nothing and the lake would drain through
        // it. A shell rather than a fill: caves are carved in RAW_GENERATION, long before this runs, and
        // closing one off completely would cost the player the passage under the lake.
        for (int y = bedY - 1; y >= bedY - BED_SHELL; y--) {
            cursor.set(x, y, z);
            if (!isOpen(world.getBlockState(cursor))) break;
            BlocksHelper.setWithoutUpdate(world, cursor, NETHERRACK);
        }

        clearAbove(world, cursor, x, z);
    }

    /**
     * Takes the roof off a column. Only ever called where {@link #overburden} found a bump rather than a
     * wall, so this cannot leave rock hanging over the lake.
     */
    private void clearAbove(WorldGenLevel world, MutableBlockPos cursor, int x, int z) {
        final int surfaceY = center.getY();
        for (int y = surfaceY + 1; y <= surfaceY + HEADROOM; y++) {
            cursor.set(x, y, z);
            final BlockState state = world.getBlockState(cursor);
            if (state.isAir()) continue;
            BlocksHelper.setWithoutUpdate(world, cursor, CAVE_AIR);
        }
    }

    /**
     * One column of the shore.
     *
     * @param s how far through the band this column sits: {@code 0} at the lava's edge, {@code 1} at the
     *          outer end of it
     */
    private void buildShore(
            WorldGenLevel world, MutableBlockPos cursor, RandomSource random, int x, int z, double s
    ) {
        final int surfaceY = center.getY();
        final int groundY;

        if (s < APRON) {
            // The apron is what holds the lava in, so it is built rather than found: everything above the
            // lava level is taken away, and the column is filled down until it meets something solid.
            clearAbove(world, cursor, x, z);
            if (!fillDownToGround(world, cursor, x, z, surfaceY)) return;
            groundY = surfaceY;
        } else {
            // Outside the apron the terrain keeps its own shape; only its skin changes, and less and less
            // of it the further out the column sits.
            final double fade = (s - APRON) / (1 - APRON);
            if (random.nextDouble() < fade) return;

            groundY = findSurface(world, cursor, x, z, surfaceY + 3, surfaceY - 3);
            if (groundY == Integer.MIN_VALUE) return;
        }

        cursor.set(x, groundY, z);
        final BlockState ground = shoreGround(x, z, s);
        BlocksHelper.setWithoutUpdate(world, cursor, ground);

        // Lush at the waterline, thinning to about a quarter at the outer edge of the band.
        if (random.nextDouble() < 1.0 - 0.75 * s) {
            vegetate(world, cursor.immutable(), ground, random, s);
        }
    }

    /**
     * Makes a column solid from {@code fromY} downwards, reaching at most {@link #APRON_FILL_DEPTH}
     * blocks for existing ground to land on.
     * <p>
     * Netherrack with one veined gloomsculk layer directly under the skin, which is how the biome builds
     * its own floor - without it the cut edge of the apron shows a hard sculk-on-rock stripe wherever the
     * terrain is opened up next to it.
     *
     * @return {@code false} if there was no ground within reach; the column is then left untouched rather
     * than given a pillar to stand on
     */
    private boolean fillDownToGround(WorldGenLevel world, MutableBlockPos cursor, int x, int z, int fromY) {
        int groundY = Integer.MIN_VALUE;
        for (int y = fromY - 1; y >= fromY - APRON_FILL_DEPTH; y--) {
            cursor.set(x, y, z);
            final BlockState state = world.getBlockState(cursor);
            if (!isOpen(state) && state.getFluidState().isEmpty()) {
                groundY = y;
                break;
            }
        }
        if (groundY == Integer.MIN_VALUE) return false;

        for (int y = fromY - 2; y > groundY; y--) {
            cursor.set(x, y, z);
            BlocksHelper.setWithoutUpdate(world, cursor, NETHERRACK);
        }
        if (fromY - 1 > groundY) {
            cursor.set(x, fromY - 1, z);
            BlocksHelper.setWithoutUpdate(world, cursor, NetherTerrainBlocks.VEINED_GLOOMSCULK.defaultBlockState());
        }
        return true;
    }

    /** The highest solid block of a column within a window, or {@link Integer#MIN_VALUE} if it has none. */
    private int findSurface(WorldGenLevel world, MutableBlockPos cursor, int x, int z, int topY, int bottomY) {
        for (int y = topY; y >= bottomY; y--) {
            cursor.set(x, y, z);
            final BlockState state = world.getBlockState(cursor);
            if (!isOpen(state) && state.getFluidState().isEmpty()) return y;
        }
        return Integer.MIN_VALUE;
    }

    /**
     * What the shore is made of at one column: the gloomwood's own three floor blocks and nothing else.
     * <p>
     * Noise channels rather than a random roll, so the materials come out as patches that hold together
     * across chunk seams instead of as speckle. The molten patches are gated on {@code s} as well as on
     * their own channel - the threshold climbs with the distance from the water until nothing clears it -
     * which is what turns "hot at the edge, cool further out" into a gradient rather than a ring.
     */
    private BlockState shoreGround(int x, int z, double s) {
        final double heat = noise.eval(x * 0.09, z * 0.09, 60);
        if (heat > MOLTEN_AT_WATER + s * MOLTEN_FADE) {
            return NetherTerrainBlocks.MOLTEN_GLOOMSCULK.defaultBlockState();
        }

        // The same two-block mix, on the same kind of noise, that the biome's surface rule lays over the
        // rest of its floor - so the outer shore is already the biome floor by the time it stops. The
        // threshold is what makes the shares match: measured over this noise it leaves sculk on 10% of
        // the skin, which is the share the biome's FLOOR_SCULK condition was tuned to.
        return noise.eval(x * 0.12, z * 0.12, -60) > SCULK_PATCH_THRESHOLD
                ? Blocks.SCULK.defaultBlockState()
                : NetherTerrainBlocks.BLEACHED_GLOOMSCULK.defaultBlockState();
    }

    /**
     * Puts one plant on top of a shore block: gloomgrass and the occasional gloomwisp stalk, with magma
     * flowers mixed through the part of the band nearest the lava.
     * <p>
     * The native cover is what the shore is made of; the flower's share falls off with the distance from
     * the lava on the same ramp the molten ground does, so the warm plant grows on the warm rock rather
     * than being scattered over the whole band and happening to land on it. It never takes more than
     * {@link #MAX_FLOWER_SHARE} of the cover, and that only at the waterline.
     * <p>
     * Which gloomgrass grows is decided by the block under it rather than by a roll, the same way the
     * biome's two grass features are split by their ground filters: pale on the bleached gloomsculk,
     * dark on the sculk and on the molten patches, whose skin is sculk with fissures in it.
     * <p>
     * {@code canSurvive} is still asked before anything is written. All three of these accept the shore's
     * materials by their registered traits, so it should never refuse; asking anyway is what keeps this
     * honest if one of those traits is narrowed later.
     *
     * @param groundState the block just written at {@code ground} - read from the argument rather than
     *                    from the world, which has not been told about it (it was set without updates).
     */
    private void vegetate(
            WorldGenLevel world, BlockPos ground, BlockState groundState, RandomSource random, double s
    ) {
        final BlockPos above = ground.above();
        if (!world.getBlockState(above).isAir()) return;

        if (random.nextInt(WISP_ONE_IN) == 0 && placeWisp(world, above, random)) return;

        final BlockState plant;
        if (random.nextDouble() < MAX_FLOWER_SHARE * (1.0 - s)) {
            plant = NetherPlantBlocks.MAGMA_FLOWER.defaultBlockState()
                                                  .setValue(BlockMagmaFlower.AGE, 1 + random.nextInt(3));
        } else {
            plant = (groundState.is(NetherTerrainBlocks.BLEACHED_GLOOMSCULK)
                    ? NetherPlantBlocks.PALE_GLOOMGRASS
                    : NetherPlantBlocks.GLOOMGRASS).defaultBlockState();
        }

        if (plant.canSurvive(world, above)) {
            BlocksHelper.setWithoutUpdate(world, above, plant);
        }
    }

    /**
     * A gloomwisp stalk - the tall element of the shoreline, and the biome's own answer to it.
     * <p>
     * The wisp derives its {@code SHAPE} from its neighbours, but this writes without neighbour updates,
     * so each segment is given the value {@code shapeAt} would have produced: TOP for the head, MIDDLE
     * for the one segment directly under it (which carries the bright half of the stem gradient) and
     * BOTTOM below that. The headroom is checked first, because a stalk whose head is cut off by the
     * ceiling reads as broken.
     */
    private boolean placeWisp(WorldGenLevel world, BlockPos base, RandomSource random) {
        final int height = MIN_WISP_HEIGHT + random.nextInt(MAX_WISP_HEIGHT - MIN_WISP_HEIGHT + 1);
        final MutableBlockPos cursor = new MutableBlockPos();
        for (int i = 0; i < height; i++) {
            cursor.set(base.getX(), base.getY() + i, base.getZ());
            if (!world.getBlockState(cursor).isAir()) return false;
        }

        final BlockState stalk = NetherVineBlocks.GLOOMWISP_VINE.defaultBlockState();
        for (int i = 0; i < height; i++) {
            final TripleShape shape = i == height - 1
                    ? TripleShape.TOP
                    : i == height - 2 ? TripleShape.MIDDLE : TripleShape.BOTTOM;
            cursor.set(base.getX(), base.getY() + i, base.getZ());
            BlocksHelper.setWithoutUpdate(world, cursor, stalk.setValue(BlockGloomwispVine.SHAPE, shape));
        }
        return true;
    }
}
