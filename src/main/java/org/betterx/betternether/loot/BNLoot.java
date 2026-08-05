package org.betterx.betternether.loot;

import org.betterx.betternether.BetterNether;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;


/**
 * The keys of the loot tables BetterNether owns.
 * <p>
 * BetterNether's additions to <em>vanilla</em> tables (ruined portal, nether bridge, the four bastion chests
 * and piglin bartering) used to be registered here as Java appenders against {@code LootTableEvents.MODIFY}.
 * They now live in data, emitted by {@code org.betterx.datagen.betternether.recipes.NetherLootAdditionProvider}
 * into {@code data/betternether/wover/loot_addition/}, which {@code wover-loot-api} applies while the
 * reloadable loot table registry is being rebuilt. The behaviour is unchanged; the additions are just
 * inspectable and overridable now.
 */
public class BNLoot {
    public static final ResourceKey<LootTable> CITY_LOOT = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/city"));
    public static final ResourceKey<LootTable> CITY_LOOT_COMMON = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/city_common"));
    public static final ResourceKey<LootTable> CITY_LOOT_SURPRISE = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/city_surprise"));
    public static final ResourceKey<LootTable> LIBRARY_LOOT = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/library"));
    public static final ResourceKey<LootTable> WITHER_TOWER_LOOT = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/wither_tower"));
    public static final ResourceKey<LootTable> WITHER_TOWER_BONUS_LOOT = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/wither_tower_bonus"));
    public static final ResourceKey<LootTable> GHAST_HIVE = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("chests/ghast_hive"));

    public static final ResourceKey<LootTable> FIREFLY = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("entities/firefly"));
    public static final ResourceKey<LootTable> FLYING_PIG = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("entities/flying_pig"));
    public static final ResourceKey<LootTable> JUNGLE_SKELETON = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("entities/jungle_skeleton"));
    public static final ResourceKey<LootTable> NAGA = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("entities/naga"));
    public static final ResourceKey<LootTable> SKULL = ResourceKey.create(Registries.LOOT_TABLE, BetterNether.C.id("entities/skull"));
}
