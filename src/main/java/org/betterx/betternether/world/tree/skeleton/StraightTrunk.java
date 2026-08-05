package org.betterx.betternether.world.tree.skeleton;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.math.Spline;

import net.minecraft.util.RandomSource;

import org.joml.Vector3f;

import java.util.List;

/**
 * A single unbranched run of wood, optionally leaning and wobbling.
 * <p>
 * The plain case, kept so that the simpler nether trees (mushroom-fir-like stalks, bushes) can be
 * expressed through the same pipeline as the complicated ones and get the same leaf-decay guarantee.
 */
public final class StraightTrunk implements TrunkShape {
    private final int minHeight;
    private final int maxHeight;
    private final float lean;
    private final float jitter;
    private final float baseRadius;
    private final float tipRadius;
    private final float anchorFrom;
    private final int anchors;

    private StraightTrunk(Builder b) {
        this.minHeight = b.minHeight;
        this.maxHeight = b.maxHeight;
        this.lean = b.lean;
        this.jitter = b.jitter;
        this.baseRadius = b.baseRadius;
        this.tipRadius = b.tipRadius;
        this.anchorFrom = b.anchorFrom;
        this.anchors = b.anchors;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Skeleton build(TreeSpace space, RandomSource random) {
        final Skeleton.Builder skeleton = Skeleton.builder();
        final int height = minHeight + random.nextInt(Math.max(1, maxHeight - minHeight + 1));

        final float leanAngle = random.nextFloat() * (float) Math.PI * 2;
        final float leanAmount = random.nextFloat() * lean;
        Vector3f top = new Vector3f(
                (float) Math.cos(leanAngle) * leanAmount,
                height,
                (float) Math.sin(leanAngle) * leanAmount
        );
        top = space.fitSegment(new Vector3f(0, 0, 0), top, tipRadius);

        final List<Vector3f> path = Spline.line(new Vector3f(0, 0, 0), top, 5);
        Spline.jitter(path, random, jitter);
        path.get(0).set(0, 0, 0);
        final List<Vector3f> smoothed = Spline.smooth(path, 3);
        skeleton.path(smoothed, baseRadius, tipRadius);

        for (int i = 0; i < anchors; i++) {
            final float t = anchors == 1
                    ? 1.0F
                    : anchorFrom + (1.0F - anchorFrom) * i / (anchors - 1);
            final int index = Math.min(smoothed.size() - 1, Math.round(t * (smoothed.size() - 1)));
            skeleton.anchor(new Anchor(new Vector3f(smoothed.get(index))));
        }

        return skeleton.build();
    }

    public static final class Builder {
        private int minHeight = 5;
        private int maxHeight = 8;
        private float lean = 0.0F;
        private float jitter = 0.3F;
        private float baseRadius = 1.0F;
        private float tipRadius = 0.8F;
        private float anchorFrom = 0.6F;
        private int anchors = 1;

        public Builder height(int min, int max) {
            this.minHeight = min;
            this.maxHeight = max;
            return this;
        }

        public Builder lean(float blocks) {
            this.lean = blocks;
            return this;
        }

        public Builder jitter(float blocks) {
            this.jitter = blocks;
            return this;
        }

        public Builder radius(float base, float tip) {
            this.baseRadius = base;
            this.tipRadius = tip;
            return this;
        }

        /**
         * Places {@code count} canopy anchors over the top of the trunk, starting at {@code from} as a
         * fraction of its height.
         */
        public Builder anchors(int count, float from) {
            this.anchors = Math.max(1, count);
            this.anchorFrom = from;
            return this;
        }

        public StraightTrunk build() {
            return new StraightTrunk(this);
        }
    }
}
