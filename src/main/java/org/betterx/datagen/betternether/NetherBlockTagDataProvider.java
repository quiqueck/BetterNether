package org.betterx.datagen.betternether;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NetherBlockTagDataProvider extends WoverTagProvider.ForBlocks {
    public NetherBlockTagDataProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Block> context) {
        context.add(NetherTags.NETHER_SAND, Blocks.SOUL_SAND);
        context.add(BlockTags.BEACON_BASE_BLOCKS, NetherBlocks.NETHER_RUBY_BLOCK);

        context.add(
                NetherTags.FIREFLY_FLOWERS,
                NetherBlocks.NETHER_GRASS,
                NetherBlocks.SOUL_GRASS,
                NetherBlocks.SWAMP_GRASS,
                NetherBlocks.BLACK_APPLE,
                NetherBlocks.MAGMA_FLOWER,
                NetherBlocks.SOUL_VEIN,
                NetherBlocks.MAT_REED.getStem(),
                NetherBlocks.INK_BUSH,
                NetherBlocks.INK_BUSH_SEED,
                NetherBlocks.POTTED_PLANT,
                Blocks.NETHER_WART
        );

        context.add(
                NetherTags.OBSIDIAN_BREAKER_MINEABLE,
                CommonBlockTags.NETHER_STONES,
                CommonBlockTags.NETHER_ORES,
                CommonBlockTags.NETHER_PORTAL_FRAME,
                CommonBlockTags.IS_OBSIDIAN
        );
        context.add(
                CommonBlockTags.IS_OBSIDIAN,
                NetherBlocks.BLUE_CRYING_OBSIDIAN,
                NetherBlocks.WEEPING_OBSIDIAN,
                NetherBlocks.BLUE_WEEPING_OBSIDIAN
        );

        context.add(
                BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
                CommonBlockTags.NETHER_PORTAL_FRAME
        );
        org.betterx.datagen.bclib.worldgen.BlockTagProvider.processCommonBlockTags(context, modCore);
    }
}
