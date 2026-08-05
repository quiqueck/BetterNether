package org.betterx.betternether.world.tree.canopy;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.math.Crackle;
import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.math.Volumes;
import org.betterx.betternether.world.tree.skeleton.Anchor;

import net.minecraft.util.RandomSource;

/**
 * A hanging, hooded canopy: a cylindrical body capped by a dome, with an ellipsoid bitten out of the
 * underside so the foliage hangs as a ragged skirt.
 *
 * <pre>
 *      .-'''-.        dome     (sphere, smooth-joined to the body)
 *     |       |       body     (cylinder)
 *     |       |
 *      \_   _/        hem      (what is left after the skirt is subtracted)
 *        '-'          skirt    (ellipsoid, removed)
 * </pre>
 *
 * <h2>Order of operations</h2>
 * The dome is joined to the body <em>before</em> the skirt is removed. Doing it the other way round
 * feeds a subtraction result into {@code smoothUnion}, and subtraction does not preserve the distance
 * metric that the smooth join reads - the seam comes out lumpy and asymmetric. See {@link Volume}.
 *
 * <h2>Leaf decay</h2>
 * The hem is the part of this shape furthest from any wood, so it is the part that leaf decay will act
 * on first. Keeping {@code radius} at or below 5 with an arm running up the body keeps the whole skirt
 * inside the decay budget; larger radii are legal, they just cost the solver a branch or two. Nothing
 * here needs to guard against it - {@link org.betterx.betternether.world.tree.decay.LeafDecay} runs
 * after rasterisation and repairs whatever this shape produced.
 */
public final class GhostCanopy implements CanopyShape {
    private final float radius;
    private final float bodyHeight;
    private final float domeHeight;
    private final float skirtWidth;
    private final float skirtDepth;
    private final float skirtRise;
    private final float smoothing;
    private final float hemWobble;
    private final float minRadius;
    private final float verticalOffset;

    private GhostCanopy(Builder b) {
        this.radius = b.radius;
        this.bodyHeight = b.bodyHeight;
        this.domeHeight = b.domeHeight;
        this.skirtWidth = b.skirtWidth;
        this.skirtDepth = b.skirtDepth;
        this.skirtRise = b.skirtRise;
        this.smoothing = b.smoothing;
        this.hemWobble = b.hemWobble;
        this.minRadius = b.minRadius;
        this.verticalOffset = b.verticalOffset;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Volume volumeAt(Anchor anchor, TreeSpace space, RandomSource random) {
        final float wanted = radius * anchor.scale();
        final float r = space.fitRadius(anchor.position(), wanted, minRadius);
        if (r < 0) return Volume.EMPTY;

        // Everything else scales with the radius that actually fit, so a canopy squeezed by the write
        // zone stays in proportion instead of turning into a squat disc.
        final float shrink = r / wanted;
        final float half = bodyHeight * anchor.scale() * shrink * 0.5F;

        // The body hangs below the anchor rather than being centred on it. That is not a cosmetic
        // choice: the skirt is subtracted out of the middle of the body, and if the body is centred on
        // the anchor then the hollow reaches the anchor too - which puts the branch tip, and every
        // cell next to it, in open space inside the canopy. Support branches then have nowhere buried
        // to grow (they would break the surface immediately) and the leaf-decay solver ends up pruning
        // the canopy instead of feeding it. Dropping the body means the wood arrives through solid
        // foliage in the dome, which is also how a hanging shape reads: below its branch.
        final float drop = verticalOffset * anchor.scale() * shrink;

        final Volume body = Volumes.cylinder(r, half).translate(0, drop, 0);
        final Volume dome = Volumes.ellipsoid(r, domeHeight * anchor.scale() * shrink, r)
                                   .translate(0, drop + half, 0);
        final Volume solid = body.smoothUnion(dome, smoothing);

        final Volume skirt = Volumes
                .ellipsoid(r * skirtWidth, skirtDepth * anchor.scale() * shrink, r * skirtWidth)
                .translate(0, drop - half + skirtRise, 0);

        Volume ghost = solid.subtract(skirt);
        if (hemWobble > 0) {
            ghost = ghost.displace(Crackle.wobble(random.nextLong(), hemWobble, 0.4F));
        }

        final var p = anchor.position();
        return ghost.translate(p.x, p.y, p.z);
    }

    @Override
    public float reach() {
        // Must cover the shape's full extent *from the anchor*, which the drop makes asymmetric: the
        // hem now reaches |offset| + half below it. Under-reporting silently truncates the canopy,
        // because the rasteriser sizes its scan box from this.
        final float below = Math.abs(verticalOffset) + bodyHeight * 0.5F;
        final float above = verticalOffset + bodyHeight * 0.5F + domeHeight;
        return Math.max(radius, Math.max(below, above)) + hemWobble + 1;
    }

    public static final class Builder {
        private float radius = 4.0F;
        private float bodyHeight = 6.0F;
        private float domeHeight = 3.0F;
        private float skirtWidth = 0.72F;
        private float skirtDepth = 3.0F;
        private float skirtRise = 0.5F;
        private float smoothing = 1.5F;
        private float hemWobble = 0.7F;
        private float minRadius = 2.0F;
        private float verticalOffset = 0.0F;

        /**
         * Radius of the cylindrical body, in blocks.
         */
        public Builder radius(float blocks) {
            this.radius = blocks;
            return this;
        }

        /**
         * Height of the cylindrical body. The anchor sits at its vertical centre, so a branch aimed at
         * the anchor runs through the middle of the foliage - which is what keeps the leaves fed for
         * decay purposes.
         */
        public Builder bodyHeight(float blocks) {
            this.bodyHeight = blocks;
            return this;
        }

        /**
         * Vertical semi-axis of the dome sitting on top of the body.
         */
        public Builder domeHeight(float blocks) {
            this.domeHeight = blocks;
            return this;
        }

        /**
         * The bite taken out of the underside: {@code width} as a fraction of the body radius,
         * {@code depth} as the ellipsoid's vertical semi-axis, {@code rise} how far above the body's
         * bottom face its centre sits.
         * <p>
         * A width near 1 leaves only a thin curtain of leaves; below about 0.5 the hollow stops reading
         * at all from outside.
         */
        public Builder skirt(float width, float depth, float rise) {
            this.skirtWidth = width;
            this.skirtDepth = depth;
            this.skirtRise = rise;
            return this;
        }

        /**
         * Width of the rounded seam where the dome meets the body. 0 gives a hard crease ring.
         */
        public Builder smoothing(float blocks) {
            this.smoothing = blocks;
            return this;
        }

        /**
         * Noise amplitude applied to the finished surface, which tears the hem instead of leaving it a
         * clean arc.
         */
        public Builder hemWobble(float blocks) {
            this.hemWobble = blocks;
            return this;
        }

        /**
         * Below this radius the canopy is not drawn at all, rather than drawn as a stub.
         */
        public Builder minRadius(float blocks) {
            this.minRadius = blocks;
            return this;
        }

        /**
         * How far below the anchor the body hangs, in blocks (negative drops it).
         * <p>
         * Keep the drop large enough that the top of the subtracted skirt stays below the anchor -
         * otherwise the hollow swallows the branch tip, and the leaf-decay solver has no buried route
         * out of the wood. Roughly {@code skirtDepth - bodyHeight/2 - 2} is the shallowest that works.
         */
        public Builder verticalOffset(float blocks) {
            this.verticalOffset = blocks;
            return this;
        }

        public GhostCanopy build() {
            return new GhostCanopy(this);
        }
    }
}
