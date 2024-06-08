package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class BlockFarmland extends BlockBase implements BlockTagProvider, AddMineableAxe {
    public BlockFarmland() {
        super(Materials.makeNetherWood(MapColor.TERRACOTTA_LIGHT_GREEN));
    }

    @Override
    public void registerItemTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.SOUL_GROUND, CommonBlockTags.NETHERRACK, NetherTags.NETHER_FARMLAND);
    }
}
