package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourOre;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.bclib.interfaces.BlockModelProvider;
import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.bclib.util.LegacyTiers;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.function.ToIntFunction;

public class RedstoneOreBlock extends RedStoneOreBlock implements BlockModelProvider, TagProvider, BehaviourOre {
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
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return BaseOreBlock.getDroppedItems(
                this,
                Items.REDSTONE,
                maxCount,
                minCount,
                LegacyTiers.IRON.level(),
                state,
                builder
        );
    }

    @Override
    public BlockModel getItemModel(ResourceLocation resourceLocation) {
        return getBlockModel(resourceLocation, defaultBlockState());
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        blockTags.add(CommonBlockTags.NETHERRACK);
        blockTags.add(CommonBlockTags.NETHER_ORES);
    }
}
