package org.betterx.betternether.registry.block;

import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.trait.BlockTraits;
import org.betterx.bclib.blocks.BasePressurePlateBlock;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;

public class NetherMetalBlocks {

    // BlockCincinnasite used to set MapColor.COLOR_YELLOW via Materials.metal(...) in its own constructor;
    // now that the constructor is pass-through, every direct BlockCincinnasite::new site below chains
    // .mapColor(COLOR_YELLOW) explicitly (WP3.7), plus its own .strength(3.0F, 10.0F).sound(SoundType.IRON)
    // right after NetherMaterial.cincinnasite() (cleanup: former NetherProps-wrapped override, now a visible
    // chained setter - see that method's javadoc). Sites that copy properties from CINCINNASITE_BLOCK
    // (pillar, lantern, ...) inherit mapColor through the copy and need no change there, but still repeat the
    // strength/sound chain since NetherMaterial.cincinnasite()'s own METAL_BLOCK classification would
    // otherwise reset them to the metal default (5/6, METAL sound).
    // BlockCincinnasite's getDrops() override (inherited from BlockBase) always dropped the block itself,
    // unconditionally - no loot table json was ever generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion() at every direct BlockCincinnasite::new site (now Block::new);
    // properties-copy sites (pillar, lantern, ...) get their own loot separately.
    public static final Block CINCINNASITE_BLOCK = NetherBlocks
            .defineBlock("cincinnasite_block", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_FORGED = NetherBlocks
            .defineBlock("cincinnasite_forged", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BlockCincinnasitPillar (a BlockBase subclass) always dropped itself unconditionally via the inherited
    // getDrops() override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion(). The class keeps its real behaviour (the SHAPE blockstate property +
    // updateShape), just off BlockBase now.
    public static final Block CINCINNASITE_PILLAR = NetherBlocks
            .defineBlock(
                    "cincinnasite_pillar",
                    BlockCincinnasitPillar::new
            )
            .replacePropertiesWithCopy(CINCINNASITE_BLOCK)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_BRICKS = NetherBlocks
            .defineBlock("cincinnasite_bricks", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_BRICK_PLATE = NetherBlocks
            .defineBlock(
                    "cincinnasite_brick_plate",
                    Block::new

            ).addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_STAIRS = NetherBlocks
            .defineBlock(
                    "cincinnasite_stairs",
                    p -> new StairBlock(CINCINNASITE_FORGED.defaultBlockState(), p)
            )
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> CINCINNASITE_FORGED))
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(RecipeTraits.stairsFrom(CINCINNASITE_FORGED))
            .buildAndRegister();
    public static final Block CINCINNASITE_SLAB = NetherBlocks
            .defineBlock("cincinnasite_slab", SlabBlock::new)
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slabColumnDouble(
                    BetterNether.C.mk("block/cincinnasite_plate_up"),
                    BetterNether.C.mk("block/cincinnasite_slab")
            ))
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(RecipeTraits.slabFrom(CINCINNASITE_FORGED))
            .buildAndRegister();
    // Former registerBlockDropSelf bundle, inlined at the registration site (WP5.12): the
    // BlockTraits.LOOT_TABLE.dropSelf() the helper baked in unconditionally, made visible.
    public static final Block CINCINNASITE_BUTTON = NetherBlocks
            .defineBlock(
                    "cincinnasite_button",
                    p -> new ButtonBlock(BlockSetType.GOLD, 20, p)
            )
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(NetherModels.button(
                    "block/cincinnasite_button"))
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .buildAndRegister();
    // Dispatch-helper branch resolution (WP5.6): BlockSetType.GOLD.soundType() == SoundType.METAL, so
    // the old dispatch's isMetal(GOLD) was true -> registerPlate's `wooden` was false -> the non-wooden
    // (MINEABLE_WITH_PICKAXE, no WOODEN_PRESSURE_PLATES tag) branch, confirmed against the committed
    // datapack (cincinnasite_plate is in minecraft:mineable/pickaxe, not in wooden_pressure_plates.json).
    // BehaviourHelper.from() retired (WP6.14): that resolution is now the explicit choice below - the
    // dispatch was never going to change (isMetal(BlockSetType) is a pure, frozen function of GOLD).
    public static final Block CINCINNASITE_PLATE = NetherBlocks
            .defineBlock(
                    "cincinnasite_plate",
                    p -> new BasePressurePlateBlock.Metal(CINCINNASITE_FORGED, p, BlockSetType.GOLD)
            )
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(NetherModels.pressurePlate(
                    "block/cincinnasite_plate_up"))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTags(BlockTags.PRESSURE_PLATES)
            .addTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .addTrait(RecipeTraits.plateFrom(CINCINNASITE_FORGED))
            .buildAndRegister();
    public static final Block CINCINNASITE_TILE_LARGE = NetherBlocks
            .defineBlock("cincinnasite_tile_large", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_TILE_SMALL = NetherBlocks
            .defineBlock("cincinnasite_tile_small", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_CARVED = NetherBlocks
            .defineBlock("cincinnasite_carved", Block::new)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_WALL = NetherBlocks
            .defineBlock("cincinnasite_wall", WallBlock::new)
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(RecipeTraits.wallFrom(CINCINNASITE_FORGED))
            .buildAndRegister();
    // BNPillar.Metal (WP6.14 sweep) dissolved to plain BNPillar::new - the nested Wood/Stone/Metal shims
    // added nothing beyond BNPillar itself. class= changes Metal -> BNPillar (CLASS-ONLY).
    public static final Block CINCINNASITE_BRICKS_PILLAR = NetherBlocks
            .defineBlock(
                    "cincinnasite_bricks_pillar",
                    BNPillar::new
            )
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            // BNPillar no longer implements DropSelfLootProvider; the loot table it used to get from that
            // interface (a plain self-drop, no survives_explosion - the block is not explosion resistant) is
            // now carried explicitly as a trait so it stays byte-identical.
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();

    public static final Block CINCINNASITE_BARS = NetherBlocks
            .defineBlock(
                    "cincinnasite_bars",
                    p -> new BNPane(p)
            )
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.BARS_BLOCK)
            .addTrait(BlockRenderTraits.RENDER_LAYER.translucent())
            .addTrait(NetherMaterial.cincinnasite())
            .sound(SoundType.IRON)
            .strength(3.0F, 10.0F)
            .buildAndRegister();

    public static final Block CINCINNASITE_CHAIN = NetherBlocks
            .defineBlock("cincinnasite_chain", ChainBlock::new)
            .replacePropertiesWithCopy(CINCINNASITE_FORGED)
            .addTrait(BlockTraits.CHAIN_BLOCK)
            .addTrait(ModelTraitLibrary.chain())
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .buildAndRegister();
    // BlockNetherRuby (a BlockBase shim) always dropped itself unconditionally via the inherited getDrops()
    // override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion().
    // WP8.3 (REVIEWED): classified as NetherMaterial.metal() rather than .stone() - its own
    // nether_ruby_slab/nether_ruby_stairs already use NetherMaterial.metal() (IRON_XYLOPHONE), so the base
    // block's previous BASEDRUM (stone) instrument was internally inconsistent (vanilla-alignment review).
    // The gem-block toughness (5/6) is kept via the explicit strength() chained after the trait, since
    // METAL_BLOCK's material default (5/6) already matches it anyway.
    public static final Block NETHER_RUBY_BLOCK = NetherBlocks
            .defineBlock("nether_ruby_block", Block::new)
            .replacePropertiesWithCopy(Blocks.DIAMOND_BLOCK)
            .addTrait(NetherMaterial.metal())
            .strength(5.0f, 6.0f)
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block NETHER_RUBY_STAIRS = NetherBlocks
            .defineBlock(
                    "nether_ruby_stairs",
                    p -> new StairBlock(NETHER_RUBY_BLOCK.defaultBlockState(), p)
            )
            .replacePropertiesWithCopy(NETHER_RUBY_BLOCK)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> NETHER_RUBY_BLOCK))
            .addTrait(NetherMaterial.metal())
            .addTrait(RecipeTraits.stairsFrom(NETHER_RUBY_BLOCK))
            .buildAndRegister();
    public static final Block NETHER_RUBY_SLAB = NetherBlocks
            .defineBlock("nether_ruby_slab", SlabBlock::new)
            .replacePropertiesWithCopy(NETHER_RUBY_BLOCK)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slabColumnDouble(
                    BetterNether.C.mk("block/nether_ruby_block"),
                    BetterNether.C.mk("block/nether_ruby_slab_side")

            ))
            .addTrait(NetherMaterial.metal())
            .addTrait(RecipeTraits.slabFrom(NETHER_RUBY_BLOCK))
            .buildAndRegister();

    public static void ensureLoaded() {
    }
}
