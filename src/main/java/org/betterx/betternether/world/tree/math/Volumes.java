package org.betterx.betternether.world.tree.math;

import org.joml.Vector3f;

/**
 * The primitive {@link Volume}s. All are centred on the origin and Y-up unless stated otherwise;
 * position them with {@link Volume#translate(float, float, float)}.
 */
public final class Volumes {
    private Volumes() {
    }

    public static Volume sphere(float radius) {
        return (x, y, z) -> (float) Math.sqrt(x * x + y * y + z * z) - radius;
    }

    /**
     * An axis-aligned ellipsoid with the given semi-axes.
     */
    public static Volume ellipsoid(float rx, float ry, float rz) {
        // Not sphere().scale(): the scaled-sphere bound is driven by the *smallest* semi-axis, which for
        // the flat, wide ellipsoid a canopy skirt wants is far too pessimistic. This is the standard
        // gradient-corrected ellipsoid estimate, which stays useful for smooth joins.
        return (x, y, z) -> {
            final float nx = x / rx;
            final float ny = y / ry;
            final float nz = z / rz;
            final float k0 = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (k0 == 0) return -Math.min(rx, Math.min(ry, rz));
            final float mx = x / (rx * rx);
            final float my = y / (ry * ry);
            final float mz = z / (rz * rz);
            final float k1 = (float) Math.sqrt(mx * mx + my * my + mz * mz);
            return k0 * (k0 - 1) / k1;
        };
    }

    /**
     * A flat-capped cylinder about the Y axis, extending {@code halfHeight} above and below the origin.
     */
    public static Volume cylinder(float radius, float halfHeight) {
        return (x, y, z) -> {
            final float dx = (float) Math.sqrt(x * x + z * z) - radius;
            final float dy = Math.abs(y) - halfHeight;
            final float outside = (float) Math.sqrt(
                    Math.max(dx, 0) * Math.max(dx, 0) + Math.max(dy, 0) * Math.max(dy, 0)
            );
            return Math.min(Math.max(dx, dy), 0) + outside;
        };
    }

    /**
     * A round-capped cylinder between {@code a} and {@code b}, tapering from {@code r0} to {@code r1}.
     * <p>
     * This is the workhorse for trunks and branches: a skeleton segment is exactly one of these.
     */
    public static Volume capsule(Vector3f a, Vector3f b, float r0, float r1) {
        final float bax = b.x - a.x;
        final float bay = b.y - a.y;
        final float baz = b.z - a.z;
        final float baLenSq = bax * bax + bay * bay + baz * baz;
        if (baLenSq < 1.0e-6F) return sphere(Math.max(r0, r1)).translate(a.x, a.y, a.z);

        return (x, y, z) -> {
            final float pax = x - a.x;
            final float pay = y - a.y;
            final float paz = z - a.z;
            float h = (pax * bax + pay * bay + paz * baz) / baLenSq;
            h = Math.max(0, Math.min(1, h));
            final float dx = pax - bax * h;
            final float dy = pay - bay * h;
            final float dz = paz - baz * h;
            return (float) Math.sqrt(dx * dx + dy * dy + dz * dz) - (r0 + (r1 - r0) * h);
        };
    }

    /**
     * An axis-aligned box with the given half-extents.
     */
    public static Volume box(float hx, float hy, float hz) {
        return (x, y, z) -> {
            final float dx = Math.abs(x) - hx;
            final float dy = Math.abs(y) - hy;
            final float dz = Math.abs(z) - hz;
            final float outside = (float) Math.sqrt(
                    Math.max(dx, 0) * Math.max(dx, 0)
                            + Math.max(dy, 0) * Math.max(dy, 0)
                            + Math.max(dz, 0) * Math.max(dz, 0)
            );
            return Math.min(Math.max(dx, Math.max(dy, dz)), 0) + outside;
        };
    }

    /**
     * The half-space {@code y < height}. Handy for slicing a canopy off flat.
     */
    public static Volume below(float height) {
        return (x, y, z) -> y - height;
    }
}
