package org.betterx.betternether.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.DebugDataItem;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.BNBlockProperties.FoodShape;
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
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.complex.api.equipment.ToolSlot;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class NetherItems {
    public static final Item BLACK_APPLE = registerItem("black_apple", ItemBlackApple::new);

    public static final Item STALAGNATE_BOWL = registerItem(
            "stalagnate_bowl",
            props -> new ItemBowlFood(null, FoodShape.NONE, props)
    );
    public static final Item STALAGNATE_BOWL_WART = registerItem(
            "stalagnate_bowl_wart",
            props -> new ItemBowlFood(
                    Foods.COOKED_CHICKEN,
                    FoodShape.WART,
                    props
            )
    );
    public static final Item STALAGNATE_BOWL_MUSHROOM = registerItem(
            "stalagnate_bowl_mushroom",
            props -> new ItemBowlFood(
                    Foods.MUSHROOM_STEW,
                    FoodShape.MUSHROOM,
                    props
            )
    );
    public static final Item STALAGNATE_BOWL_APPLE = registerItem(
            "stalagnate_bowl_apple",
            props -> new ItemBowlFood(Foods.APPLE, FoodShape.APPLE, props)
    );
    public static final Item HOOK_MUSHROOM_COOKED = registerFood("hook_mushroom_cooked", 4, 0.4F);

    public static final Item CINCINNASITE = registerItem("cincinnasite", Item::new);
    public static final Item CINCINNASITE_INGOT = registerItem("cincinnasite_ingot", Item::new,
            CommonItemTags.IRON_INGOTS
    );
    public static final Item NETHER_RUBY = registerItem("nether_ruby", Item::new);

    public static final NetherSet CINCINNASITE_SET = new NetherSet(
            "cincinnasite",
            BNToolTiers.CINCINNASITE,
            BNArmorTiers.CINCINNASITE,
            true
    );


    public static final NetherSet NETHER_RUBY_SET = new NetherSet(
            "nether_ruby",
            BNToolTiers.NETHER_RUBY,
            BNArmorTiers.NETHER_RUBY,
            false
    );

    public static final DiamondSet CINCINNASITE_DIAMOND_SET = new DiamondSet(CINCINNASITE_SET);

    public static final NetherSet FLAMING_RUBY_SET = new NetherSet(
            "flaming_ruby",
            BNToolTiers.FLAMING_RUBY,
            BNArmorTiers.FLAMING_RUBY,
            false,
            NETHER_RUBY_SET
    );
    public static final Item CINCINNASITE_HAMMER = registerItem(
            "cincinnasite_hammer",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.CINCINNASITE,
                    4,
                    -2.0F,
                    props
            )
    );
    public static final Item CINCINNASITE_HAMMER_DIAMOND = registerItem(
            "cincinnasite_hammer_diamond",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.CINCINNASITE_DIAMOND,
                    5,
                    -2.0F,
                    props
            )
    );
    public static final Item NETHER_RUBY_HAMMER = registerItem(
            "nether_ruby_hammer",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.NETHER_RUBY,
                    5,
                    -2.0F,
                    props
            )
    );

    public static final Item CINCINNASITE_EXCAVATOR = registerItem(
            "cincinnasite_excavator",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.CINCINNASITE,
                    4,
                    -1.6F,
                    props
            )
    );
    public static final Item CINCINNASITE_EXCAVATOR_DIAMOND = registerItem(
            "cincinnasite_excavator_diamond",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.CINCINNASITE_DIAMOND,
                    5,
                    -2.0F,
                    props
            )
    );
    public static final Item NETHER_RUBY_EXCAVATOR = registerItem(
            "nether_ruby_excavator",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.NETHER_RUBY,
                    5,
                    -2.0F,
                    props
            )
    );

    public static final Item GLOWSTONE_PILE = registerItem("glowstone_pile", Item::new);
    public static final Item LAPIS_PILE = registerItem("lapis_pile", Item::new);

    public static final Item AGAVE_LEAF = registerItem("agave_leaf", Item::new);
    public static final Item AGAVE_MEDICINE = registerMedicine("agave_medicine", 40, 2, true);
    public static final Item HERBAL_MEDICINE = registerMedicine("herbal_medicine", 10, 5, true);

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

    public static Stream<Item> getModItems() {
        return getItemRegistry().allItems();
    }


    public static Item registerShears(String name, Item item) {
        if (item != Items.AIR) {
            return getItemRegistry().registerAsTool(name, item);
        }

        return item;
    }

    public static Item registerTool(String name, Item item, TagKey<Item>... tags) {
        if (item != Items.AIR) {
            getItemRegistry().registerAsTool(name, item, tags);
        }

        return item;
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
                    .<Item>defineFoodItem(name, def -> new Item(def.getProperties()) {
                        @Override
                        public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
                            if (stack.getCount() == 1) {
                                super.finishUsingItem(stack, world, user);
                                return new ItemStack(NetherItems.STALAGNATE_BOWL, stack.getCount());
                            } else {
                                if (user instanceof Player player) {
                                    if (!player.isCreative())
                                        player.addItem(new ItemStack(NetherItems.STALAGNATE_BOWL));
                                }
                                return super.finishUsingItem(stack, world, user);
                            }
                        }
                    })
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

    static {
        if (BCLib.isDevEnvironment()) {
            BetterNether.C.log.warn("Generating Debug Helpers");

            registerNetherItem(
                    "debug/city_loot",
                    DebugDataItem.forLootTable(getItemRegistry().key("debug/city_loot"), BNLoot.CITY_LOOT, Items.IRON_INGOT)
            );
            registerNetherItem(
                    "debug/city_loot_common",
                    DebugDataItem.forLootTable(getItemRegistry().key("debug/city_loot_common"), BNLoot.CITY_LOOT_COMMON, Items.GOLD_INGOT)
            );
            registerNetherItem(
                    "debug/city_loot_surprise",
                    DebugDataItem.forLootTable(getItemRegistry().key("debug/city_loot_surprise"), BNLoot.CITY_LOOT_SURPRISE, Items.DIAMOND)
            );
            registerNetherItem(
                    "debug/wither_tower_loot",
                    DebugDataItem.forLootTable(getItemRegistry().key("debug/wither_tower_loot"), BNLoot.WITHER_TOWER_LOOT, NetherItems.CINCINNASITE_INGOT)
            );
            registerNetherItem(
                    "debug/wither_tower_bonus_loot",
                    DebugDataItem.forLootTable(getItemRegistry().key("debug/wither_tower_bonus_loot"), BNLoot.WITHER_TOWER_BONUS_LOOT, NetherItems.NETHER_RUBY)
            );

            registerNetherItem(
                    "debug/city_spawner",
                    DebugDataItem.forSpawner(getItemRegistry().key("debug/city_spawner"), NetherItems::buildCitySpawnerData, Items.SPECTRAL_ARROW)
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
        handItems.add(buildItem(1, CINCINNASITE_DIAMOND_SET.get(ToolSlot.SWORD_SLOT)));
        handItems.add(buildItem(1, Items.SHIELD));

        ListTag armorItems = new ListTag();
        armorItems.add(buildItem(
                1,
                CINCINNASITE_SET.get(ArmorSlot.BOOTS_SLOT),
                Enchantments.PROTECTION
        ));
        armorItems.add(buildItem(
                1,
                CINCINNASITE_SET.get(ArmorSlot.LEGGINGS_SLOT),
                Enchantments.PROTECTION
        ));
        armorItems.add(buildItem(
                1,
                CINCINNASITE_SET.get(ArmorSlot.CHESTPLATE_SLOT),
                Enchantments.PROTECTION,
                Enchantments.THORNS
        ));
        armorItems.add(buildItem(
                1,
                CINCINNASITE_SET.get(ArmorSlot.HELMET_SLOT),
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
        //NO-OP
    }
}
