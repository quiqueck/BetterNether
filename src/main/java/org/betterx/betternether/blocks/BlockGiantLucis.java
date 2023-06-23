package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.MHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import com.google.common.collect.Lists;

import java.util.List;

public class BlockGiantLucis extends HugeMushroomBlock implements AddMineableAxe {
    public BlockGiantLucis() {
        super(BehaviourBuilders
                .createWalkablePlant(MapColor.COLOR_YELLOW)
                .requiresCorrectToolForDrops()
                .lightLevel((bs) -> 15)
                .sound(SoundType.WOOD)
                .strength(1F));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0)
            return Lists.newArrayList(new ItemStack(this.asItem()));
        return Lists.newArrayList(
                new ItemStack(NetherBlocks.LUCIS_SPORE, MHelper.randRange(0, 1, MHelper.RANDOM)),
                new ItemStack(NetherItems.GLOWSTONE_PILE, MHelper.randRange(0, 2, MHelper.RANDOM))
        );
    }
}
