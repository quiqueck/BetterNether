package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.wover.block.api.CustomBlockItemProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockNetherRuby extends BlockBase implements CustomBlockItemProvider, BehaviourStone {
    public BlockNetherRuby() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK));
    }

    @Override
    public BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings) {
        return new BlockItem(this, settings.fireResistant());
    }
}
