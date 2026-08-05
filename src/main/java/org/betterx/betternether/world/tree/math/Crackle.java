package org.betterx.betternether.world.tree.math;

import de.ambertation.wover.math.api.noise.OpenSimplexNoise;

/**
 * Surface-perturbation fields for {@link Volume#displace(Volume.Displacement)}.
 *
 * <h2>Amplitude discipline</h2>
 * A displacement is added to a signed distance, so an amplitude of {@code a} moves the surface by up to
 * {@code a} blocks inwards and outwards. Push it past the shape's smallest radius and the solid stops
 * being a solid: a trunk of radius 1.2 with amplitude 1.0 generates a column of disconnected log
 * fragments, which is exactly the floating-block failure this library exists to avoid. Keep the
 * amplitude below half the smallest radius the field is applied to; the factories below do not enforce
 * that because they do not know the shape, but every call site should be able to justify its number.
 */
public final class Crackle {
    private Crackle() {
    }

    /**
     * Fissured bark: ridged noise, which pinches into narrow creases rather than rolling dunes.
     *
     * @param amplitude how far the surface moves, in blocks
     * @param scale     feature size - larger values give finer detail
     */
    public static Volume.Displacement bark(long seed, float amplitude, float scale) {
        final OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        return (x, y, z) -> {
            // 1 - |n| turns the smooth field into ridges; the vertical axis is sampled at a third of the
            // horizontal rate so the creases run *along* the trunk like real bark rather than banding it.
            final double n = noise.eval(x * scale, y * scale * 0.33, z * scale);
            return amplitude * (float) (1.0 - Math.abs(n)) - amplitude * 0.5F;
        };
    }

    /**
     * A soft, isotropic wobble - for canopy hems, where the goal is "torn" rather than "fissured".
     */
    public static Volume.Displacement wobble(long seed, float amplitude, float scale) {
        final OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        return (x, y, z) -> amplitude * (float) noise.eval(x * scale, y * scale, z * scale);
    }
}
