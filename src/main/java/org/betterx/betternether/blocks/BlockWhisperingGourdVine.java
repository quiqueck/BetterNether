package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.items.tool.BaseShearsItem;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockWhisperingGourdVine extends BaseVineBlock.Growing implements BlockLootProvider {
    public BlockWhisperingGourdVine() {
        super(
                BehaviourBuilders
                        .createStaticVine(MapColor.COLOR_RED)
                        .randomTicks(),
                6,
                1,
                16
        );
    }

    @Override
    public ItemInteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack tool = player.getItemInHand(hand);
        if (BaseShearsItem.isShear(tool) && state.getValue(SHAPE) == BlockProperties.TripleShape.MIDDLE) {
            if (!world.isClientSide) {
                BlocksHelper.setWithUpdate(world, pos, state.setValue(SHAPE, BlockProperties.TripleShape.BOTTOM));
                world.addFreshEntity(new ItemEntity(
                        world,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        new ItemStack(
                                NetherBlocks.WHISPERING_GOURD)
                ));
                if (world.random.nextBoolean()) {
                    world.addFreshEntity(new ItemEntity(
                            world,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            new ItemStack(NetherBlocks.WHISPERING_GOURD)
                    ));
                }
            }
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.useItemOn(itemStack, state, world, pos, player, hand, hit);
        }
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
                        .hasProperty(SHAPE, BlockProperties.TripleShape.TOP))
                .invert();


        return LootTable.lootTable().withPool(LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(NetherBlocks.WHISPERING_GOURD.asItem())
                             .when(fruityState.and(provider.shearsOrSilkTouchCondition()))
                             .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                             .otherwise(LootItem.lootTableItem(this.asItem())
                                                .when(ExplosionCondition.survivesExplosion())
                                                .when(BonusLevelTableCondition.bonusLevelFlatChance(provider.fortune(), LootLookupProvider.VANILLA_LEAVES_SAPLING_CHANCES))
                             )
                )
        );
    }
}
