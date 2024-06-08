package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class BlockReedsBlock extends BNPillar.Wood implements BlockTagProvider {
    public BlockReedsBlock() {
        super(Materials.makeNetherWood(MapColor.COLOR_CYAN).strength(1));
    }

    @Override
    public void registerItemTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, BlockTags.PLANKS);
    }
}
