package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.behaviour.LootTableTrait;
import de.ambertation.wover.loot.api.LootLookupProvider;
import de.ambertation.wover.tag.api.predefined.ToolTags;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.minecraft.advancements.critereon.ItemPredicate;
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
                        new LootLookupProvider.DropInfo(NetherMushroomBlocks.LUCIS_SPORE, UniformGenerator.between(0, 1)),
                        new LootLookupProvider.DropInfo(NetherResourceItems.GLOWSTONE_PILE, UniformGenerator.between(0, 2))
                )
        ));
    }


    /** Terrain/mycelium cover blocks: silk-touch drops the block, otherwise plain netherrack. */
    public static LootTableTrait terrain() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) ->
                provider.dropWithSilkTouch(block, Blocks.NETHERRACK, ConstantValue.exactly(1)));
    }

    // Nether ores (cincinnasite/ruby/lapis/redstone) now use BlockTraits.ORE_BLOCK.dropping(drop, min, max),
    // which bundles the ore classification with the vanilla ore-drop loot trait, so the old redstoneOre()/ore()
    // helpers here are no longer needed.

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
                    .add(LootItem.lootTableItem(NetherCropBlocks.WHISPERING_GOURD.asItem())
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
                            .add(LootItem.lootTableItem(NetherResourceItems.GLOWSTONE_PILE)
                                         .when(fruityState.and(provider.shearsOrHoeSilkTouchCondition()))
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            )
                    );
        });
    }

    // ------------------------------------------------------------------------------------------------------
    // Recovered getDrops() overrides.
    //
    // The tables below replace getDrops(BlockState, LootParams.Builder) overrides in blocks/*.java, which
    // bypassed the loot table entirely at runtime and so were the last second loot path in the mod (see the
    // class javadoc). None of those overrides consulted survives_explosion - most never called
    // super.getDrops() at all - so none of these tables carry it either: the drops are reproduced exactly,
    // including that oddity. MHelper.randRange(min, max) is inclusive at both ends, so it maps onto
    // UniformGenerator.between(min, max) unchanged, and a rolled count of 0 yields an empty stack that both
    // paths discard.
    // ------------------------------------------------------------------------------------------------------

    /**
     * {@code minecraft:block_state_property} on a single enum/{@link StringRepresentable} state property.
     */
    private static <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder stateIs(
            Block block, Property<T> property, T value
    ) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value));
    }

    /** {@link #stateIs} for an {@code age}-style integer property. */
    private static LootItemCondition.Builder stateIs(Block block, Property<Integer> property, int value) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value));
    }

    /** {@link #stateIs} widened to a set of values for one property, as {@code minecraft:any_of}. */
    @SafeVarargs
    private static <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder stateIsAnyOf(
            Block block, Property<T> property, T first, T... rest
    ) {
        LootItemCondition.Builder condition = stateIs(block, property, first);
        for (T value : rest) {
            condition = condition.or(stateIs(block, property, value));
        }
        return condition;
    }

    /** {@code minecraft:match_tool} against an item tag. */
    private static LootItemCondition.Builder toolIsIn(LootLookupProvider provider, TagKey<Item> tag) {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(provider.itemLookup(), tag));
    }

    /** Lucis mushroom: one lucis spore and 2-4 glowstone piles, unconditionally. */
    public static LootTableTrait lucisMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(NetherMushroomBlocks.LUCIS_SPORE)))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(NetherResourceItems.GLOWSTONE_PILE)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))));
    }

    /** Barrel cactus: a ripe ({@code age=3}) one yields 1-3 of itself, any other age exactly one. */
    public static LootTableTrait barrelCactus() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                     .when(stateIs(block, BlockCommonPlant.AGE, 3))
                                     .otherwise(LootItem.lootTableItem(block)))));
    }

    /**
     * Agave: a ripe ({@code age=3}) plant yields 1-2 of itself plus 2-5 agave leaves, any other age exactly
     * one plant and no leaves.
     */
    public static LootTableTrait agave() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final LootItemCondition.Builder ripe = stateIs(block, BlockCommonPlant.AGE, 3);
            return LootTable
                    .lootTable()
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                         .when(ripe)
                                         .otherwise(LootItem.lootTableItem(block))))
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(ripe)
                            .add(LootItem.lootTableItem(NetherResourceItems.AGAVE_LEAF)
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))));
        });
    }

    /**
     * Jellyfish mushroom: the {@code top} cap yields 1-2 saplings, 0-2 glowstone piles and 0-1 slime balls,
     * the {@code bottom} only the 1-2 saplings, and the {@code middle} a single nether mushroom stem.
     */
    public static LootTableTrait jellyfishMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final var shape = BlockJellyfishMushroom.SHAPE;
            final LootItemCondition.Builder top =
                    stateIs(block, shape, BlockProperties.TripleShape.TOP);
            final LootItemCondition.Builder bottom =
                    stateIs(block, shape, BlockProperties.TripleShape.BOTTOM);
            final LootItemCondition.Builder middle =
                    stateIs(block, shape, BlockProperties.TripleShape.MIDDLE);

            return LootTable
                    .lootTable()
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(top.or(bottom))
                            .add(LootItem.lootTableItem(NetherSaplingBlocks.JELLYFISH_MUSHROOM_SAPLING)
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(top)
                            .add(LootItem.lootTableItem(NetherResourceItems.GLOWSTONE_PILE)
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))))
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(top)
                            .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                         .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))))
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(middle)
                            .add(LootItem.lootTableItem(NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem())));
        });
    }

    /** Willow branch: only the {@code end} segment drops anything, a single willow torch. */
    public static LootTableTrait willowBranch() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(stateIs(
                                block,
                                BlockWillowBranch.SHAPE, BNBlockProperties.WillowBranchShape.END))
                        .add(LootItem.lootTableItem(NetherWoodBlocks.MAT_WILLOW.getTorch()))));
    }

    /**
     * Soul lily: every segment yields one nether mushroom stem, and the crown yields a sapling on top -
     * always one for {@code small}, {@code medium_top} and {@code big_top_center}, and 0-1 for the four
     * {@code big_top_side_*}. The stalk shapes ({@code medium_bottom}, {@code big_bottom},
     * {@code big_middle}) yield no sapling at all.
     */
    public static LootTableTrait soulLily() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final var shape = BlockSoulLily.SHAPE;
            final var sapling = NetherSaplingBlocks.SOUL_LILY_SAPLING;
            return LootTable
                    .lootTable()
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem())))
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(sapling)
                                         .when(stateIsAnyOf(
                                                 block, shape,
                                                 BlockSoulLily.SoulLilyShape.SMALL,
                                                 BlockSoulLily.SoulLilyShape.MEDIUM_TOP,
                                                 BlockSoulLily.SoulLilyShape.BIG_TOP_CENTER))
                                         .otherwise(LootItem
                                                 .lootTableItem(sapling)
                                                 .apply(SetItemCountFunction.setCount(UniformGenerator.between(
                                                         0,
                                                         1
                                                 )))
                                                 .when(stateIsAnyOf(
                                                         block, shape,
                                                         BlockSoulLily.SoulLilyShape.BIG_TOP_SIDE_N,
                                                         BlockSoulLily.SoulLilyShape.BIG_TOP_SIDE_S,
                                                         BlockSoulLily.SoulLilyShape.BIG_TOP_SIDE_E,
                                                         BlockSoulLily.SoulLilyShape.BIG_TOP_SIDE_W)))));
        });
    }

    /**
     * Cincinnasite anvil: itself, but only to a pickaxe.
     * <p>
     * The override this replaces gated on {@code LootUtil.isCorrectTool}, i.e. "correct for drops, or any
     * item in the block's mineable tag" - which for this block (METAL_BLOCK, so mineable/pickaxe) is the
     * pickaxe tags below. The condition is not redundant with the block's own
     * {@code requiresCorrectToolForDrops()}: that only gates <i>player</i> mining, and the override
     * deliberately dropped nothing when destroyed with no tool at all, i.e. by an explosion, a piston, or
     * {@code /setblock destroy}.
     */
    public static LootTableTrait cincinnasiteAnvil() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(toolIsIn(provider, ItemTags.PICKAXES)
                                .or(toolIsIn(provider, ToolTags.FABRIC_PICKAXES)))
                        .add(LootItem.lootTableItem(block))));
    }
}
