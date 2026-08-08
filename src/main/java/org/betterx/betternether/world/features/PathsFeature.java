package org.betterx.betternether.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.betternether.world.structures.StructurePath;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PathsFeature extends DefaultFeature {
    public static final MapCodec<PathsFeature> CODEC = MapCodec.unit(PathsFeature::new);

    @Override
    public MapCodec<PathsFeature> codec() {
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

        paths.generate(
                level,
                new BlockPos(sx, 0, sz),
                random,
                generator.getGenDepth(),
                NetherThreadDataStorage.generatorForThread().context
        );
        return true;
    }

    private static StructurePath paths;

    public static void onLoad(long seed) {
        paths = new StructurePath(seed + 1);
    }
}
