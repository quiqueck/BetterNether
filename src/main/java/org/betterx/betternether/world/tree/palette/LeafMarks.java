package org.betterx.betternether.world.tree.palette;

import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.math.Volumes;

import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A base leaf block, plus regions painted in a different one.
 *
 * <h2>Regions, not geometry</h2>
 * A mark is a {@link Volume} evaluated in <em>anchor-local</em> space - the position of the leaf minus
 * the position of the canopy anchor it belongs to. That means a mark is defined once and applies
 * identically to every canopy on the tree, and, more usefully, that marks are decoupled from the canopy
 * shape entirely: the shape decides where there are leaves at all, the mark only recolours the ones that
 * fall inside it. Nothing is placed outside the canopy, so a mark that pokes out of the foliage simply
 * has no effect rather than growing a blob of stray blocks.
 */
public final class LeafMarks implements TreePalette.LeafSource {
    private final Supplier<BlockState> base;
    private final List<Mark> marks;

    private LeafMarks(Builder b) {
        this.base = b.base;
        this.marks = List.copyOf(b.marks);
    }

    public static Builder of(Supplier<BlockState> base) {
        return new Builder(base);
    }

    @Override
    public BlockState at(TreePalette.LeafQuery query) {
        final float lx = query.local().x - query.anchor().x;
        final float ly = query.local().y - query.anchor().y;
        final float lz = query.local().z - query.anchor().z;

        for (Mark mark : marks) {
            if (mark.primaryOnly && !query.primary()) continue;
            if (mark.region.contains(lx, ly, lz)
                    && (mark.chance >= 1 || query.random().nextFloat() < mark.chance)) {
                return mark.block.get();
            }
        }
        return base.get();
    }

    private record Mark(Volume region, Supplier<BlockState> block, float chance, boolean primaryOnly) {
    }

    public static final class Builder {
        private final Supplier<BlockState> base;
        private final List<Mark> marks = new ArrayList<>();

        private Builder(Supplier<BlockState> base) {
            this.base = base;
        }

        /**
         * Paints every leaf inside {@code region} (anchor-local) with {@code block}. Marks are tested in
         * the order added; the first match wins.
         */
        public Builder mark(Volume region, Supplier<BlockState> block) {
            return mark(region, block, 1.0F);
        }

        public Builder mark(Volume region, Supplier<BlockState> block, float chance) {
            marks.add(new Mark(region, block, chance, false));
            return this;
        }

        /**
         * As {@link #mark(Volume, Supplier)}, but only on the tree's primary canopy lobe - so the
         * tree carries the mark once rather than once per lobe.
         */
        public Builder markOnPrimaryLobe(Volume region, Supplier<BlockState> block) {
            marks.add(new Mark(region, block, 1.0F, true));
            return this;
        }

        /**
         * A pair of round marks on one side of every canopy lobe - a face per lobe.
         *
         * @see #eyesOnPrimaryLobe
         */
        public Builder eyes(
                float facing, float height, float separation, float radius, float forward,
                Supplier<BlockState> block
        ) {
            return addEyes(facing, height, separation, radius, forward, block, false);
        }

        /**
         * A single pair of round marks, on the tree's primary lobe only - one face per tree.
         *
         * @param facing     which way the face looks, in radians around the Y axis
         * @param height     how far above the anchor the pair sits
         * @param separation distance between the two centres
         * @param radius     radius of each mark
         * @param forward    how far from the lobe's axis the centres sit. Set it a little inside the
         *                   canopy radius so the sphere straddles the shell: a mark only recolours
         *                   leaves that already exist, so the part outside simply has no effect and
         *                   what remains is a clean round patch on the surface. Placing it exactly on
         *                   the radius leaves barely any leaves inside the sphere and the eye reads as
         *                   a smudge
         */
        public Builder eyesOnPrimaryLobe(
                float facing, float height, float separation, float radius, float forward,
                Supplier<BlockState> block
        ) {
            return addEyes(facing, height, separation, radius, forward, block, true);
        }

        private Builder addEyes(
                float facing, float height, float separation, float radius, float forward,
                Supplier<BlockState> block, boolean primaryOnly
        ) {
            final float fx = (float) Math.cos(facing);
            final float fz = (float) Math.sin(facing);
            // Perpendicular in the horizontal plane, which is the axis the two marks are spread along.
            final float px = -fz;
            final float pz = fx;
            final float half = separation * 0.5F;

            marks.add(new Mark(
                    Volumes.sphere(radius).translate(fx * forward + px * half, height, fz * forward + pz * half),
                    block, 1.0F, primaryOnly
            ));
            marks.add(new Mark(
                    Volumes.sphere(radius).translate(fx * forward - px * half, height, fz * forward - pz * half),
                    block, 1.0F, primaryOnly
            ));
            return this;
        }

        public LeafMarks build() {
            return new LeafMarks(this);
        }
    }
}
