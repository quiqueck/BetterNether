package org.betterx.betternether.world.tree.skeleton;

import org.betterx.betternether.world.tree.math.Volume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The woody part of a tree: every {@link Segment} plus the {@link Anchor}s foliage hangs on.
 * <p>
 * Held as segments rather than as a single {@link Volume} because the rasteriser needs per-segment
 * information that a merged distance function has thrown away - notably {@link Segment#axis()}, which
 * decides the {@code axis} property of each log block.
 */
public record Skeleton(List<Segment> segments, List<Anchor> anchors) {
    public Skeleton {
        segments = List.copyOf(segments);
        anchors = List.copyOf(anchors);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The union of every segment - used for bounds, never for block placement.
     */
    public Volume toVolume() {
        Volume result = Volume.EMPTY;
        for (Segment s : segments) {
            result = result.union(s.toVolume());
        }
        return result;
    }

    /**
     * The largest distance from the tree's origin that any segment reaches, in blocks.
     */
    public float horizontalReach() {
        float max = 0;
        for (Segment s : segments) {
            max = Math.max(max, horizontalReach(s.from().x, s.from().z, s.maxRadius()));
            max = Math.max(max, horizontalReach(s.to().x, s.to().z, s.maxRadius()));
        }
        return max;
    }

    private static float horizontalReach(float x, float z, float radius) {
        return (float) Math.sqrt(x * x + z * z) + radius;
    }

    public float highestPoint() {
        float max = 0;
        for (Segment s : segments) {
            max = Math.max(max, Math.max(s.from().y, s.to().y) + s.maxRadius());
        }
        return max;
    }

    public static final class Builder {
        private final List<Segment> segments = new ArrayList<>();
        private final List<Anchor> anchors = new ArrayList<>();

        public Builder segment(Segment segment) {
            segments.add(segment);
            return this;
        }

        /**
         * Adds every consecutive pair of a polyline as a segment, tapering the radius linearly along the
         * whole run.
         */
        public Builder path(List<org.joml.Vector3f> points, float radiusStart, float radiusEnd) {
            for (int i = 0; i < points.size() - 1; i++) {
                final float t0 = (float) i / (points.size() - 1);
                final float t1 = (float) (i + 1) / (points.size() - 1);
                segments.add(new Segment(
                        points.get(i), points.get(i + 1),
                        radiusStart + (radiusEnd - radiusStart) * t0,
                        radiusStart + (radiusEnd - radiusStart) * t1
                ));
            }
            return this;
        }

        public Builder anchor(Anchor anchor) {
            anchors.add(anchor);
            return this;
        }

        public Skeleton build() {
            return new Skeleton(
                    Collections.unmodifiableList(new ArrayList<>(segments)),
                    Collections.unmodifiableList(new ArrayList<>(anchors))
            );
        }
    }
}
