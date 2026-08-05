package org.betterx.betternether.world.tree.canopy;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.math.Crackle;
import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.math.Volumes;
import org.betterx.betternether.world.tree.skeleton.Anchor;

import net.minecraft.util.RandomSource;

/**
 * A lumpy ellipsoid - the ordinary canopy, and the shape most existing nether trees actually want.
 * <p>
 * Present mainly so that porting an existing tree onto this pipeline (and thereby onto working leaf
 * decay) does not force a redesign of how it looks.
 */
public final class BlobCanopy implements CanopyShape {
    private final float radius;
    private final float heightRatio;
    private final float wobble;
    private final float minRadius;

    private BlobCanopy(Builder b) {
        this.radius = b.radius;
        this.heightRatio = b.heightRatio;
        this.wobble = b.wobble;
        this.minRadius = b.minRadius;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Volume volumeAt(Anchor anchor, TreeSpace space, RandomSource random) {
        final float wanted = radius * anchor.scale();
        final float r = space.fitRadius(anchor.position(), wanted, minRadius);
        if (r < 0) return Volume.EMPTY;

        Volume blob = Volumes.ellipsoid(r, r * heightRatio, r);
        if (wobble > 0) {
            blob = blob.displace(Crackle.wobble(random.nextLong(), wobble, 0.45F));
        }
        final var p = anchor.position();
        return blob.translate(p.x, p.y, p.z);
    }

    @Override
    public float reach() {
        return radius * Math.max(1, heightRatio) + wobble + 1;
    }

    public static final class Builder {
        private float radius = 3.5F;
        private float heightRatio = 0.8F;
        private float wobble = 0.6F;
        private float minRadius = 1.5F;

        public Builder radius(float blocks) {
            this.radius = blocks;
            return this;
        }

        /**
         * Vertical semi-axis as a multiple of the horizontal one. Below 1 gives the squashed dome most
         * broadleaf canopies want.
         */
        public Builder heightRatio(float ratio) {
            this.heightRatio = ratio;
            return this;
        }

        public Builder wobble(float blocks) {
            this.wobble = blocks;
            return this;
        }

        public Builder minRadius(float blocks) {
            this.minRadius = blocks;
            return this;
        }

        public BlobCanopy build() {
            return new BlobCanopy(this);
        }
    }
}
