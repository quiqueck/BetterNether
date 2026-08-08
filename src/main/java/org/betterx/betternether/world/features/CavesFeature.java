package org.betterx.betternether.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.betternether.world.structures.StructureCaves;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class CavesFeature extends DefaultFeature {
    public static final MapCodec<CavesFeature> CODEC = MapCodec.unit(CavesFeature::new);

    @Override
    public MapCodec<CavesFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos worldPos
    ) {
        final int sx = (worldPos.getX() >> 4) << 4;
        final int sz = (worldPos.getZ() >> 4) << 4;

        caves.generate(
                level,
                new BlockPos(sx, 0, sz),
                random,
                generator.getGenDepth(),
                NetherThreadDataStorage.generatorForThread().context
        );
        return true;
    }

    private static StructureCaves caves;

    public static void onLoad(long seed) {
        caves = new StructureCaves(seed);
    }

    public static boolean isInCave(int x, int y, int z) {
        return caves.isInCave(x, y, z, NetherThreadDataStorage.generatorForThread().context);
    }
}
