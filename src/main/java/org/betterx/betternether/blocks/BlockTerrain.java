package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.bonemeal.BonemealNyliumLike;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockTerrain extends BlockBase implements BlockTagProvider, BonemealNyliumLike, BehaviourStone, BlockLootProvider {
    protected BonemealAPI.FeatureProvider vegetationFeature;
    public static final SoundType TERRAIN_SOUND = new SoundType(1.0F, 1.0F,
            SoundEvents.NETHERRACK_BREAK,
            SoundEvents.WART_BLOCK_STEP,
            SoundEvents.NETHERRACK_PLACE,
            SoundEvents.NETHERRACK_HIT,
            SoundEvents.NETHERRACK_FALL
    );

    public BlockTerrain() {
        super(FabricBlockSettings.copyOf(Blocks.NETHERRACK).sounds(TERRAIN_SOUND).requiresTool());
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

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this, Blocks.NETHERRACK, ConstantValue.exactly(1));
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.NETHERRACK, CommonBlockTags.NETHER_STONES);
    }
}