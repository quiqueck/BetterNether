package org.betterx.betternether.world.tree.palette;

import de.ambertation.wover.math.api.noise.OpenSimplexNoise;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Selects the log block from how high up the trunk it sits.
 *
 * <h2>Boundaries are not flat</h2>
 * A band boundary evaluated purely from height is a horizontal plane, and a horizontal plane cutting
 * through a trunk reads as a painted stripe - most obviously on a wide trunk, where the whole cross
 * section changes block in one layer. There are two ways out, and which one a wood wants depends on
 * whether its bands blend or butt together:
 * <ul>
 *     <li>{@link Builder#jitter(float, long)} offsets every boundary per column with 2D noise, so a
 *     band edge wanders by a block or two around the trunk and reads as a gradient.</li>
 *     <li>{@link Builder#seam(float, Supplier)} is for a wood that has a dedicated joint block between
 *     two others - gloomwood's transition log. A joint is a joint: it belongs in exactly one layer of
 *     any given column. Declared as an ordinary band it would instead fill every layer of its height
 *     range, stacking two or three transition logs on top of each other, which reads as a third kind
 *     of wood rather than as the place where the dark wood ends. A seam draws one layer per column,
 *     independently, from within its range: the joint is one block thick everywhere and sits at a
 *     different height in each column, so the two woods interleave over the band instead of meeting
 *     along a plane. That is the same problem jitter solves, solved at the resolution a one-block
 *     joint needs - which is why a seam does not want jitter on top of it.</li>
 * </ul>
 */
public final class LogBands implements TreePalette.LogSource {
    /**
     * Horizontal scale of the jitter noise, in blocks<sup>-1</sup>. Coarse enough that a trunk's own
     * columns mostly agree - a band edge tilts and steps across the trunk instead of dithering - but
     * fine enough that a 3-wide trunk still spans a visible part of one noise feature.
     */
    private static final double NOISE_SCALE = 0.35;

    private final List<Band> bands;
    private final OpenSimplexNoise jitterNoise;
    private final float jitterAmount;
    private final long seamSeed;
    private final boolean applyAxis;

    private LogBands(Builder b) {
        this.bands = List.copyOf(b.bands);
        this.jitterAmount = b.jitterAmount;
        this.jitterNoise = b.jitterAmount > 0 ? new OpenSimplexNoise(b.jitterSeed) : null;
        this.seamSeed = b.seamSeed;
        this.applyAxis = b.applyAxis;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public BlockState at(TreePalette.LogQuery query) {
        float height = query.heightAbove();
        if (jitterNoise != null) {
            // Sampled from the world position so neighbouring trees do not share a boundary pattern, and
            // at a coarse scale so the boundary undulates rather than dithering block by block.
            height -= (float) jitterNoise.eval(
                    query.world().getX() * NOISE_SCALE, query.world().getZ() * NOISE_SCALE
            ) * jitterAmount;
        }

        BlockState state = null;
        float from = 0;
        for (Band band : bands) {
            if (band.seam) {
                // Compared as whole layers: the seam owns one cell of this column and nothing else, so
                // the block below it has to fall through to the band under the seam and the one above it
                // to the band over it. Note that the jitter, being constant for a column, shifts which
                // world layer that is without ever splitting the seam or dropping it - `floor` of
                // consecutive heights is still consecutive.
                final int layer = Mth.floor(height);
                final int seam = seamLayer(query, from, band.upTo);
                if (layer > seam) {
                    from = band.upTo;
                    continue;
                }
                if (layer == seam) state = band.block.get();
                break;
            }
            state = band.block.get();
            if (height < band.upTo) break;
            from = band.upTo;
        }
        if (state == null) return null;

        if (applyAxis && state.hasProperty(BlockStateProperties.AXIS)) {
            state = state.setValue(BlockStateProperties.AXIS, query.axis());
        }
        return state;
    }

    /**
     * A seam with nothing above it is not a seam: it is the top of a branch that happened to stop on
     * the joint layer, so the joint is capped by leaves or sky and joins the wood below it to nothing.
     * Such a cell falls back to the band under the seam - the wood the rest of that column already is
     * - rather than to the one over it, which would strand a single cell of the upper wood on top of
     * a branch made entirely of the lower one.
     * <p>
     * Only the top is settled. A branch leaning out far enough that its underside starts on the joint
     * layer leaves a joint with no wood below it either, but that one is a face of the branch rather
     * than the end of it, and it reads as the joint continuing around the wood.
     */
    @Override
    public BlockState topOfColumn(BlockState state) {
        if (state == null) return state;
        for (int i = 1; i < bands.size() - 1; i++) {
            if (!bands.get(i).seam()) continue;
            if (!state.is(bands.get(i).block.get().getBlock())) continue;

            return sameAxis(bands.get(i - 1).block.get(), state);
        }
        return state;
    }

    private BlockState sameAxis(BlockState state, BlockState like) {
        if (applyAxis && state.hasProperty(BlockStateProperties.AXIS)
                && like.hasProperty(BlockStateProperties.AXIS)) {
            return state.setValue(BlockStateProperties.AXIS, like.getValue(BlockStateProperties.AXIS));
        }
        return state;
    }

    /**
     * The one layer, of those in {@code [from, upTo)}, that this column's seam sits in - drawn
     * independently for every column, so the joint is ragged across a trunk rather than a lid on it.
     * <p>
     * Chosen among whole layers rather than by scaling the height range directly, because the two ends
     * of that range are rarely on layer boundaries and a layer the band below already owns is not a
     * layer the seam can have: {@code height < from} is tested first, so a seam placed at, say, 2 out of
     * a range starting at 2.6 would never be reached and that column would get no joint at all - the
     * dark wood would meet the pale wood directly. {@code lowest} is therefore the first layer the band
     * below cannot claim, and {@code highest} the last one wholly inside the range.
     * <p>
     * Hashed from the column rather than drawn from {@link TreePalette.LogQuery#random()}: the palette
     * is asked about the cells of a trunk in whatever order the rasteriser walks its bounding box, and
     * it is asked about each column once per log in it. A draw per query would therefore give a column
     * several different answers - stacking the joint again, and this time unrepeatably - and would make
     * the tree depend on how many cells happened to be tested. The seed keeps two trees standing next
     * to each other from sharing a pattern.
     */
    private int seamLayer(TreePalette.LogQuery query, float from, float upTo) {
        final int lowest = Mth.ceil(from);
        final int highest = Math.max(lowest, Mth.ceil(upTo) - 1);
        return lowest + choice(query.world().getX(), query.world().getZ(), highest - lowest + 1);
    }

    /**
     * A stable, uniform {@code [0, choices)} per column - a 64-bit mix, so that neighbouring columns
     * (whose inputs differ by one) land on unrelated values instead of walking through the range.
     */
    private int choice(int x, int z, int choices) {
        long h = seamSeed ^ (x * 0x9E3779B97F4A7C15L) ^ (z * 0xC2B2AE3D27D4EB4FL);
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        return (int) Math.floorMod(h, choices);
    }

    private record Band(float upTo, Supplier<BlockState> block, boolean seam) {
    }

    public static final class Builder {
        private final List<Band> bands = new ArrayList<>();
        private float jitterAmount = 0;
        private long jitterSeed = 0;
        private long seamSeed = 0;
        private boolean applyAxis = true;

        /**
         * Adds a band used for every block below {@code upTo} blocks above the tree base and above the
         * previous band. Add them bottom-up; the last one added is used for everything above it, so its
         * {@code upTo} is ignored.
         * <p>
         * The block is a {@link Supplier} because wood materials are resolved through registries that
         * may not be populated at the point where a palette is declared as a static field.
         */
        public Builder band(float upTo, Supplier<BlockState> block) {
            bands.add(new Band(upTo, block, false));
            return this;
        }

        /**
         * Adds a band that is exactly one block thick: per column, a single layer picked from between
         * the previous band's boundary and {@code upTo}, with the surrounding bands meeting directly
         * everywhere else. For a wood whose bands are joined by a dedicated transition block, this is
         * what keeps that block a joint instead of a band of its own - see the class documentation.
         * <p>
         * The range is what the choice is made from, so it wants to be a few blocks tall; a seam is
         * still one block thick however wide it is. Needs a band below it and one above it.
         */
        public Builder seam(float upTo, Supplier<BlockState> block) {
            bands.add(new Band(upTo, block, true));
            return this;
        }

        /**
         * The block used above the final boundary.
         */
        public Builder top(Supplier<BlockState> block) {
            bands.add(new Band(Float.MAX_VALUE, block, false));
            return this;
        }

        /**
         * Wanders every band boundary by up to {@code blocks} depending on the column, so the seams are
         * not flat planes.
         */
        public Builder jitter(float blocks, long seed) {
            this.jitterAmount = blocks;
            this.jitterSeed = seed;
            return this;
        }

        /**
         * Seeds the per-column draw that places a {@link #seam(float, Supplier)}, so that two trees
         * standing next to each other do not put their joints at the same heights.
         */
        public Builder seamSeed(long seed) {
            this.seamSeed = seed;
            return this;
        }

        /**
         * Whether to set {@code axis} from the skeleton direction on states that have it. On by default;
         * turn it off for wood that is not a {@code RotatedPillarBlock}.
         */
        public Builder applyAxis(boolean apply) {
            this.applyAxis = apply;
            return this;
        }

        public LogBands build() {
            if (bands.isEmpty()) throw new IllegalStateException("a LogBands needs at least one band");
            for (int i = 0; i < bands.size(); i++) {
                // A seam is defined as the joint between the bands on either side of it, so it cannot be
                // the outermost band: with nothing below it the column would be empty under the joint,
                // and with nothing above it everything over the joint would fall back to the band below
                // and the seam would do nothing at all.
                if (bands.get(i).seam() && (i == 0 || i == bands.size() - 1)) {
                    throw new IllegalStateException("a seam needs a band below and above it");
                }
            }
            return new LogBands(this);
        }
    }
}
