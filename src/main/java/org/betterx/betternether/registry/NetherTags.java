package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class NetherTags {
    public static final TagKey<Biome> BETTER_NETHER_DECORATIONS = TagManager.BIOMES.makeStructureTag(
            BetterNether.C,
            "nether_decorations"
    );

    /**
     * Structures a nether city neither builds into nor carves its cave through (see
     * {@link org.betterx.betternether.world.structures.city.StructureAvoidingProcessor}); fortress and
     * bastion by default. Hand-written in
     * {@code resources/data/betternether/tags/worldgen/structure/}, as Wover has no structure tag registry.
     */
    public static final TagKey<Structure> CITY_AVOIDS = TagKey.create(
            Registries.STRUCTURE,
            BetterNether.C.id("city_avoids")
    );

    public static final TagKey<Item> SOUL_GROUND_ITEM = TagManager.ITEMS.makeTag(BetterNether.C, "soul_ground");

    public static final TagKey<Block> NETHER_FARMLAND = TagManager.BLOCKS.makeTag(BetterNether.C, "nether_farmland");


    public static final TagKey<Block> NETHER_SAND = TagManager.BLOCKS.makeTag(BetterNether.C, "nether_sand");
    /**
     * Nylium that belongs to the Nether. {@code #minecraft:nylium} is not usable as a plant's ground rule:
     * BetterEnd puts its End mosses into it, so a nether plant planted on that tag grows on End terrain
     * (#118).
     */
    public static final TagKey<Block> NETHER_NYLIUM = TagManager.BLOCKS.makeTag(BetterNether.C, "nether_nylium");
    public static final TagKey<Biome> BETTER_NETHER = TagManager.BIOMES.makeTag(BetterNether.C, "biome");

    public static final TagKey<Block> FIREFLY_FLOWERS = TagManager.BLOCKS.makeTag(
            BetterNether.C,
            "firefly_flowers"
    );

    public static final TagKey<Item> FLAMING_RUBY_ENCHANTABLE = TagManager.ITEMS.makeTag(BetterNether.C, "flaming_ruby_enchantable");
    public static final TagKey<Item> FLAMING_RUBY_PRIMARY = TagManager.ITEMS.makeTag(BetterNether.C, "flaming_ruby_primary");

    public static final TagKey<Item> OBSIDIAN_BREAKER_ENCHANTABLE = TagManager.ITEMS.makeTag(BetterNether.C, "obsidian_breaker_enchantable");
    public static final TagKey<Item> OBSIDIAN_BREAKER_PRIMARY = TagManager.ITEMS.makeTag(BetterNether.C, "obsidian_breaker_primary");
    public static final TagKey<Block> OBSIDIAN_BREAKER_MINEABLE = TagManager.BLOCKS.makeTag(BetterNether.C, "obsidian_breaker_mineable");

    public static final TagKey<Item> NETHER_PICKAXES = TagManager.ITEMS.makeTag(BetterNether.C, "pickaxes");

    public static void register() {


    }
}