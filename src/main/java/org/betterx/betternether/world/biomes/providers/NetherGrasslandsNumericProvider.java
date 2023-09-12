package org.betterx.betternether.world.biomes.providers;


import org.betterx.betternether.MHelper;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;

public class NetherGrasslandsNumericProvider implements NumericProvider {
    public static final NetherGrasslandsNumericProvider DEFAULT = new NetherGrasslandsNumericProvider();
    public static final Codec<NetherGrasslandsNumericProvider> CODEC = Codec.BYTE.fieldOf("nether_grasslands")
                                                                                 .xmap(
                                                                                         (obj) -> DEFAULT,
                                                                                         obj -> (byte) 0
                                                                                 )
                                                                                 .codec();

    @Override
    public int getNumber(SurfaceRulesContext ctx) {
        final int depth = ctx.getStoneDepthAbove();
        if (depth <= 1) return MHelper.RANDOM.nextInt(3);
        if (depth <= MHelper.RANDOM.nextInt(3) + 1) return 0;
        return 2;
    }

    @Override
    public Codec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

}
