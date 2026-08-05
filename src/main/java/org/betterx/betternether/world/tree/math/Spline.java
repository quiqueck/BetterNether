package org.betterx.betternether.world.tree.math;

import net.minecraft.util.RandomSource;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Polyline helpers for building trunk and branch paths.
 * <p>
 * A "spline" here is just an ordered {@link List} of points in tree-local space; the segments between
 * consecutive points become capsules. Nothing in this class touches a level.
 */
public final class Spline {
    private Spline() {
    }

    /**
     * A straight run of {@code points} samples from {@code from} to {@code to}, inclusive of both ends.
     */
    public static List<Vector3f> line(Vector3f from, Vector3f to, int points) {
        final List<Vector3f> result = new ArrayList<>(Math.max(points, 2));
        final int last = Math.max(points, 2) - 1;
        for (int i = 0; i <= last; i++) {
            final float t = (float) i / last;
            result.add(new Vector3f(
                    from.x + (to.x - from.x) * t,
                    from.y + (to.y - from.y) * t,
                    from.z + (to.z - from.z) * t
            ));
        }
        return result;
    }

    /**
     * Subdivides a polyline with a centripetal-ish Catmull-Rom pass, so that a path jittered at a few
     * control points reads as a curve rather than a chain of kinks.
     *
     * @param subdivisions samples generated per input segment; 1 returns the input unchanged
     */
    public static List<Vector3f> smooth(List<Vector3f> control, int subdivisions) {
        if (control.size() < 3 || subdivisions <= 1) return new ArrayList<>(control);

        final List<Vector3f> result = new ArrayList<>(control.size() * subdivisions);
        for (int i = 0; i < control.size() - 1; i++) {
            // Endpoints are duplicated rather than extrapolated, which keeps the curve from overshooting
            // past the trunk base or the branch tip - both of which are attachment points that must stay
            // exactly where the caller put them.
            final Vector3f p0 = control.get(Math.max(i - 1, 0));
            final Vector3f p1 = control.get(i);
            final Vector3f p2 = control.get(i + 1);
            final Vector3f p3 = control.get(Math.min(i + 2, control.size() - 1));
            for (int s = 0; s < subdivisions; s++) {
                result.add(catmullRom(p0, p1, p2, p3, (float) s / subdivisions));
            }
        }
        result.add(new Vector3f(control.get(control.size() - 1)));
        return result;
    }

    private static Vector3f catmullRom(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        final float t2 = t * t;
        final float t3 = t2 * t;
        return new Vector3f(
                catmullRom(p0.x, p1.x, p2.x, p3.x, t, t2, t3),
                catmullRom(p0.y, p1.y, p2.y, p3.y, t, t2, t3),
                catmullRom(p0.z, p1.z, p2.z, p3.z, t, t2, t3)
        );
    }

    private static float catmullRom(float a, float b, float c, float d, float t, float t2, float t3) {
        return 0.5F * ((2 * b) + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2 + (-a + 3 * b - 3 * c + d) * t3);
    }

    /**
     * Randomly offsets the interior points of a polyline, leaving both endpoints untouched.
     * <p>
     * The offset ramps in and out with a sine window, so the displacement is largest in the middle of
     * the run: a trunk that leaned as hard at its base as in its middle would not look rooted.
     */
    public static void jitter(List<Vector3f> spline, RandomSource random, float amount) {
        if (spline.size() < 3 || amount <= 0) return;
        for (int i = 1; i < spline.size() - 1; i++) {
            final float window = (float) Math.sin(Math.PI * i / (spline.size() - 1));
            final float scale = amount * window;
            spline.get(i).add(
                    (random.nextFloat() * 2 - 1) * scale,
                    (random.nextFloat() * 2 - 1) * scale * 0.35F,
                    (random.nextFloat() * 2 - 1) * scale
            );
        }
    }
}
