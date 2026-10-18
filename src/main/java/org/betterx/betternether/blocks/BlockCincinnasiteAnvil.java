package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.bclib.util.LootUtil;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import com.google.common.collect.Lists;

import java.util.List;

public class BlockCincinnasiteAnvil extends AnvilBlock implements BehaviourMetal, TagProvider {
    public BlockCincinnasiteAnvil() {
        super(FabricBlockSettings.copy(NetherBlocks.CINCINNASITE_BLOCK).noOcclusion());
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        // the block tag is added on registration (NetherBlocks), keep the item in the vanilla anvil tag as well
        itemTags.add(ItemTags.ANVIL);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (LootUtil.isCorrectTool(this, state, tool)) {
            return Lists.newArrayList(new ItemStack(this));
        } else {
            return Lists.newArrayList();
        }
    }
}
