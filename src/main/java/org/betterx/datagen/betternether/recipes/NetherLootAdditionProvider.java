package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.item.NetherMusicDiscItems;
import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider;
import de.ambertation.wover.loot.api.LootAdditionFile;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Injects BetterNether's obsidian variants and the nether bowl smithing template into vanilla's
 * ruined-portal / nether-bridge / bastion chests and into the piglin bartering table.
 *
 * <p>These additions used to be Java appenders registered from {@code BNLoot.register()} against
 * {@code fabric-loot-api-v3}'s {@code LootTableEvents.MODIFY}. They are now emitted as
 * {@code data/betternether/wover/loot_addition/*.json} and applied by {@code wover-loot-api} while the
 * reloadable loot table registry is being rebuilt, just before vanilla validates it - inspectable and
 * overridable by a pack author now, with the verbs, ordering and idempotency guarantees unchanged.
 */
public class NetherLootAdditionProvider extends WoverLootAdditionProvider {
    public NetherLootAdditionProvider(ModCore modCore) {
        super(modCore, "BetterNether Loot Additions");
    }

    @Override
    protected void bootstrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<Identifier, LootAdditionFile.Builder> consumer
    ) {
        final LootAdditionFile.Builder chests = LootAdditionFile.builder();

        chests.forTables(BuiltInLootTables.RUINED_PORTAL, BuiltInLootTables.NETHER_BRIDGE)
              .addPool(
                      LootPool.lootPool()
                              .setRolls(UniformGenerator.between(0, 4))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.BLUE_OBSIDIAN.asItem())
                                           .setWeight(1)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   1.0F,
                                                   2.0F
                                           ))))
                              .add(EmptyLootItem.emptyItem()
                                                .setWeight(9)),
                      LootPool.lootPool()
                              .setRolls(ConstantValue.exactly(1.0f))
                              .add(EmptyLootItem.emptyItem().setWeight(9))
                              .add(LootItem.lootTableItem(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                                           .setWeight(1))
              );

        chests.forTables(
                      BuiltInLootTables.BASTION_BRIDGE,
                      BuiltInLootTables.BASTION_HOGLIN_STABLE,
                      BuiltInLootTables.BASTION_TREASURE
              )
              .addPool(
                      LootPool.lootPool()
                              .setRolls(UniformGenerator.between(1, 2))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN.asItem())
                                           .setWeight(5)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   3.0F,
                                                   8.0F
                                           ))))
                              .add(LootItem
                                      .lootTableItem(NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN.asItem())
                                      .setWeight(1)
                                      .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                              1.0F,
                                              4.0F
                                      ))))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.WEEPING_OBSIDIAN.asItem())
                                           .setWeight(1)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   1.0F,
                                                   4.0F
                                           ))))
                              .add(EmptyLootItem.emptyItem()
                                                .setWeight(50)),
                      LootPool.lootPool()
                              .setRolls(ConstantValue.exactly(1.0f))
                              .add(EmptyLootItem.emptyItem().setWeight(99))
                              .add(LootItem.lootTableItem(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                                           .setWeight(1))
              );

        chests.forTables(BuiltInLootTables.BASTION_OTHER)
              .addPool(
                      LootPool.lootPool()
                              .setRolls(UniformGenerator.between(1, 2))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.BLUE_OBSIDIAN.asItem())
                                           .setWeight(10)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   4.0F,
                                                   6.0F
                                           ))))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN.asItem())
                                           .setWeight(5)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   1.0F,
                                                   5.0F
                                           ))))
                              .add(LootItem
                                      .lootTableItem(NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN.asItem())
                                      .setWeight(1)
                                      .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                              1.0F,
                                              2.0F
                                      ))))
                              .add(LootItem.lootTableItem(NetherObsidianBlocks.WEEPING_OBSIDIAN.asItem())
                                           .setWeight(1)
                                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                   1.0F,
                                                   2.0F
                                           ))))
                              .add(EmptyLootItem.emptyItem()
                                                .setWeight(50)),
                      LootPool.lootPool()
                              .setRolls(ConstantValue.exactly(1.0f))
                              .add(EmptyLootItem.emptyItem().setWeight(9))
                              .add(LootItem.lootTableItem(NetherTemplates.NETHER_BOWL_SMITHING_TEMPLATE)
                                           .setWeight(1))
              );

        consumer.accept(BetterNether.C.id("vanilla_chests"), chests);

        // The bartering table rolls exactly one entry from a single weighted pool, so the BetterNether items
        // have to be merged *into* that pool instead of being added as a separate pool (which would hand out
        // an extra stack on every barter).
        final LootAdditionFile.Builder bartering = LootAdditionFile.builder();
        bartering.forTables(BuiltInLootTables.PIGLIN_BARTERING)
                 .addToEveryPool(
                         LootItem
                                 .lootTableItem(NetherObsidianBlocks.BLUE_OBSIDIAN.asItem())
                                 .setWeight(40),
                         LootItem
                                 .lootTableItem(NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN.asItem())
                                 .setWeight(40)
                                 .apply(SetItemCountFunction.setCount(
                                         UniformGenerator.between(
                                                 1.0F,
                                                 3.0F
                                         ))),
                         LootItem
                                 .lootTableItem(NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN.asItem())
                                 .setWeight(20)
                                 .apply(SetItemCountFunction.setCount(
                                         UniformGenerator.between(
                                                 1.0F,
                                                 2.0F
                                         ))),
                         LootItem
                                 .lootTableItem(NetherObsidianBlocks.WEEPING_OBSIDIAN.asItem())
                                 .setWeight(20)
                                 .apply(SetItemCountFunction.setCount(
                                         UniformGenerator.between(
                                                 1.0F,
                                                 2.0F
                                         ))),
                         // The gloomwood discs sit at the rare end of the pool (vanilla's own pool totals
                         // ~459, so weight 3 is roughly a 0.5% chance each, on par with the iron boots).
                         LootItem
                                 .lootTableItem(NetherMusicDiscItems.MUSIC_DISC_GLOOM_WOODS)
                                 .setWeight(3),
                         LootItem
                                 .lootTableItem(NetherMusicDiscItems.MUSIC_DISC_GLOOM_WISPS)
                                 .setWeight(3),
                         LootItem
                                 .lootTableItem(NetherMusicDiscItems.MUSIC_DISC_GLOOMSCULK)
                                 .setWeight(3)
                 );

        consumer.accept(BetterNether.C.id("piglin_bartering"), bartering);
    }
}
