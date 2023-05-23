package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

public class BlockReedsBlock extends BNPillar.Wood implements TagProvider {
    public BlockReedsBlock() {
        super(Materials.makeWood(MaterialColor.COLOR_CYAN));
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        blockTags.add(BlockTags.PLANKS);
    }
}
