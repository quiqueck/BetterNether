package org.betterx.betternether.world.features.configs;

import org.betterx.betternether.world.features.GloomwoodTreeFeature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

/**
 * {@link NaturalTreeConfiguration} plus the one thing that varies between the gloomwood's feature
 * instances: how often a tree comes out bleached.
 * <p>
 * A bleached gloomwood wears its palette the other way round - the pale leaves that normally only mark
 * the canopy's eyes cover the whole crown, and the eyes are cut from the ordinary dark ones - and it has
 * ancient debris buried under its roots. It is a per-tree roll rather than a per-feature switch, so a
 * feature carrying a chance still produces mostly ordinary trees.
 * <p>
 * The chance lives in the configuration rather than in the feature's code because it is a property of
 * <em>where</em> the tree grows: only the solitary placement in the gloomwood's open ground carries a
 * non-zero one, which is what keeps the bleached trees - and the debris - to the lone specimens standing
 * clear of the groves. Everything else uses {@link #natural()} and never produces one.
 */
public class GloomwoodTreeConfiguration extends NaturalTreeConfiguration {
    /**
     * Minimum separation between two gloomwoods, wider than the 7 the other trees use.
     * <p>
     * The species is what a gloomwood grove is made of, and at 7 its canopies interlocked into one
     * unbroken ceiling wherever the grove placement saturated - which it always does, because the count
     * is deliberately higher than the spacing can accept. Thinning the count alone does not fix that:
     * it makes groves rarer without making the trees inside one stand any further apart. This does.
     */
    public static final int SPACING = 9;

    /**
     * How often the solitary gloomwood comes out bleached - aimed at one bleached tree per biome.
     * <p>
     * Measured rather than guessed, and not by counting bleached trees: with the chance forced to 1.0 in
     * a test datapack every solitary tree buries a debris pocket, and those pockets are compact and one
     * per tree, so counting them counts the solitary trees exactly. That came to 12 across two gloomwood
     * patches, or six per biome. A third of six is two, which is where this is aimed: one per biome was
     * the original target, but at that rate a player crossing several gloomwoods was finding none at all
     * - the average is over biomes, and half of them round down to zero. Two is the smallest number that
     * makes the pale tree something you actually meet. (Counting bleached crowns instead of pockets does
     * not work: neighbouring canopies merge into one blob and undercount badly.)
     * <p>
     * Here rather than in the data provider, which is where 26.2 keeps it: 26.3 builds the configured
     * feature instance at the point the content key is declared (see {@code NetherTrees}), and the
     * provider only bootstraps that key, so the provider is no longer the place the number is used.
     */
    public static final float SOLITARY_BLEACHED_CHANCE = 0.33F;

    /**
     * Builds the {@code MapCodec} for the gloomwood, inlining its fields next to {@code "type"} the way
     * {@link NaturalTreeConfiguration#mapCodecFor} does for the trees that need no extra ones.
     * <p>
     * Named differently from the inherited one on purpose: both take a {@code Function}, so they erase to
     * the same signature and Java will not let a subclass declare the second without hiding the first.
     * <p>
     * {@code bleached_chance} is an {@code optionalFieldOf} with a default, which omits the field when it
     * holds that default - so an ordinary gloomwood serializes to exactly what it did before this
     * configuration existed.
     */
    public static MapCodec<GloomwoodTreeFeature> codecFor(
            Function<GloomwoodTreeConfiguration, GloomwoodTreeFeature> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        Codec.BOOL.fieldOf("natural").orElse(true).forGetter(o -> o.config.natural),
                        Codec.INT.fieldOf("distance").orElse(SPACING).forGetter(o -> o.config.distance),
                        Codec.FLOAT.optionalFieldOf("bleached_chance", 0.0F)
                                   .forGetter(GloomwoodTreeFeature::bleachedChance)
                )
                .apply(instance, (natural, distance, bleached) ->
                        factory.apply(new GloomwoodTreeConfiguration(natural, distance, bleached))));
    }

    private static final GloomwoodTreeConfiguration NATURAL = new GloomwoodTreeConfiguration(true, SPACING, 0.0F);

    /** Probability, per tree, that the canopy palette is inverted and the roots hide debris. */
    public final float bleachedChance;

    public GloomwoodTreeConfiguration(boolean natural, int distance, float bleachedChance) {
        super(natural, distance);
        this.bleachedChance = bleachedChance;
    }

    public static GloomwoodTreeConfiguration natural() {
        return NATURAL;
    }

    /** A naturally placed gloomwood that is bleached {@code bleachedChance} of the time. */
    public static GloomwoodTreeConfiguration bleachedSometimes(float bleachedChance) {
        return new GloomwoodTreeConfiguration(true, SPACING, bleachedChance);
    }
}
