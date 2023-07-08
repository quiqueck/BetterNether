package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourOre;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.bclib.interfaces.CustomItemProvider;
import org.betterx.bclib.interfaces.TagProvider;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;

public class BlockOre extends BaseOreBlock implements TagProvider, CustomItemProvider, BehaviourOre {
    public final boolean fireproof;

    public BlockOre(
            Supplier<Item> drop,
            int minCount,
            int maxCount,
            int experience,
            int miningLevel,
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
                miningLevel
        );
        this.fireproof = fireproof;
    }

    @Override
    public void addTags(List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags) {
        super.addTags(blockTags, itemTags);
        blockTags.add(CommonBlockTags.NETHERRACK);
        blockTags.add(CommonBlockTags.NETHER_ORES);
    }

    @Override
    public BlockItem getCustomItem(ResourceLocation blockID, Item.Properties settings) {
        if (fireproof) settings = settings.fireResistant();
        return new BlockItem(this, settings);
    }
}
