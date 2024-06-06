package org.betterx.betternether.world.biomes.providers;


import org.betterx.betternether.MHelper;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class NetherMushroomForestEdgeNumericProvider implements NumericProvider {
    public static final NetherMushroomForestEdgeNumericProvider DEFAULT = new NetherMushroomForestEdgeNumericProvider();
    public static final MapCodec<NetherMushroomForestEdgeNumericProvider> CODEC = Codec.BYTE.fieldOf(
            "nether_mushroom_forrest_edge").xmap((obj) -> DEFAULT, obj -> (byte) 0);

    @Override
    public int getNumber(SurfaceRulesContext ctx) {
        return MHelper.RANDOM.nextInt(4) > 0 ? 0 : (MHelper.RANDOM.nextBoolean() ? 1 : 2);
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }
}
