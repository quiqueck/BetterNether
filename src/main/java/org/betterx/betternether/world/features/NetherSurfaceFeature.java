package org.betterx.betternether.world.features;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Optional;

/**
 * 26.3 made {@link Feature} a non-generic interface that carries its own configuration and deleted
 * {@code FeaturePlaceContext} along with the {@code ...feature.configurations} package. The helpers that
 * used to take the context now take the pieces of it they actually used.
 */
public abstract class NetherSurfaceFeature implements Feature {

    protected boolean isValidSurface(BlockState state) {
        return BlocksHelper.isNetherGround(state);
    }

    protected void generate(
            BlockPos centerPos,
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random
    ) {
        generate(
                level,
                centerPos,
                random,
                generator.getGenDepth(),
                NetherThreadDataStorage.generatorForThread().context
        );
    }

    protected abstract void generate(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    );

    protected int minHeight(ChunkGenerator generator) {
        return generator.getSeaLevel();
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        Optional<BlockPos> pos = org.betterx.bclib.util.BlocksHelper.findSurfaceBelow(
                level,
                origin,
                minHeight(generator),
                this::isValidSurface
        );
        if (pos.isPresent()) {
            generate(pos.get(), level, generator, random);
            return true;
        }

        return false;
    }
}
