package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

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
    public static final TagKey<Item> FLAMING_RUBY_PRIMARY = TagManager.ITEMS.makeTag(BetterNether.C.modId, "flaming_ruby_primary");

    public static final TagKey<Item> OBSIDIAN_BREAKER_ENCHANTABLE = TagManager.ITEMS.makeTag(BetterNether.C.modId, "obsidian_breaker_enchantable");
    public static final TagKey<Item> OBSIDIAN_BREAKER_PRIMARY = TagManager.ITEMS.makeTag(BetterNether.C.modId, "obsidian_breaker_primary");
    public static final TagKey<Block> OBSIDIAN_BREAKER_MINEABLE = TagManager.BLOCKS.makeTag(BetterNether.C.modId, "obsidian_breaker_mineable");

    public static final TagKey<Item> NETHER_PICKAXES = TagManager.ITEMS.makeTag(BetterNether.C.modId, "pickaxes");

    public static void register() {


    }
}