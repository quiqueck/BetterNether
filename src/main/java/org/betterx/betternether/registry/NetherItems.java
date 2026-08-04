package org.betterx.betternether.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.DebugDataItem;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.BNBlockProperties.FoodShape;
import org.betterx.betternether.blocks.NetherModels;
import org.betterx.betternether.integrations.VanillaExcavatorsIntegration;
import org.betterx.betternether.integrations.VanillaHammersIntegration;
import org.betterx.betternether.items.ItemBlackApple;
import org.betterx.betternether.items.ItemBowlFood;
import org.betterx.betternether.items.complex.DiamondSet;
import org.betterx.betternether.items.complex.NetherSet;
import org.betterx.betternether.items.materials.BNArmorTiers;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.items.materials.BNToolTiers;
import org.betterx.betternether.loot.BNLoot;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.betterx.betternether.registry.item.NetherFoodItems;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

/**
 * Registry facade for BetterNether's items. The actual field declarations live in the per-category
 * classes under {@code org.betterx.betternether.registry.item} (see {@link #register()}
 * for the boot order); this class only keeps the shared registration forwarders, the debug-item
 * bootstrap, and the driver that loads every category class in the correct order.
 */
public class NetherItems {


    private NetherItems() {
    }

    private static ItemRegistry ITEMS_REGISTRY;

    @NotNull
    public static ItemRegistry getItemRegistry() {
        if (ITEMS_REGISTRY == null) {
            ITEMS_REGISTRY = ItemRegistry.forMod(BetterNether.C);
        }
        return ITEMS_REGISTRY;
    }

    @SafeVarargs
    public static Item registerItem(
            String name,
            Function<Item.Properties, ? extends Item> factory,
            TagKey<Item>... tags
    ) {
        return getItemRegistry()
                .<Item>defineDefaultItem(name, def -> factory.apply(def.getProperties()))
                .addTags(tags)
                .buildAndRegister();
    }

    public static Item registerFood(String name, int hunger, float saturationMultiplier) {
        return getItemRegistry()
                .<Item>defineFoodItem(name, def -> new Item(def.getProperties()))
                .nutrition(hunger)
                .saturationModifier(saturationMultiplier)
                .buildAndRegister();
    }

    public static Item registerMedicine(String name, int ticks, int power, boolean bowl) {
        final ApplyStatusEffectsConsumeEffect regeneration = new ApplyStatusEffectsConsumeEffect(
                new MobEffectInstance(MobEffects.REGENERATION, ticks, power),
                1F
        );
        if (bowl) {
            return getItemRegistry()
                    .<Item>defineFoodItem(
                            name, def -> new Item(def.getProperties()) {
                                @Override
                                public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
                                    if (stack.getCount() == 1) {
                                        super.finishUsingItem(stack, world, user);
                                        return new ItemStack(NetherFoodItems.STALAGNATE_BOWL, stack.getCount());
                                    } else {
                                        if (user instanceof Player player) {
                                            if (!player.isCreative())
                                                player.addItem(new ItemStack(NetherFoodItems.STALAGNATE_BOWL));
                                        }
                                        return super.finishUsingItem(stack, world, user);
                                    }
                                }
                            }
                    )
                    .stacksTo(16)
                    .onConsume(regeneration)
                    .buildAndRegister();
        }
        return getItemRegistry()
                .<Item>defineFoodItem(name, def -> new Item(def.getProperties()))
                .onConsume(regeneration)
                .buildAndRegister();
    }

    public static Properties defaultSettings() {
        return new Item.Properties();
    }

    public static Item makeEgg(String name, EntityType<? extends Mob> type, int background, int dots) {
        return getItemRegistry().defineSpawnEgg(name).entityType(type).colors(background, dots).buildAndRegister();
    }

    public static Item registerNetherItem(String name, Item item) {
        return getItemRegistry().register(name, item);
    }

    /**
     * Registers a dev-only {@link DebugDataItem} together with the model trait that renders it as
     * {@code icon}'s texture.
     * <p>
     * {@code DebugDataItem} used to build its model at runtime from that same icon (via the old
     * {@code ItemModelProvider}). Runtime model building is unsupported since 1.21.4, so the model has to be
     * generated at datagen time instead - hence the {@link NetherModels#debugItem} trait. Traits are
     * only applied by {@link de.ambertation.wover.item.api.ItemDefinition#build()}, so these go through
     * {@code defineDefaultItem} rather than the plain {@link #registerNetherItem(String, Item)}.
     *
     * @param name    the item's registry path
     * @param factory builds the item from its (already known) registry key
     * @param icon    the item whose texture stands in for the debug item
     */
    private static Item registerDebugItem(
            String name,
            Function<ResourceKey<Item>, DebugDataItem> factory,
            Item icon
    ) {
        return getItemRegistry()
                .<DebugDataItem>defineDefaultItem(name, def -> factory.apply(def.itemKey))
                .addTrait(NetherModels.debugItem(() -> icon))
                .buildAndRegister();
    }

    static {
        if (BCLib.isDevEnvironment()) {
            BetterNether.C.log.warn("Generating Debug Helpers");

            registerDebugItem(
                    "debug/city_loot",
                    key -> DebugDataItem.forLootTable(key, BNLoot.CITY_LOOT, Items.IRON_INGOT),
                    Items.IRON_INGOT
            );
            registerDebugItem(
                    "debug/city_loot_common",
                    key -> DebugDataItem.forLootTable(key, BNLoot.CITY_LOOT_COMMON, Items.GOLD_INGOT),
                    Items.GOLD_INGOT
            );
            registerDebugItem(
                    "debug/city_loot_surprise",
                    key -> DebugDataItem.forLootTable(key, BNLoot.CITY_LOOT_SURPRISE, Items.DIAMOND),
                    Items.DIAMOND
            );
            registerDebugItem(
                    "debug/wither_tower_loot",
                    key -> DebugDataItem.forLootTable(key, BNLoot.WITHER_TOWER_LOOT, NetherResourceItems.CINCINNASITE_INGOT),
                    NetherResourceItems.CINCINNASITE_INGOT
            );
            registerDebugItem(
                    "debug/wither_tower_bonus_loot",
                    key -> DebugDataItem.forLootTable(key, BNLoot.WITHER_TOWER_BONUS_LOOT, NetherResourceItems.NETHER_RUBY),
                    NetherResourceItems.NETHER_RUBY
            );

            registerDebugItem(
                    "debug/city_spawner",
                    key -> DebugDataItem.forSpawner(key, NetherItems::buildCitySpawnerData, Items.SPECTRAL_ARROW),
                    Items.SPECTRAL_ARROW
            );
        }
    }

    private static CompoundTag buildItem(int count, Item item, ResourceKey<Enchantment>... enchantments) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putByte("Count", (byte) count);

        if (enchantments.length > 0 && WorldState.registryAccess() != null) {
            ListTag chants = new ListTag();
            final var enchReg = WorldState.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            tag.put("Enchantments", chants);
            for (ResourceKey<Enchantment> e : enchantments) {
                final var ench = enchReg.getValue(e);
                final var eTag = new CompoundTag();
                eTag.putInt("lvl", ench.getMaxLevel());
                eTag.putString("id", e.location().toString());
                chants.add(eTag);
            }
        }
        return tag;
    }

    @NotNull
    private static CompoundTag buildCitySpawnerData() {
        ListTag handItems = new ListTag();
        handItems.add(buildItem(1, NetherEquipmentItems.CINCINNASITE_DIAMOND_SET.get(ToolSlot.SWORD_SLOT)));
        handItems.add(buildItem(1, Items.SHIELD));

        ListTag armorItems = new ListTag();
        armorItems.add(buildItem(
                1,
                NetherEquipmentItems.CINCINNASITE_SET.get(ArmorSlot.BOOTS_SLOT),
                Enchantments.PROTECTION
        ));
        armorItems.add(buildItem(
                1,
                NetherEquipmentItems.CINCINNASITE_SET.get(ArmorSlot.LEGGINGS_SLOT),
                Enchantments.PROTECTION
        ));
        armorItems.add(buildItem(
                1,
                NetherEquipmentItems.CINCINNASITE_SET.get(ArmorSlot.CHESTPLATE_SLOT),
                Enchantments.PROTECTION,
                Enchantments.THORNS
        ));
        armorItems.add(buildItem(
                1,
                NetherEquipmentItems.CINCINNASITE_SET.get(ArmorSlot.HELMET_SLOT),
                Enchantments.PROTECTION
        ));

        ListTag handDropChance = new ListTag();
        handDropChance.add(FloatTag.valueOf(0));
        handDropChance.add(FloatTag.valueOf(0));

        ListTag armorDropChance = new ListTag();
        armorDropChance.add(FloatTag.valueOf(0));
        armorDropChance.add(FloatTag.valueOf(0));
        armorDropChance.add(FloatTag.valueOf(0));
        armorDropChance.add(FloatTag.valueOf(0));


        CompoundTag entity = new CompoundTag();
        entity.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER_SKELETON).toString());
        entity.putBoolean("PersistenceRequired", true);
        entity.put("HandItems", handItems);
        entity.put("ArmorItems", armorItems);
        entity.put("HandDropChances", handDropChance);
        entity.put("ArmorDropChances", armorDropChance);

        CompoundTag skyLightLimit = new CompoundTag();
        skyLightLimit.putByte("max_inclusive", (byte) 13);

        CompoundTag blockLightLimit = new CompoundTag();
        skyLightLimit.putByte("max_inclusive", (byte) 13);

        CompoundTag customSpawnRules = new CompoundTag();
        customSpawnRules.put("sky_light_limit", skyLightLimit);
        customSpawnRules.put("block_light_limit", blockLightLimit);

        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        spawnData.put("custom_spawn_rules", customSpawnRules);

        CompoundTag root = new CompoundTag();
        root.putShort("SpawnRange", (short) 4);
        root.putShort("SpawnCount", (short) 8);
        root.putShort("MaxNearbyEntities", (short) 18);
        root.putShort("Delay", (short) 499);
        root.putShort("MinSpawnDelay", (short) 300);
        root.putShort("MaxSpawnDelay", (short) 1600);
        root.putShort("RequiredPlayerRange", (short) 20);
        root.put("SpawnData", spawnData);


        return root;
    }

    @ApiStatus.Internal
    public static void register() {
        NetherFoodItems.ensureLoaded();
        NetherEquipmentItems.ensureLoaded();
        NetherResourceItems.ensureLoaded();
    }
}
