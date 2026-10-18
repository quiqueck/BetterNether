package org.betterx.betternether.world.structures.city;

import org.betterx.betternether.registry.NetherFeatures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * Drops a placement whose feature could reach into a nether city ({@code betternether:avoid_nether_city}).
 * <p>
 * For features that cannot check this themselves, such as the template based forest litter of the
 * Upside Down Forest. Its fallen anchor trees, stumps and roots are placed in the same step as the city,
 * so wherever a neighbouring chunk's city already stood they landed in its rooms and streets or hung in
 * its cave. See {@link CityStructure#buildingsWithin} and {@link CityStructure#cityWithin}.
 */
public class AvoidNetherCity extends PlacementFilter {
    public static final Codec<AvoidNetherCity> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.intRange(0, 64).fieldOf("reach").forGetter(filter -> filter.reach),
                    Codec.BOOL.optionalFieldOf("include_cave", false).forGetter(filter -> filter.includeCave)
            )
            .apply(instance, AvoidNetherCity::new)
    );

    private final int reach;
    private final boolean includeCave;

    /**
     * @param reach       how far sideways the feature can write from the position it is given
     * @param includeCave {@code true} to keep out of the city's cave as well, not only its buildings
     */
    public AvoidNetherCity(int reach, boolean includeCave) {
        this.reach = reach;
        this.includeCave = includeCave;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos origin) {
        return includeCave
                ? !CityStructure.cityWithin(context.getLevel(), origin, reach)
                : !CityStructure.buildingsWithin(context.getLevel(), origin, reach);
    }

    @Override
    public PlacementModifierType<?> type() {
        return NetherFeatures.AVOID_NETHER_CITY;
    }
}
