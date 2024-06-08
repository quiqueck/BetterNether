package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.bonemeal.BonemealNyliumLike;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockTerrain extends BlockBase implements TagProvider, BonemealNyliumLike, BehaviourStone, BlockLootProvider {
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
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        blockTags.add(CommonBlockTags.NETHERRACK);
        blockTags.add(org.betterx.worlds.together.tag.v3.CommonBlockTags.NETHER_STONES);
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
}