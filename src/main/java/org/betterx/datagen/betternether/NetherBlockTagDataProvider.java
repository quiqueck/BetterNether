package org.betterx.datagen.betternether;

import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NetherBlockTagDataProvider extends WoverTagProvider.ForBlocks {
    public NetherBlockTagDataProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        context.add(NetherTags.NETHER_SAND, Blocks.SOUL_SAND);
        context.add(BlockTags.BEACON_BASE_BLOCKS, NetherMetalBlocks.NETHER_RUBY_BLOCK);

        context.add(
                NetherTags.FIREFLY_FLOWERS,
                NetherPlantBlocks.NETHER_GRASS,
                NetherPlantBlocks.SOUL_GRASS,
                NetherPlantBlocks.SWAMP_GRASS,
                NetherCropBlocks.BLACK_APPLE,
                NetherPlantBlocks.MAGMA_FLOWER,
                NetherVineBlocks.SOUL_VEIN,
                NetherWoodBlocks.MAT_REED.getStem(),
                NetherPlantBlocks.INK_BUSH,
                NetherPlantBlocks.INK_BUSH_SEED,
                NetherPlantBlocks.POTTED_PLANT,
                Blocks.NETHER_WART
        );

        // Obsidian Breaker speeds up obsidian and nothing else, and "is obsidian" is decided in exactly
        // one place: NetherMaterial.obsidian() (and its portal-frame variant), which every obsidian
        // block routes through and which adds CommonBlockTags.IS_OBSIDIAN. Keying off that tag rather
        // than enumerating blocks here means a new obsidian variant is covered the moment it declares
        // its material - and a block that forgets to is a bug in that block, not something to paper
        // over with an entry in this list.
        //
        // This used to also pull in nether stones, nether ores and the portal-frame tag, which put
        // netherrack, every nether ore and the glass panes under the enchantment.
        context.add(
                NetherTags.OBSIDIAN_BREAKER_MINEABLE,
                CommonBlockTags.IS_OBSIDIAN
        );
        context.add(
                CommonBlockTags.IS_OBSIDIAN,
                NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN,
                NetherObsidianBlocks.WEEPING_OBSIDIAN,
                NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN
        );
    }
}
