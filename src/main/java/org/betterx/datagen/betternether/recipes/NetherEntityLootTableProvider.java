package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.loot.BNLoot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;

import java.util.function.BiConsumer;

public class NetherEntityLootTableProvider extends SimpleFabricLootTableProvider {

    public NetherEntityLootTableProvider(
            FabricDataOutput output
    ) {
        super(output, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(
                BNLoot.FIREFLY,
                LootTable.lootTable()
                         .withPool(killLoot(2, 2, Items.GLOWSTONE_DUST))
        );

        biConsumer.accept(
                BNLoot.FLYING_PIG,
                LootTable.lootTable()
                         .withPool(killLoot(2, 2, Items.PORKCHOP))
        );

        biConsumer.accept(
                BNLoot.JUNGLE_SKELETON,
                LootTable.lootTable()
                         .withPool(killLoot(2, 1, Items.ARROW))
                         .withPool(killLoot(2, 2, Items.BONE))
        );

        biConsumer.accept(
                BNLoot.NAGA,
                LootTable.lootTable()
                         .withPool(killLoot(1, 1, Items.COAL))
                         .withPool(killLoot(2, 2, Items.BONE))
                         .withPool(nagaPlayerKill())
        );

        biConsumer.accept(
                BNLoot.SKULL,
                LootTable.lootTable()
                         .withPool(killLoot(1, 1, Items.COAL))
                         .withPool(killLoot(2, 2, Items.BONE))
                         .withPool(nagaPlayerKill())
        );
    }

    private LootPool.Builder nagaPlayerKill() {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(Blocks.WITHER_SKELETON_SKULL)
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f))
                );
    }

    private LootPool.Builder killLoot(int maxDrop, int maxLootingDrop, ItemLike drop) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(drop)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, maxDrop)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, maxLootingDrop)))
                );

    }
}
