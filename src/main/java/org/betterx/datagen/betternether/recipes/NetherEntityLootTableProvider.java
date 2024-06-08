package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.loot.BNLoot;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverLootTableProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public class NetherEntityLootTableProvider extends WoverLootTableProvider {


    public NetherEntityLootTableProvider(
            ModCore modCore
    ) {
        super(modCore, "BetterNether - Entity Loot", LootContextParamSets.ENTITY);
    }

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        biConsumer.accept(
                BNLoot.FIREFLY,
                LootTable.lootTable()
                         .withPool(killLoot(lookup, 2, 2, Items.GLOWSTONE_DUST))
        );

        biConsumer.accept(
                BNLoot.FLYING_PIG,
                LootTable.lootTable()
                         .withPool(killLoot(lookup, 2, 2, Items.PORKCHOP))
        );

        biConsumer.accept(
                BNLoot.JUNGLE_SKELETON,
                LootTable.lootTable()
                         .withPool(killLoot(lookup, 2, 1, Items.ARROW))
                         .withPool(killLoot(lookup, 2, 2, Items.BONE))
        );

        biConsumer.accept(
                BNLoot.NAGA,
                LootTable.lootTable()
                         .withPool(killLoot(lookup, 1, 1, Items.COAL))
                         .withPool(killLoot(lookup, 2, 2, Items.BONE))
                         .withPool(nagaPlayerKill(lookup))
        );

        biConsumer.accept(
                BNLoot.SKULL,
                LootTable.lootTable()
                         .withPool(killLoot(lookup, 1, 1, Items.COAL))
                         .withPool(killLoot(lookup, 2, 2, Items.BONE))
                         .withPool(nagaPlayerKill(lookup))
        );
    }

    private LootPool.Builder nagaPlayerKill(HolderLookup.@NotNull Provider lookup) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(Blocks.WITHER_SKELETON_SKULL)
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(lookup, 0.05f, 0.01f))
                );
    }

    private LootPool.Builder killLoot(
            HolderLookup.@NotNull Provider lookup,
            int maxDrop,
            int maxLootingDrop,
            ItemLike drop
    ) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(drop)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, maxDrop)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(lookup, UniformGenerator.between(0, maxLootingDrop)))
                );

    }
}
