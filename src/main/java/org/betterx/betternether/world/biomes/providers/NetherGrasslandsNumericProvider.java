package org.betterx.betternether.world.biomes.providers;


import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import net.minecraft.util.RandomSource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class NetherGrasslandsNumericProvider implements NumericProvider {
    public static final NetherGrasslandsNumericProvider DEFAULT = new NetherGrasslandsNumericProvider();
    public static final MapCodec<NetherGrasslandsNumericProvider> CODEC = Codec.BYTE
            .fieldOf("nether_grasslands")
            .xmap(
                    (obj) -> DEFAULT,
                    obj -> (byte) 0
            );

    // An arbitrary but fixed base seed, so that this provider and the mushroom-forest-edge one do not walk
    // the same sequence over the same positions.
    private static final int SEED = 0x6E677261;

    @Override
    public int getNumber(SurfaceRulesContext ctx) {
        // Seeded from the block position. Both draws used to come from MHelper.RANDOM, which is
        // ThreadLocalRandom: it has no seed, cannot be given one, and hands a different stream to every
        // thread and every JVM start - so this surface could never be built twice from the same world seed.
        final RandomSource random = RandomSource.create(MathHelper.getSeed(
                SEED,
                ctx.getBlockX(),
                ctx.getBlockY(),
                ctx.getBlockZ()
        ));

        final int depth = ctx.getStoneDepthAbove();
        if (depth <= 1) return random.nextInt(3);
        if (depth <= random.nextInt(3) + 1) return 0;
        return 2;
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

}
