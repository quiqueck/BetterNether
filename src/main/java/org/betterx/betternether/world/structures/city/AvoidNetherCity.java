package org.betterx.betternether.world.structures.city;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;

import org.jetbrains.annotations.NotNull;

/**
 * Drops a placement whose feature could reach into a nether city ({@code betternether:avoid_nether_city}).
 * <p>
 * For features that cannot check this themselves, such as the {@code wover:template} forest litter of the
 * Upside Down Forest. Its fallen anchor trees, stumps and roots are placed in {@code SURFACE_STRUCTURES},
 * before the city, so wherever a neighbouring chunk's city already stood they landed in its rooms and
 * streets or hung in its cave - on eight Upside Down Forest cities, 793 anchor tree blocks inside the
 * buildings, all of them litter. See {@link CityStructure#buildingsWithin} and
 * {@link CityStructure#cityWithin}.
 */
public class AvoidNetherCity implements PlacementFilter {
    public static final MapCodec<AvoidNetherCity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
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
    public boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos origin) {
        return includeCave
                ? !CityStructure.cityWithin(context.getLevel(), origin, reach)
                : !CityStructure.buildingsWithin(context.getLevel(), origin, reach);
    }

    @Override
    public @NotNull MapCodec<AvoidNetherCity> codec() {
        return CODEC;
    }
}
