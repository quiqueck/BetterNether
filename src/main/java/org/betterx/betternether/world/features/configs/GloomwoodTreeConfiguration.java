package org.betterx.betternether.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * {@link NaturalTreeConfiguration} plus the one thing that varies between the gloomwood's configured
 * features: how often a tree comes out bleached.
 * <p>
 * A bleached gloomwood wears its palette the other way round - the pale leaves that normally only mark
 * the canopy's eyes cover the whole crown, and the eyes are cut from the ordinary dark ones. It is a
 * per-tree roll rather than a per-feature switch, so a feature configured with a chance still produces
 * mostly ordinary trees.
 * <p>
 * The chance lives in the configuration rather than in the feature because it is a property of
 * <em>where</em> the tree grows: only the solitary placement in the gloomwood's open ground carries a
 * non-zero one (see {@code PlacedTreeFeatureDataProvider}), which is what keeps the bleached trees to
 * the lone specimens standing clear of the groves. Everything else - the groves, a player's sapling -
 * uses the default of zero and never produces one.
 */
public class GloomwoodTreeConfiguration extends NaturalTreeConfiguration {
    /**
     * Minimum separation between two gloomwoods, wider than the 7 the other trees use.
     * <p>
     * The species is what a gloomwood grove is made of, and at 7 its canopies interlocked into one
     * unbroken ceiling wherever the grove placement saturated - which it always does, because the count
     * is deliberately higher than the spacing can accept. Thinning the count alone does not fix that:
     * it makes groves rarer without making the trees inside one stand any further apart. This does.
     * <p>
     * Declared before the codec that defaults to it, or javac calls it an illegal forward reference.
     */
    public static final int SPACING = 9;

    public static final Codec<GloomwoodTreeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.BOOL.fieldOf("natural").orElse(true).forGetter(o -> o.natural),
                    Codec.INT.fieldOf("distance").orElse(SPACING).forGetter(o -> o.distance),
                    // optionalFieldOf with a default omits the field when it holds that default, so the
                    // ordinary gloomwood's json is byte-identical to what the shared configuration wrote.
                    Codec.FLOAT.optionalFieldOf("bleached_chance", 0.0F).forGetter(o -> o.bleachedChance)
            )
            .apply(instance, GloomwoodTreeConfiguration::new));

    private static final GloomwoodTreeConfiguration NATURAL = new GloomwoodTreeConfiguration(true, SPACING, 0.0F);
    private static final GloomwoodTreeConfiguration USER = new GloomwoodTreeConfiguration(false, SPACING, 0.0F);

    /** Probability, per tree, that the canopy palette is inverted. */
    public final float bleachedChance;

    public GloomwoodTreeConfiguration(boolean natural, int distance, float bleachedChance) {
        super(natural, distance);
        this.bleachedChance = bleachedChance;
    }

    public static GloomwoodTreeConfiguration natural() {
        return NATURAL;
    }

    public static GloomwoodTreeConfiguration userGrown() {
        return USER;
    }

    /** A naturally placed gloomwood that is bleached {@code bleachedChance} of the time. */
    public static GloomwoodTreeConfiguration bleachedSometimes(float bleachedChance) {
        return new GloomwoodTreeConfiguration(true, SPACING, bleachedChance);
    }
}
