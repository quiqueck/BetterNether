package org.betterx.betternether.world.tree.math;

/**
 * A solid, described by a signed distance function: negative inside, positive outside.
 *
 * <h2>Sign is authoritative, magnitude is not</h2>
 * Every rasteriser in this library asks only {@link #contains(float, float, float)}, i.e. the sign. That
 * matters because several of the combinators below are not distance-preserving:
 * {@link #subtract(Volume)} and {@link #intersect(Volume)} return the max of two distances, which
 * over-estimates near the seam, and {@link #scale(float, float, float)} with unequal factors is only a
 * bound. The sign stays exactly right in all three cases, so shapes built from them rasterise correctly;
 * only {@link #smoothUnion(Volume, float)} actually reads the magnitude, and it reads it close to the
 * surface where the error of the primitives is nil.
 * <p>
 * The practical consequence: build the smooth joins <em>before</em> subtracting, not after. See
 * {@link org.betterx.betternether.world.tree.canopy.GhostCanopy} for the shape that motivated the rule.
 */
@FunctionalInterface
public interface Volume {
    /**
     * Signed distance from {@code (x, y, z)} to this volume's surface, negative inside.
     */
    float distance(float x, float y, float z);

    /**
     * A volume that is empty everywhere. Useful as the identity of a {@code union} fold.
     */
    Volume EMPTY = (x, y, z) -> Float.MAX_VALUE;

    default boolean contains(float x, float y, float z) {
        return distance(x, y, z) < 0;
    }

    default Volume union(Volume other) {
        return (x, y, z) -> Math.min(this.distance(x, y, z), other.distance(x, y, z));
    }

    default Volume intersect(Volume other) {
        return (x, y, z) -> Math.max(this.distance(x, y, z), other.distance(x, y, z));
    }

    /**
     * This volume with {@code other} carved out of it.
     */
    default Volume subtract(Volume other) {
        return (x, y, z) -> Math.max(this.distance(x, y, z), -other.distance(x, y, z));
    }

    /**
     * Union with a rounded seam of width {@code k} instead of a crease.
     * <p>
     * The polynomial smooth-min from the standard SDF toolbox. {@code k} is in blocks; 0 degenerates to
     * a plain {@link #union(Volume)}.
     */
    default Volume smoothUnion(Volume other, float k) {
        if (k <= 0) return union(other);
        return (x, y, z) -> {
            final float a = this.distance(x, y, z);
            final float b = other.distance(x, y, z);
            final float h = Math.max(0, Math.min(1, 0.5F + 0.5F * (b - a) / k));
            return b + (a - b) * h - k * h * (1 - h);
        };
    }

    default Volume translate(float dx, float dy, float dz) {
        return (x, y, z) -> this.distance(x - dx, y - dy, z - dz);
    }

    /**
     * Non-uniform scale. See the class note: the result bounds the true distance rather than equalling
     * it, which is why the scale factors are also divided out of the returned value - without that the
     * bound is not even conservative.
     */
    default Volume scale(float sx, float sy, float sz) {
        final float min = Math.min(sx, Math.min(sy, sz));
        return (x, y, z) -> this.distance(x / sx, y / sy, z / sz) * min;
    }

    /**
     * Grows the volume by {@code r} in every direction (negative shrinks it).
     */
    default Volume expand(float r) {
        return (x, y, z) -> this.distance(x, y, z) - r;
    }

    /**
     * Perturbs the surface by an additive field - the "crackly bark" operator.
     * <p>
     * The displacement is added to the distance, so a positive value pulls the surface <em>in</em>.
     * Keep the amplitude well below the smallest radius in the shape or the solid breaks into islands.
     */
    default Volume displace(Displacement d) {
        return (x, y, z) -> this.distance(x, y, z) + d.at(x, y, z);
    }

    /**
     * A scalar field over space, for {@link #displace(Displacement)}.
     */
    @FunctionalInterface
    interface Displacement {
        float at(float x, float y, float z);
    }
}
