package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NetherTags {
    public static final TagKey<Biome> BETTER_NETHER_DECORATIONS = TagManager.BIOMES.makeStructureTag(
            BetterNether.C.modId,
            "nether_decorations"
    );

    public static final TagKey<Item> SOUL_GROUND_ITEM = TagManager.ITEMS.makeCommonTag("soul_ground");

    public static final TagKey<Block> NETHER_FARMLAND = TagManager.BLOCKS.makeCommonTag("nether_farmland");


    public static final TagKey<Block> NETHER_SAND = TagManager.BLOCKS.makeCommonTag("nether_sand");
    public static final TagKey<Biome> BETTER_NETHER = TagManager.BIOMES.makeTag(BetterNether.C.modId, "biome");

    public static final TagKey<Block> FIREFLY_FLOWERS = TagManager.BLOCKS.makeTag(
            BetterNether.C.modId,
            "firefly_flowers"
    );

    public static void register() {
        TagManager.BLOCKS.add(NETHER_SAND, Blocks.SOUL_SAND);
        TagManager.BLOCKS.add(BlockTags.BEACON_BASE_BLOCKS, NetherBlocks.NETHER_RUBY_BLOCK);

        TagManager.BLOCKS.add(
                FIREFLY_FLOWERS,
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
    }
}