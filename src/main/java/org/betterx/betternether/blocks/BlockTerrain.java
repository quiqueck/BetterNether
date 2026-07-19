package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.bonemeal.BonemealNyliumLike;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.Nullable;

public class BlockTerrain extends BlockBase implements BonemealNyliumLike {
    protected BonemealAPI.FeatureProvider vegetationFeature;
    public static final SoundType TERRAIN_SOUND = new SoundType(1.0F, 1.0F,
            SoundEvents.NETHERRACK_BREAK,
            SoundEvents.WART_BLOCK_STEP,
            SoundEvents.NETHERRACK_PLACE,
            SoundEvents.NETHERRACK_HIT,
            SoundEvents.NETHERRACK_FALL
    );

    public BlockTerrain(BlockBehaviour.Properties settings) {
        super(settings.sound(TERRAIN_SOUND).requiresCorrectToolForDrops());
        this.setDropItself(false);
    }

    public void setVegetationFeature(BonemealAPI.FeatureProvider vegetationFeature) {
        this.vegetationFeature = vegetationFeature;
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader blockGetter,
            BlockPos blockPos,
            BlockState blockState
    ) {
        return vegetationFeature != null && BonemealNyliumLike.super.isValidBonemealTarget(
                blockGetter,
                blockPos,
                blockState
        );
    }

    @Override
    public Block getHostBlock() {
        return this;
    }

    @Override
    public @Nullable Holder<? extends ConfiguredFeature<?, ?>> getCoverFeature() {
        return vegetationFeature.getFeature();
    }
}