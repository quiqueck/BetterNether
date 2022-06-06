package org.betterx.betternether.world.features.configs;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NaturalTreeConfiguration implements FeatureConfiguration {
    public static final Codec<NaturalTreeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.BOOL.fieldOf("natural").orElse(true).forGetter(o -> o.natural),
                    Codec.INT.fieldOf("distance").orElse(7).forGetter(o -> o.distance)
            )
            .apply(instance, NaturalTreeConfiguration::new));
    private static final NaturalTreeConfiguration NATURAL = new NaturalTreeConfiguration(true, 7);
    private static final NaturalTreeConfiguration USER = new NaturalTreeConfiguration(false, 7);
    public final boolean natural;
    public final int distance;
    public final int manDist;

    private NaturalTreeConfiguration(boolean natural, int distance) {
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
}
