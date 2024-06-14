package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourOre;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.block.api.CustomBlockItemProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class BlockOre extends BaseOreBlock implements BlockTagProvider, CustomBlockItemProvider, BehaviourOre {
    public final boolean fireproof;

    public BlockOre(
            Supplier<Item> drop,
            int minCount,
            int maxCount,
            int experience,
            TagKey<Block> miningTag,
            boolean fireproof
    ) {
        super(
                BehaviourBuilders
                        .createStone(MapColor.COLOR_RED)
                        .strength(3, 5)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.NETHERRACK),
                drop,
                minCount,
                maxCount,
                experience,
                miningTag
        );
        this.fireproof = fireproof;
    }

    @Override
    public BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings) {
        if (fireproof) settings = settings.fireResistant();
        return new BlockItem(this, settings);
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        super.registerBlockTags(location, context);
        context.add(this, CommonBlockTags.NETHERRACK, CommonBlockTags.NETHER_ORES);
    }
}
