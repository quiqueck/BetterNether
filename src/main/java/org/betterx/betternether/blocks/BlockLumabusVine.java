package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.world.features.DeferedSeedBlock;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLumabusVine extends BaseVineBlock implements DeferedSeedBlock, BlockLootProvider {
    private static final VoxelShape MIDDLE_SHAPE = box(4, 0, 4, 12, 16, 12);
    static final VoxelShape BOTTOM_SHAPE = box(2, 4, 2, 14, 16, 14);
    private Block seed;

    public BlockLumabusVine(MapColor color) {
        super(
                BehaviourBuilders
                        .createStaticVine(color)
                        .lightLevel(getLuminance()),
                9,
                1
        );
    }

    @Override
    public void setSeed(Block seed) {
        this.seed = seed;
    }

    private static ToIntFunction<BlockState> getLuminance() {
        return (blockState) -> blockState.getOptionalValue(SHAPE).orElse(TripleShape.TOP) == TripleShape.BOTTOM
                ? 15
                : 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.BOTTOM ? BOTTOM_SHAPE : MIDDLE_SHAPE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @NotNull ItemStack getCloneItemStack(
            @NotNull LevelReader level,
            @NotNull BlockPos pos,
            @NotNull BlockState state
    ) {
        return new ItemStack(seed);
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        var fruityState = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(this)
                .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder
                        .properties()
                        .hasProperty(SHAPE, BlockProperties.TripleShape.BOTTOM));


        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(seed)
                                     .when(fruityState.and(provider.shearsOrSilkTouchCondition()))
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                     .otherwise(LootItem.lootTableItem(seed)
                                                        .when(ExplosionCondition.survivesExplosion())
                                                        .when(BonusLevelTableCondition.bonusLevelFlatChance(provider.fortune(), LootLookupProvider.VANILLA_LEAVES_SAPLING_CHANCES))
                                     )
                        )
                )
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(NetherItems.GLOWSTONE_PILE)
                                     .when(fruityState.and(provider.shearsOrHoeSilkTouchCondition()))
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        )
                );
    }
}