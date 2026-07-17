package org.betterx.betternether.blocks;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collections;
import java.util.List;

public class BNBoneBlock extends BlockBase {
    public BNBoneBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
