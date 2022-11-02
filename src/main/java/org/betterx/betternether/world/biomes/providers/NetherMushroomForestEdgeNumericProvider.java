package org.betterx.betternether.world.biomes.providers;

import org.betterx.bclib.interfaces.NumericProvider;
import org.betterx.bclib.mixin.common.SurfaceRulesContextAccessor;
import org.betterx.betternether.MHelper;

import com.mojang.serialization.Codec;

public class NetherMushroomForestEdgeNumericProvider implements NumericProvider {
    public static final NetherMushroomForestEdgeNumericProvider DEFAULT = new NetherMushroomForestEdgeNumericProvider();
    public static final Codec<NetherMushroomForestEdgeNumericProvider> CODEC = Codec.BYTE.fieldOf(
            "nether_mushroom_forrest_edge").xmap((obj) -> DEFAULT, obj -> (byte) 0).codec();

    @Override
    public int getNumber(SurfaceRulesContextAccessor ctx) {
        return MHelper.RANDOM.nextInt(4) > 0 ? 0 : (MHelper.RANDOM.nextBoolean() ? 1 : 2);
    }

    @Override
    public Codec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    static {

    }
}
