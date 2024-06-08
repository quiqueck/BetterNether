package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.tag.v3.TagManager;
import org.betterx.wover.complex.api.tool.ArmorSlot;
import org.betterx.wover.complex.api.tool.ToolSlot;

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

    public static final TagKey<Item> FLAMING_RUBY_ENCHANTABLE = TagManager.ITEMS.makeTag(BetterNether.C.modId, "flaming_ruby_enchantable");

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

        TagManager.ITEMS.add(
                FLAMING_RUBY_ENCHANTABLE,
                NetherItems.FLAMING_RUBY_SET.get(ArmorSlot.BOOTS_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ArmorSlot.CHESTPLATE_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ArmorSlot.HELMET_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ArmorSlot.LEGGINGS_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ToolSlot.AXE_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ToolSlot.HOE_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ToolSlot.PICKAXE_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ToolSlot.SHOVEL_SLOT),
                NetherItems.FLAMING_RUBY_SET.get(ToolSlot.SWORD_SLOT)
        );
    }
}