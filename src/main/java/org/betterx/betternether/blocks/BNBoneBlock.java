package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.Collections;
import java.util.List;

public class BNBoneBlock extends BlockBase implements BehaviourStone {
    public BNBoneBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BONE_BLOCK));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
