package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseDoorBlock;
import org.betterx.bclib.interfaces.TagProvider;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.List;

public class BNWoodlikeDoor extends BaseDoorBlock implements TagProvider {

    public BNWoodlikeDoor(Block source, WoodType type) {
        super(source, type.setType());
    }

    public BNWoodlikeDoor(Properties properties, WoodType type) {
        super(properties, type.setType());
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        blockTags.add(BlockTags.WOODEN_DOORS);
        itemTags.add(ItemTags.WOODEN_DOORS);
    }
}
