package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourClimableVine;
import org.betterx.bclib.blocks.BaseSimpleVineBlock;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.jetbrains.annotations.NotNull;

public class BlockBlackVine extends BaseSimpleVineBlock implements BonemealableBlock, BehaviourClimableVine, BlockLootProvider {
    public BlockBlackVine() {
        super(BehaviourBuilders.createStaticVine(MapColor.COLOR_BLACK).instabreak(), 27, 1);
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 2.0F))
                        .when(provider.shearsOrHoeSilkTouchCondition())
                        .add(LootItem.lootTableItem(this)));
    }
}
