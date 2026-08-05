package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherFoodItems;
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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;

import java.util.List;
import java.util.function.Supplier;

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
 * <p>
 * The same applies to the third path, a hand-authored
 * {@code src/main/resources/data/betternether/loot_table/blocks/<id>.json}: that file must be deleted in the
 * same change that adds the block's trait, or {@code checkDuplicateAssets} fails on the regenerated copy in
 * {@code src/main/generated}.
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


    /**
     * Only the head of a multi-block plant drops, whatever segment was broken.
     * <p>
     * A wisp is up to four blocks tall and every one of them dropping meant a single plant yielded four
     * items, so a short walk left you with a stack. Keyed on the shape property rather than on the block
     * so the stalk segments are simply worth nothing.
     */
    public static LootTableTrait onlyTopDrops() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition
                                .hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder
                                        .properties()
                                        .hasProperty(
                                                BlockProperties.TRIPLE_SHAPE,
                                                BlockProperties.TripleShape.TOP
                                        )))
                        .add(LootItem.lootTableItem(block))));
    }

    /** Terrain/mycelium cover blocks: silk-touch drops the block, otherwise plain netherrack. */
    public static LootTableTrait terrain() {
        return terrain(Blocks.NETHERRACK);
    }

    /**
     * Terrain cover blocks over ground other than netherrack: silk-touch drops the block, otherwise
     * {@code base}. The gloomsculk variants sit on sculk, so stripping the cover off them should leave
     * sculk behind rather than netherrack.
     */
    public static LootTableTrait terrain(Block base) {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) ->
                provider.dropWithSilkTouch(block, base, ConstantValue.exactly(1)));
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
    // Recovered hand-authored tables.
    //
    // The 17 tables below used to live as hand-written json under
    // src/main/resources/data/betternether/loot_table/blocks/. They load and drop correctly on 26.1 - the
    // schema they use is still the one datagen emits - so moving them here changes nothing about what the
    // blocks drop (bar the three deliberate corrections called out in the javadoc below). The point is to
    // remove the second path: block loot is generated from the trait, and a hand-maintained copy in a second
    // schema is a bug waiting to happen. It duly happened on 26.3, where the loot-entry schema changed and
    // the plural "conditions"/"functions" keys on these files became unknown map keys that the codec drops
    // without an error - silently discarding every condition and every set_count on them.
    // ------------------------------------------------------------------------------------------------------

    /**
     * {@code minecraft:block_state_property} on a single enum/{@link StringRepresentable} state property -
     * the condition every one of these tables is built around.
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

    /** {@link #stateIs} for a boolean property. */
    private static LootItemCondition.Builder stateIs(Block block, Property<Boolean> property, boolean value) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value));
    }

    /** A one-roll pool with a single entry, gated only on {@code survives_explosion}. */
    private static LootPool.Builder survivingPool(ItemLike drop) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(ExplosionCondition.survivesExplosion())
                .add(LootItem.lootTableItem(drop));
    }

    /**
     * A one-roll pool yielding {@code count} of {@code drop}, gated on the block sitting at {@code age} AND on
     * {@code survives_explosion}. The count is an entry-level {@code set_count}, matching the source json.
     */
    private static LootPool.Builder ripePool(
            Block block,
            Property<Integer> age,
            int ripeAge,
            ItemLike drop,
            NumberProvider count
    ) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(stateIs(block, age, ripeAge))
                .when(ExplosionCondition.survivesExplosion())
                .add(LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(count)));
    }

    /**
     * The shape shared by {@code bone_mushroom}, {@code feather_fern}, {@code magma_flower} and
     * {@code orange_mushroom}: one ripe-only pool per {@code ripeDrops} harvest item, then a ripe-only pool of
     * {@code ripeSelfCount} extra copies of the plant, then an always-on pool of a single plant. Every pool
     * carries {@code survives_explosion}, so a fully grown plant blown up by a ghast yields nothing.
     */
    private static LootTable.Builder ripePlant(
            Block block,
            Property<Integer> age,
            int ripeAge,
            NumberProvider ripeSelfCount,
            List<LootLookupProvider.DropInfo> ripeDrops
    ) {
        final LootTable.Builder table = LootTable.lootTable();
        for (LootLookupProvider.DropInfo drop : ripeDrops) {
            table.withPool(ripePool(block, age, ripeAge, drop.item(), drop.numberProvider()));
        }
        return table
                .withPool(ripePool(block, age, ripeAge, block, ripeSelfCount))
                .withPool(survivingPool(block));
    }

    /**
     * A single unconditional drop of some <i>other</i> block, gated only on {@code survives_explosion}:
     * {@code veined_sand} (soul sand), {@code wart_roots} (wart log) and {@code willow_trunk} (willow log) -
     * all three are worldgen blocks that yield their material rather than themselves.
     * <p>
     * {@code drop} is a {@link Supplier} because it names a registry field other than the block being broken;
     * per the class contract that read must not happen before the lambda runs.
     */
    public static LootTableTrait dropOther(Supplier<? extends ItemLike> drop) {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(survivingPool(drop.get())));
    }

    /**
     * Black apple: a ripe ({@code age=3}) bush yields one apple, and any bush yields one seed. Note the apple
     * pool has no {@code set_count} - unlike its siblings, the ripe drop here is always exactly one.
     */
    public static LootTableTrait blackApple() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(stateIs(block, BlockCommonPlant.AGE, 3))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(NetherFoodItems.BLACK_APPLE)))
                .withPool(survivingPool(NetherCropBlocks.BLACK_APPLE_SEED)));
    }

    /**
     * Bone mushroom: ripe is {@code age=2} here (the property is {@code AGE_THREE}, i.e. vanilla's 0..2), and
     * a ripe one adds 1-3 bone meal plus 1-2 extra mushrooms on top of the always-dropped one.
     */
    public static LootTableTrait boneMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> ripePlant(
                block, BlockBoneMushroom.AGE, 2,
                UniformGenerator.between(1, 2),
                List.of(new LootLookupProvider.DropInfo(Items.BONE_MEAL, UniformGenerator.between(1, 3)))
        ));
    }

    /** Feather fern: a ripe ({@code age=3}) fern adds 1-4 feathers and 1-2 extra ferns. */
    public static LootTableTrait featherFern() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> ripePlant(
                block, BlockCommonPlant.AGE, 3,
                UniformGenerator.between(1, 2),
                List.of(new LootLookupProvider.DropInfo(Items.FEATHER, UniformGenerator.between(1, 4)))
        ));
    }

    /** Magma flower: a ripe ({@code age=3}) flower adds 1-4 magma cream and 1-2 extra flowers. */
    public static LootTableTrait magmaFlower() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> ripePlant(
                block, BlockCommonPlant.AGE, 3,
                UniformGenerator.between(1, 2),
                List.of(new LootLookupProvider.DropInfo(Items.MAGMA_CREAM, UniformGenerator.between(1, 4)))
        ));
    }

    /**
     * Orange mushroom: a ripe ({@code age=3}) one adds 1-3 orange dye, plus 1-2 extra mushrooms.
     * <p>
     * The hand-authored table this replaced also dropped 1-3 <i>purple</i> dye alongside the orange, which
     * an orange mushroom has no reason to produce. Dropped deliberately.
     */
    public static LootTableTrait orangeMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> ripePlant(
                block, BlockCommonPlant.AGE, 3,
                UniformGenerator.between(1, 2),
                List.of(new LootLookupProvider.DropInfo(Items.ORANGE_DYE, UniformGenerator.between(1, 3)))
        ));
    }

    /**
     * Ink bush: the bush has no item of its own, so it drops seeds - one always, plus 2-4 more when ripe
     * ({@code age=3}).
     */
    public static LootTableTrait inkBush() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(ripePool(
                        block, BlockCommonPlant.AGE, 3,
                        NetherPlantBlocks.INK_BUSH_SEED, UniformGenerator.between(2, 4)))
                .withPool(survivingPool(NetherPlantBlocks.INK_BUSH_SEED)));
    }

    /** Eyeball: 2-4 slime balls and 1-2 eye seeds, unconditionally. */
    public static LootTableTrait eyeball() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(NetherPlantBlocks.EYE_SEED)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))));
    }

    /** Small eyeball: 1-2 slime balls and exactly one eye seed. */
    public static LootTableTrait eyeballSmall() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(survivingPool(NetherPlantBlocks.EYE_SEED)));
    }

    /**
     * Red large mushroom: the cap ({@code shape=top}) yields 2-4 red mushrooms, every other segment yields a
     * nether mushroom stem.
     * <p>
     * The stem fallback carries {@code survives_explosion} like every other entry here. The hand-authored
     * table this replaced omitted it, so blowing up a red mushroom dropped stems while the otherwise
     * identical brown one dropped nothing - inconsistent, and fixed deliberately.
     */
    public static LootTableTrait redLargeMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                                     .when(stateIs(
                                             block,
                                             BlockRedLargeMushroom.SHAPE, BlockProperties.TripleShape.TOP))
                                     .when(ExplosionCondition.survivesExplosion())
                                     .otherwise(LootItem
                                             .lootTableItem(NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem())
                                             .when(ExplosionCondition.survivesExplosion())))));
    }

    /**
     * Brown large mushroom: the {@code middle} and {@code bottom} segments yield a nether mushroom stem, and
     * everything else (i.e. the cap shapes) yields 1-3 brown mushrooms. Reproduced as a flat three-child
     * {@code alternatives} to match the source; the two stem children are identical apart from their shape.
     */
    public static LootTableTrait brownLargeMushroom() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final Block stem = NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem();
            return LootTable
                    .lootTable()
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(AlternativesEntry.alternatives(
                                    LootItem.lootTableItem(stem)
                                            .when(stateIs(
                                                    block, BlockBrownLargeMushroom.SHAPE,
                                                    BNBlockProperties.BrownMushroomShape.MIDDLE))
                                            .when(ExplosionCondition.survivesExplosion()),
                                    LootItem.lootTableItem(stem)
                                            .when(stateIs(
                                                    block, BlockBrownLargeMushroom.SHAPE,
                                                    BNBlockProperties.BrownMushroomShape.BOTTOM))
                                            .when(ExplosionCondition.survivesExplosion()),
                                    LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                            .when(ExplosionCondition.survivesExplosion())
                            )));
        });
    }

    /**
     * Giant mold: the {@code top} segment yields 1-3 mold saplings and, from a second pool, 2-8 string; every
     * other segment yields a nether mushroom stem.
     * <p>
     * As with {@link #redLargeMushroom()}, the stem fallback now carries {@code survives_explosion}; the
     * hand-authored table omitted it.
     */
    public static LootTableTrait giantMold() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(NetherSaplingBlocks.GIANT_MOLD_SAPLING)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                     .when(stateIs(
                                             block,
                                             BlockGiantMold.SHAPE, BlockProperties.TripleShape.TOP))
                                     .when(ExplosionCondition.survivesExplosion())
                                     .otherwise(LootItem
                                             .lootTableItem(NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem())
                                             .when(ExplosionCondition.survivesExplosion()))))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.STRING)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))
                                     .when(stateIs(
                                             block,
                                             BlockGiantMold.SHAPE, BlockProperties.TripleShape.TOP))
                                     .when(ExplosionCondition.survivesExplosion()))));
    }

    /**
     * Mushroom fir trunk: the {@code bottom}/{@code middle}/{@code top} segments each yield one mushroom fir
     * stem; every other shape (the {@code side_*} branches and {@code end}) falls through to 0-2 saplings.
     * The three stem children are identical apart from their shape condition and are kept separate only to
     * mirror the source table.
     */
    public static LootTableTrait mushroomFirTrunk() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            final Block stem = NetherWoodBlocks.MAT_MUSHROOM_FIR.getStem();
            return LootTable
                    .lootTable()
                    .withPool(LootPool
                            .lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(AlternativesEntry.alternatives(
                                    LootItem.lootTableItem(stem)
                                            .when(stateIs(
                                                    block, BlockMushroomFir.SHAPE,
                                                    BlockMushroomFir.MushroomFirShape.BOTTOM))
                                            .when(ExplosionCondition.survivesExplosion()),
                                    LootItem.lootTableItem(stem)
                                            .when(stateIs(
                                                    block, BlockMushroomFir.SHAPE,
                                                    BlockMushroomFir.MushroomFirShape.MIDDLE))
                                            .when(ExplosionCondition.survivesExplosion()),
                                    LootItem.lootTableItem(stem)
                                            .when(stateIs(
                                                    block, BlockMushroomFir.SHAPE,
                                                    BlockMushroomFir.MushroomFirShape.TOP))
                                            .when(ExplosionCondition.survivesExplosion()),
                                    LootItem.lootTableItem(NetherWoodBlocks.MAT_MUSHROOM_FIR.getSapling())
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                            )));
        });
    }

    /**
     * Stalagnate trunk: always one stalagnate stem, plus a 25% chance of a stalagnate seed. The random chance
     * is an entry-level condition inside a pool that itself only carries {@code survives_explosion}.
     */
    public static LootTableTrait stalagnateTrunk() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(survivingPool(NetherWoodBlocks.MAT_STALAGNATE.getStem()))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(NetherWoodBlocks.MAT_STALAGNATE.getSeed())
                                     .when(LootItemRandomChanceCondition.randomChance(0.25F)))));
    }

    /**
     * Pig statue respawner: only the lower half ({@code top=false}) drops the statue, so a two-block statue
     * yields exactly one item.
     */
    public static LootTableTrait pigStatueRespawner() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(stateIs(block, BlockStatueRespawner.TOP, false))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(block))));
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

    /**
     * Stalagnate bowl: drops whatever its {@code food} property names - the empty bowl, or one of the three
     * filled ones.
     * <p>
     * {@code c5ba1c8f} kept this block's {@code getDrops} override on the grounds that a loot table cannot
     * drop "the item named by its own state property". It can: the property is a four-value enum, so four
     * pools, each conditioned on one value, cover it exhaustively. What the override actually needed was for
     * {@code FoodShape.getItem()} to be resolvable, and it is by the time datagen runs - every
     * {@link org.betterx.betternether.items.ItemBowlFood} registers itself with its shape from its own
     * constructor, and all four shapes have one.
     */
    public static LootTableTrait stalagnateBowl() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            LootTable.Builder table = LootTable.lootTable();
            for (BNBlockProperties.FoodShape food : BNBlockProperties.FoodShape.values()) {
                table = table.withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(stateIs(block, BlockStalagnateBowl.FOOD, food))
                        .add(LootItem.lootTableItem(food.getItem())));
            }
            return table;
        });
    }

    /**
     * Potted plant: drops whatever its {@code plant} property names.
     * <p>
     * The same story as {@link #stalagnateBowl()}, only wider - twenty-three pools rather than four. Each
     * {@code PottedPlantShape} carries the {@code Supplier<Block>} for its plant in the enum itself, so the
     * mapping resolves for datagen without anything having to be wired up first.
     */
    public static LootTableTrait pottedPlant() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> {
            LootTable.Builder table = LootTable.lootTable();
            for (BNBlockProperties.PottedPlantShape plant : BNBlockProperties.PottedPlantShape.values()) {
                table = table.withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(stateIs(block, BlockPottedPlant.PLANT, plant))
                        .add(LootItem.lootTableItem(plant.getBlock())));
            }
            return table;
        });
    }
}
