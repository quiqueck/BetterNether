package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourOre;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneOreBlock extends RedStoneOreBlock implements RuntimeBlockModelProvider, BlockTagProvider, BehaviourOre, BlockLootProvider {
    private final int minCount;
    private final int maxCount;

    public RedstoneOreBlock() {
        super(BehaviourBuilders.createStone(MapColor.COLOR_RED)
                               .strength(3, 5)
                               .requiresCorrectToolForDrops()
                               .sound(SoundType.NETHERRACK)
                               .randomTicks()
                               .lightLevel(litBlockEmission(9)));

        this.minCount = 1;
        this.maxCount = 3;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int i) {
        return (blockState) -> {
            return (Boolean) blockState.getValue(BlockStateProperties.LIT) ? i : 0;
        };
    }

    @Override
    public BlockModel getItemModel(ResourceLocation resourceLocation) {
        return getBlockModel(resourceLocation, defaultBlockState());
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropOre(this, Items.REDSTONE, UniformGenerator.between(minCount, maxCount));
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.NETHERRACK, CommonBlockTags.NETHER_ORES);
    }
}
