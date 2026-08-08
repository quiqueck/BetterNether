package org.betterx.betternether.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

/**
 * 26.3 deleted the {@code net.minecraft.world.level.levelgen.feature.configurations} package, including the
 * empty {@code FeatureConfiguration} marker this class used to implement. Nothing consumed the marker, so
 * the {@code implements} clause is simply dropped and this stays a plain value object.
 * <p>
 * What did change is where the value lives: a {@code Feature} now carries its own configuration, so instead
 * of a standalone {@code Codec<NaturalTreeConfiguration>} attached to a {@code ConfiguredFeature}, the two
 * fields are serialized directly into the owning feature's {@code MapCodec} - see
 * {@link #mapCodecFor(Function)}. The field names ({@code natural}, {@code distance}) and their defaults are
 * unchanged; they just sit next to {@code "type"} now instead of inside a {@code "config"} object.
 */
public class NaturalTreeConfiguration {
    /**
     * Builds the {@code MapCodec} for a feature that is configured by a {@link NaturalTreeConfiguration}.
     *
     * @param factory creates the feature from a decoded configuration
     * @param <T>     the feature type
     * @return a {@code MapCodec} with the {@code natural} and {@code distance} fields inlined
     */
    public static <T extends org.betterx.betternether.world.features.NonOverlappingFeature>
    com.mojang.serialization.MapCodec<T> mapCodecFor(Function<NaturalTreeConfiguration, T> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        Codec.BOOL.fieldOf("natural").orElse(true).forGetter(o -> o.config.natural),
                        Codec.INT.fieldOf("distance").orElse(7).forGetter(o -> o.config.distance)
                )
                .apply(instance, (natural, distance) -> factory.apply(new NaturalTreeConfiguration(
                        natural,
                        distance
                ))));
    }

    private static final NaturalTreeConfiguration NATURAL = new NaturalTreeConfiguration(true, 7);
    private static final NaturalTreeConfiguration USER = new NaturalTreeConfiguration(false, 7);
    private static final NaturalTreeConfiguration NATURAL_LARGE = new NaturalTreeConfiguration(true, 13);
    private static final NaturalTreeConfiguration USER_LARGE = new NaturalTreeConfiguration(false, 13);
    public final boolean natural;
    public final int distance;
    public final int manDist;

    public NaturalTreeConfiguration(boolean natural, int distance) {
        this.natural = natural;
        this.distance = distance;
        this.manDist = (int) Math.ceil(distance * 1.5);
    }

    public static NaturalTreeConfiguration natural() {
        return NATURAL;
    }

    public static NaturalTreeConfiguration userGrown() {
        return USER;
    }

    public static NaturalTreeConfiguration naturalLarge() {
        return NATURAL_LARGE;
    }

    public static NaturalTreeConfiguration userGrownLarge() {
        return USER_LARGE;
    }
}
