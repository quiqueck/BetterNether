package org.betterx.betternether.world.tree.skeleton;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.math.Spline;

import net.minecraft.util.RandomSource;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A trunk that rises, then splits into a fan of arms - the "Y" shape.
 * <p>
 * With {@code arms(2, 2)} it is a literal Y; with three or four it becomes a candelabra. The arms are
 * what carry the canopy anchors, and - importantly for leaf decay - they are placed so that the wood
 * runs <em>up into</em> the foliage rather than stopping underneath it. A canopy hung off a trunk that
 * ends below it will always have leaves too far from any log; see
 * {@link org.betterx.betternether.world.tree.decay.LeafDecay}.
 */
public final class ForkingTrunk implements TrunkShape {
    private final int minHeight;
    private final int maxHeight;
    private final float forkAt;
    private final int minArms;
    private final int maxArms;
    private final float spread;
    private final float lean;
    private final float jitter;
    private final float baseRadius;
    private final float tipRadius;
    private final int anchorsPerArm;

    private ForkingTrunk(Builder b) {
        this.minHeight = b.minHeight;
        this.maxHeight = b.maxHeight;
        this.forkAt = b.forkAt;
        this.minArms = b.minArms;
        this.maxArms = b.maxArms;
        this.spread = b.spread;
        this.lean = b.lean;
        this.jitter = b.jitter;
        this.baseRadius = b.baseRadius;
        this.tipRadius = b.tipRadius;
        this.anchorsPerArm = b.anchorsPerArm;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Skeleton build(TreeSpace space, RandomSource random) {
        final Skeleton.Builder skeleton = Skeleton.builder();

        final int height = minHeight + random.nextInt(Math.max(1, maxHeight - minHeight + 1));
        final float forkHeight = height * forkAt;

        // --- the stem, from the ground to the fork ---
        final float leanAngle = random.nextFloat() * (float) Math.PI * 2;
        final float leanAmount = random.nextFloat() * lean;
        final Vector3f forkPoint = new Vector3f(
                (float) Math.cos(leanAngle) * leanAmount,
                forkHeight,
                (float) Math.sin(leanAngle) * leanAmount
        );

        final List<Vector3f> stem = Spline.line(new Vector3f(0, 0, 0), forkPoint, 4);
        Spline.jitter(stem, random, jitter);
        // The base must stay exactly on the origin block: the sapling grew there, and a trunk that
        // wanders off it leaves a hole where the player was standing.
        stem.get(0).set(0, 0, 0);
        final float forkRadius = baseRadius * 0.75F;
        skeleton.path(Spline.smooth(stem, 3), baseRadius, forkRadius);

        // --- the arms ---
        final int arms = minArms + random.nextInt(Math.max(1, maxArms - minArms + 1));
        final float armBase = random.nextFloat() * (float) Math.PI * 2;
        for (int i = 0; i < arms; i++) {
            // Evenly spaced around the stem with a little slop, so three arms never look surveyed.
            final float angle = armBase + i * (float) Math.PI * 2 / arms
                    + (random.nextFloat() - 0.5F) * 0.6F;
            final float reach = spread * (0.7F + random.nextFloat() * 0.6F);
            final float top = height * (0.85F + random.nextFloat() * 0.3F);

            Vector3f tip = new Vector3f(
                    forkPoint.x + (float) Math.cos(angle) * reach,
                    top,
                    forkPoint.z + (float) Math.sin(angle) * reach
            );
            // Pull the arm back until it fits the write zone. Length-only, so the fan keeps its angles.
            tip = space.fitSegment(forkPoint, tip, tipRadius);

            final List<Vector3f> arm = Spline.line(forkPoint, tip, 4);
            Spline.jitter(arm, random, jitter * 0.7F);
            arm.get(0).set(forkPoint);
            final List<Vector3f> smoothed = Spline.smooth(arm, 3);
            skeleton.path(smoothed, forkRadius, tipRadius);

            for (Anchor anchor : anchorsAlong(smoothed)) {
                skeleton.anchor(anchor);
            }
        }

        return skeleton.build();
    }

    /**
     * Spreads the anchors over the outer part of an arm rather than only at its tip, which is what keeps
     * a long arm's foliage connected to wood along its whole length instead of only at the end.
     */
    private List<Anchor> anchorsAlong(List<Vector3f> arm) {
        final List<Anchor> result = new ArrayList<>(anchorsPerArm);
        for (int i = 0; i < anchorsPerArm; i++) {
            final float t = anchorsPerArm == 1
                    ? 1.0F
                    : 1.0F - 0.35F * i / (anchorsPerArm - 1);
            final int index = Math.min(arm.size() - 1, Math.round(t * (arm.size() - 1)));
            // Trailing anchors are smaller: the canopy tapers back towards the fork.
            result.add(new Anchor(new Vector3f(arm.get(index)), 1.0F - 0.2F * i));
        }
        return result;
    }

    public static final class Builder {
        private int minHeight = 9;
        private int maxHeight = 14;
        private float forkAt = 0.55F;
        private int minArms = 2;
        private int maxArms = 3;
        private float spread = 3.0F;
        private float lean = 0.8F;
        private float jitter = 0.6F;
        private float baseRadius = 1.4F;
        private float tipRadius = 0.7F;
        private int anchorsPerArm = 2;

        public Builder height(int min, int max) {
            this.minHeight = min;
            this.maxHeight = max;
            return this;
        }

        /**
         * Where the stem splits, as a fraction of total height.
         */
        public Builder forkAt(float fraction) {
            this.forkAt = fraction;
            return this;
        }

        public Builder arms(int min, int max) {
            this.minArms = min;
            this.maxArms = max;
            return this;
        }

        /**
         * How far the arm tips reach horizontally from the stem, in blocks.
         */
        public Builder spread(float blocks) {
            this.spread = blocks;
            return this;
        }

        /**
         * How far the stem may lean off vertical before it forks, in blocks.
         */
        public Builder lean(float blocks) {
            this.lean = blocks;
            return this;
        }

        /**
         * Wobble applied to the interior of every run. Purely a silhouette control - the bark texture
         * comes from a separate displacement applied at rasterisation time.
         */
        public Builder jitter(float blocks) {
            this.jitter = blocks;
            return this;
        }

        public Builder radius(float base, float tip) {
            this.baseRadius = base;
            this.tipRadius = tip;
            return this;
        }

        public Builder anchorsPerArm(int count) {
            this.anchorsPerArm = Math.max(1, count);
            return this;
        }

        public ForkingTrunk build() {
            return new ForkingTrunk(this);
        }
    }
}
