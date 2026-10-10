package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.items.NetherAxe;
import org.betterx.betternether.items.NetherHoe;
import org.betterx.betternether.items.NetherPickaxe;
import org.betterx.betternether.items.NetherShovel;
import org.betterx.betternether.items.NetherSpear;
import org.betterx.betternether.items.NetherSword;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.equipment.Equippable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Every BetterNether tool and armor piece must carry the same item tags as its vanilla counterpart
 * (quiqueck/BetterNether#197, #156).
 * <p>
 * The expected tags are not hard-coded: they are read from the loaded tag registry as the tags that
 * <em>both</em> the iron and the diamond counterpart are in (so material-specific tags drop out). That
 * catches every tag vanilla or Fabric's convention tags attach to "a pickaxe" / "a helmet" -
 * {@code #minecraft:cluster_max_harvestables}, {@code #c:tools/mining_tool},
 * {@code #minecraft:enchantable/*}, {@code #minecraft:trimmable_armor}, {@code #c:armors}, ...
 * <p>
 * Only the shared namespaces ({@link #SHARED_NAMESPACES}) are compared. Other mods list vanilla gear in
 * their own private tags too - Incendium's {@code #incendium:tools}, Terralith's loot tags - and those
 * are theirs to fill; requiring our gear there made the cross-mod compat runs fail.
 * <p>
 * Items are classified by their class or {@code EQUIPPABLE} component, never by a tag, so an item that
 * lost its primary tag (e.g. {@code #minecraft:pickaxes}) is still checked.
 */
public class EquipmentTagParityGameTest {
    private record Category(String name, List<Item> vanilla) {}

    private static final Category PICKAXE = new Category("pickaxe", List.of(Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE));
    private static final Category AXE = new Category("axe", List.of(Items.IRON_AXE, Items.DIAMOND_AXE));
    private static final Category SHOVEL = new Category("shovel", List.of(Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL));
    private static final Category HOE = new Category("hoe", List.of(Items.IRON_HOE, Items.DIAMOND_HOE));
    private static final Category SWORD = new Category("sword", List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD));
    private static final Category SPEAR = new Category("spear", List.of(Items.IRON_SPEAR, Items.DIAMOND_SPEAR));
    private static final Category SHEARS = new Category("shears", List.of(Items.SHEARS));
    private static final Category HELMET = new Category("helmet", List.of(Items.IRON_HELMET, Items.DIAMOND_HELMET));
    private static final Category CHESTPLATE = new Category("chestplate", List.of(Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE));
    private static final Category LEGGINGS = new Category("leggings", List.of(Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS));
    private static final Category BOOTS = new Category("boots", List.of(Items.IRON_BOOTS, Items.DIAMOND_BOOTS));

    private static Category categorize(Item item) {
        if (item instanceof NetherPickaxe) return PICKAXE;
        if (item instanceof NetherAxe) return AXE;
        if (item instanceof NetherShovel) return SHOVEL;
        if (item instanceof NetherHoe) return HOE;
        if (item instanceof NetherSword) return SWORD;
        if (item instanceof NetherSpear) return SPEAR;
        if (item instanceof ShearsItem) return SHEARS;

        final Equippable equippable = item.components().get(DataComponents.EQUIPPABLE);
        if (equippable != null && equippable.assetId().isPresent()) {
            final EquipmentSlot slot = equippable.slot();
            if (slot == EquipmentSlot.HEAD) return HELMET;
            if (slot == EquipmentSlot.CHEST) return CHESTPLATE;
            if (slot == EquipmentSlot.LEGS) return LEGGINGS;
            if (slot == EquipmentSlot.FEET) return BOOTS;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static Set<TagKey<Item>> tagsOf(Item item) {
        return item.builtInRegistryHolder().tags().collect(Collectors.toSet());
    }

    private static final Set<String> SHARED_NAMESPACES = Set.of("minecraft", "c", "fabric");

    private static Set<TagKey<Item>> expectedTags(Category category) {
        Set<TagKey<Item>> expected = null;
        for (Item vanilla : category.vanilla) {
            final Set<TagKey<Item>> tags = tagsOf(vanilla);
            if (expected == null) expected = new HashSet<>(tags);
            else expected.retainAll(tags);
        }
        expected.removeIf(tag -> !SHARED_NAMESPACES.contains(tag.location().getNamespace()));
        return expected;
    }

    @GameTest
    public void equipmentCarriesVanillaCounterpartTags(GameTestHelper helper) {
        // Control: the harness must see vanilla's own tags, or every expectation below is empty and
        // the test would pass vacuously.
        final Set<TagKey<Item>> pickaxeTags = expectedTags(PICKAXE);
        if (!pickaxeTags.contains(ItemTags.PICKAXES) || !pickaxeTags.contains(ItemTags.MINING_ENCHANTABLE)) {
            throw helper.assertionException(Component.literal(
                    "vanilla pickaxe tags not loaded, got " + pickaxeTags
            ));
        }
        if (!expectedTags(HELMET).contains(ItemTags.TRIMMABLE_ARMOR)) {
            throw helper.assertionException(Component.literal("vanilla helmets are not #trimmable_armor?"));
        }

        final List<String> problems = new ArrayList<>();
        final Set<String> seen = new TreeSet<>();
        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            if (!entry.getKey().identifier().getNamespace().equals("betternether")) continue;
            final Item item = entry.getValue();
            final Category category = categorize(item);
            if (category == null) continue;
            seen.add(category.name);

            final Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
            final Set<String> missing = new TreeSet<>();
            for (TagKey<Item> tag : expectedTags(category)) {
                if (!holder.is(tag)) missing.add("#" + tag.location());
            }
            if (!missing.isEmpty()) {
                problems.add(entry.getKey().identifier() + " (" + category.name + ") is missing " + missing);
            }
        }

        // Every category BetterNether ships must actually have been found, or the classification broke.
        for (Category c : List.of(PICKAXE, AXE, SHOVEL, HOE, SWORD, SPEAR, SHEARS, HELMET, CHESTPLATE, LEGGINGS, BOOTS)) {
            if (!seen.contains(c.name)) problems.add("no betternether " + c.name + " found");
        }

        if (!problems.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betternether equipment tags differ from vanilla:\n - " + String.join("\n - ", problems)
            ));
        }
        helper.succeed();
    }
}
