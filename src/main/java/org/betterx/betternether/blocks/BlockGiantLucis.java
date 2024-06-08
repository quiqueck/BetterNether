package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGiantLucis extends HugeMushroomBlock implements AddMineableAxe, BlockLootProvider {
    public BlockGiantLucis() {
        super(BehaviourBuilders
                .createWalkablePlant(MapColor.COLOR_YELLOW)
                .requiresCorrectToolForDrops()
                .lightLevel((bs) -> 15)
                .sound(SoundType.WOOD)
                .strength(1F));
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(
                this,
                List.of(
                        new LootLookupProvider.DropInfo(NetherBlocks.LUCIS_SPORE, UniformGenerator.between(0, 1)),
                        new LootLookupProvider.DropInfo(NetherItems.GLOWSTONE_PILE, UniformGenerator.between(0, 2))
                )
        );
    }
}
