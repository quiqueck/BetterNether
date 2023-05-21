package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class BlockFarmland extends BlockBase implements TagProvider, AddMineableAxe {
    public BlockFarmland() {
        super(Materials.makeNetherWood(MapColor.TERRACOTTA_LIGHT_GREEN));
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        blockTags.add(CommonBlockTags.SOUL_GROUND);
        blockTags.add(CommonBlockTags.NETHERRACK);
        blockTags.add(NetherTags.NETHER_FARMLAND);
    }
}
