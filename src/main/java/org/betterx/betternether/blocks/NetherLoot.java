package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.block.api.trait.behaviour.LootTableTrait;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;

import java.util.List;

/**
 * What a block drops, as {@link BlockTraits#LOOT_TABLE} traits to hand to a block's definition at
 * registration. Replaces the {@code BlockLootProvider} interface (deprecated for removal in wover): the loot
 * table now lives on the block definition rather than in the class hierarchy, and
 * {@code AutoBlockTraitLootProvider} generates it.
 * <p>
 * These are methods rather than constants for the same reason {@link NetherSurvival}'s are: the tables
 * reference BetterNether's own blocks and items, and a constant would capture them during
 * {@link NetherBlocks}' static init, before they are assigned. The registry reads inside each lambda body are
 * safe regardless - the lambda only runs at datagen time, long after every registry is populated - but the
 * enclosing factory must not hoist them out of the lambda.
 * <p>
 * {@link BlockTraits#LOOT_TABLE} returns {@code null} outside datagen, so these must be combined through
 * {@link NetherTraits}, which drops nulls, rather than {@link List#of}.
 * <p>
 * <b>A block must not carry both paths.</b> They are not mutually exclusive - wover runs
 * {@code AutoBlockLootProvider} (which scans for the deprecated {@code BlockLootProvider} interface) and
 * {@code AutoBlockTraitLootProvider} (which scans for this trait) independently, with no filter between them,
 * so a block carrying both generates its table twice, and the tree only looks clean while both happen to emit
 * the same bytes. A table therefore moves here only once its block has stopped inheriting the interface - for
 * the nether-grass family that meant dropping it from bclib's {@code BasePlantBlock} first.
 */
public class NetherLoot {
    /**
     * A plain self-drop with <b>no</b> {@code survives_explosion} condition.
     * <p>
     * Reproduces byte-for-byte the table bclib's {@code DropSelfLootProvider} generated for a block that is
     * <b>not</b> {@code BehaviourExplosionResistant} (the common case). Deliberately <b>not</b>
     * {@link BlockTraits#LOOT_TABLE}'s {@code dropSelf()} shortcut: that one routes through vanilla's
     * {@code createSingleItemTable}, which adds the {@code survives_explosion} condition and so would change
     * the committed table. Used to move blocks off the deprecated {@code DropSelfLootProvider} interface
     * without regenerating their loot.
     */
    public static LootTableTrait dropSelfNoExplosion() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block))));
    }

    /**
     * The nether-grass family: drops itself, but only when sheared or silk-touched.
     * <p>
     * Reproduces the table {@code BasePlantBlock} generated through the {@code BlockLootProvider} interface,
     * before bclib dropped it. Deliberately <b>not</b> {@link BlockTraits#LOOT_TABLE}'s
     * {@code dropWithSilktouchOrHoeOrShears()} shortcut: despite the near-identical name, that one also
     * accepts a hoe, which is a different table.
     */
    public static LootTableTrait netherGrass() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) ->
                provider.dropWithSilkTouchOrShears(block));
    }

    /** Giant lucis: silk-touch drops itself, otherwise lucis spores and glowstone piles. */
    public static LootTableTrait giantLucis() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> provider.dropWithSilkTouch(
                block,
                List.of(
                        new LootLookupProvider.DropInfo(NetherBlocks.LUCIS_SPORE, UniformGenerator.between(0, 1)),
                        new LootLookupProvider.DropInfo(NetherItems.GLOWSTONE_PILE, UniformGenerator.between(0, 2))
                )
        ));
    }


    /** Terrain/mycelium cover blocks: silk-touch drops the block, otherwise plain netherrack. */
    public static LootTableTrait terrain() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) ->
                provider.dropWithSilkTouch(block, Blocks.NETHERRACK, ConstantValue.exactly(1)));
    }

    /** Nether redstone ore: drops redstone, or itself with silk touch. */
    public static LootTableTrait redstoneOre(int minCount, int maxCount) {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) ->
                provider.dropOre(block, Items.REDSTONE, UniformGenerator.between(minCount, maxCount)));
    }

    /** A generic ore: silk-touch drops the block itself, otherwise a fortune-boosted {@code min..max} of {@code drop}. */
    public static LootTableTrait ore(java.util.function.Supplier<net.minecraft.world.item.Item> drop, int minCount, int maxCount) {
        return BlockTraits.LOOT_TABLE.dropOre(drop, minCount, maxCount);
    }

    /** Black vine (and blooming vine): 1-2 of itself, and only when sheared/hoed/silk-touched. */
    public static LootTableTrait blackVine() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 2.0F))
                        .when(provider.shearsOrHoeSilkTouchCondition())
                        .add(LootItem.lootTableItem(block))));
    }

    /**
     * Whispering gourd vine: any segment but the TOP one is "fruity" and yields 1-2 gourds when sheared or
     * silk-touched; otherwise the vine itself drops on the vanilla leaves-sapling chance table.
     */
    public static LootTableTrait whisperingGourdVine() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final var fruityState = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder
                            .properties()
                            .hasProperty(BaseVineBlock.SHAPE, BlockProperties.TripleShape.TOP))
                    .invert();

            return LootTable.lootTable().withPool(LootPool
                    .lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(NetherBlocks.WHISPERING_GOURD.asItem())
                                 .when(fruityState.and(provider.shearsOrSilkTouchCondition()))
                                 .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                 .otherwise(LootItem.lootTableItem(block.asItem())
                                                    .when(ExplosionCondition.survivesExplosion())
                                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                                            provider.fortune(),
                                                            LootLookupProvider.VANILLA_LEAVES_SAPLING_CHANCES
                                                    ))
                                 )
                    )
            );
        });
    }

    /**
     * Lumabus vine: the BOTTOM segment is the fruity one - it yields 1-3 seeds when sheared/silk-touched and
     * a glowstone pile when sheared/hoed/silk-touched; otherwise a seed drops on the vanilla leaves-sapling
     * chance table. The seed differs per vine, so it is read back off the block rather than baked in.
     */
    public static LootTableTrait lumabusVine() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final var seed = ((BlockLumabusVine) block).getSeed();
            final var fruityState = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder
                            .properties()
                            .hasProperty(BaseVineBlock.SHAPE, BlockProperties.TripleShape.BOTTOM));

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
                                                            .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                                                    provider.fortune(),
                                                                    LootLookupProvider.VANILLA_LEAVES_SAPLING_CHANCES
                                                            ))
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
        });
    }
}
