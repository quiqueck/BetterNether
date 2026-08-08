package org.betterx.betternether.world.features;

import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * 26.3 collapsed {@code ConfiguredFeature<FC, F>} into {@link Feature}: {@link Feature} is a non-generic
 * <em>interface</em> that carries its own configuration and its own {@code MapCodec}, and the whole
 * {@code net.minecraft.world.level.levelgen.feature.configurations} package (including
 * {@code FeatureConfiguration} and {@code NoneFeatureConfiguration}) is gone. {@code FeaturePlaceContext}
 * went with it: {@link Feature#place} now receives level, generator, random source and origin directly.
 * <p>
 * The {@code <FC>} type parameter therefore names nothing any more and is dropped, as is the
 * {@code Codec<FC>} constructor argument - every concrete subclass supplies its own {@code codec()}.
 */
public abstract class ContextFeature implements Feature {
    @Override
    public final boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        return place(
                level,
                origin,
                random,
                generator.getGenDepth(),
                NetherThreadDataStorage.generatorForThread().context
        );
    }

    protected abstract boolean place(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    );
}
