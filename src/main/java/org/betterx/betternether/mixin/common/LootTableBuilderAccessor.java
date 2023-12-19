package org.betterx.betternether.mixin.common;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {
    @Accessor("pools")
    ImmutableList.Builder<LootPool> getPools();

    @Accessor("functions")
    ImmutableList.Builder<LootItemFunction> getFunctions();
}
