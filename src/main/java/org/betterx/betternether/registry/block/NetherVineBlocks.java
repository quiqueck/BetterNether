package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.DescriptionBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnSolidTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import org.betterx.bclib.furniture.block.BaseBarStool;
import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.bclib.furniture.block.BaseTaburet;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.blocks.complex.*;
import org.betterx.betternether.blocks.complex.slots.VanillaNetherWood;
import org.betterx.betternether.blocks.complex.slots.VanillaWood;
import org.betterx.betternether.registry.features.configured.NetherVines;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.betterx.betternether.registry.NetherBlocks;

public class NetherVineBlocks {

    // Vines //
    // Cutout, for the same reason as NEON_EQUISETUM below: these extend bclib's BaseVineBlock /
    // BaseSimpleVineBlock rather than BlockBase, so they were never IRenderTypeable and the old
    // registerRenderLayers() walk never gave them a layer. Every texture they reach is binary alpha.
    // NetherTraits.vine() replaces the retired BehaviourVine/BehaviourClimableVine markers (mineable
    // hoe/shears, the CLIMBABLE tag and the vine block tag), folding in the CompostableBlockTrait these
    // registrations already carried - see NetherTraits.vine()'s doc-comment for why this isn't bclib's
    // full VineBlockTrait.
    public static final Block BLACK_VINE = NetherBlocks.defineBlock("black_vine", BlockBlackVine::new)
            .addTrait(NetherRender.cutout())
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BaseSimpleVineBlock.BOTTOM,
                    List.of(
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/black_vine")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/black_vine"))
                    ),
                    List.of(
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/black_vine_bottom")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/black_vine_bottom"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("block/black_vine"))
            ))
            .addTrait(NetherLoot.blackVine())
            // Former Materials.staticVine(COLOR_BLACK) preset, folded (category-traits Batch 3) onto
            // NetherMaterial.staticVine() - the trailing instabreak() stays chained since the preset never
            // sets destroyTime/resistance itself, so there's no ordering hazard.
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_BLACK))
            .instabreak()
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    public static final Block BLOOMING_VINE = NetherBlocks.defineBlock("blooming_vine", BlockBlackVine::new)
            .addTrait(NetherRender.cutout())
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BaseSimpleVineBlock.BOTTOM,
                    List.of(
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_1")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_1")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_2")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_2")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_3")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_3"))
                    ),
                    List.of(
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block"), BetterNether.C.mk("block/flowered_vine_bottom_1")),
                            WeightedCrossModelTrait.cropParent(BetterNether.C.mk("block/crop_block_inverted"), BetterNether.C.mk("block/flowered_vine_bottom_1"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/flowered_vine"))
            ))
            .addTrait(NetherLoot.blackVine())
            // Former Materials.staticVine(COLOR_BLACK) preset, folded onto NetherMaterial.staticVine().
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_BLACK))
            .instabreak()
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    // Former registerVine bundle, inlined at the registration site (WP5.8): drops only via silk touch,
    // hoe, or shears.
    public static final Block GOLDEN_VINE = NetherBlocks.defineBlock("golden_vine", BlockGoldenVine::new)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouchOrHoeOrShears())
            .addTrait(NetherRender.cutout())
            // Former Materials.staticVine(COLOR_YELLOW) preset, folded onto NetherMaterial.staticVine().
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_YELLOW))
            .lightLevel(bs -> 15)
            .instabreak()
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BaseSimpleVineBlock.BOTTOM,
                    List.of(
                            WeightedCrossModelTrait.cross(BetterNether.C.mk("block/golden_vine")),
                            WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/golden_vine"))
                    ),
                    List.of(
                            WeightedCrossModelTrait.cross(BetterNether.C.mk("block/golden_vine_bottom")),
                            WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/golden_vine_bottom"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/golden_vine"))
            ))
            .addTrait(NetherTraits.vine())
            .buildAndRegister();

    public static final BlockLumabusVine LUMABUS_VINE = NetherBlocks.defineBlock("lumabus_vine", p -> new BlockLumabusVine(p))
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherModels.lumabusVineModelTrait("lumabus"))
            .addTrait(NetherLoot.lumabusVine())
            // Former Materials.staticVine(COLOR_CYAN) preset, folded onto NetherMaterial.staticVine().
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_CYAN))
            .strength(0.2f)
            .lightLevel(BlockLumabusVine.getLuminance())
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    public static final BlockLumabusVine GOLDEN_LUMABUS_VINE = NetherBlocks.defineBlock("golden_lumabus_vine", p -> new BlockLumabusVine(p))
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherModels.lumabusVineModelTrait("golden_lumabus"))
            .addTrait(NetherLoot.lumabusVine())
            // Former Materials.staticVine(COLOR_YELLOW) preset, folded onto NetherMaterial.staticVine().
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_YELLOW))
            .strength(0.2f)
            .lightLevel(BlockLumabusVine.getLuminance())
            .addTrait(NetherTraits.vine())
            .buildAndRegister();

    /**
     * The vine that hangs off the gloomwood's sculk ceiling: a dark stem in the sculk tones with leaves
     * shading out to the pale bark, and the odd leaf finishing on a hot orange tip.
     */
    public static final Block GLOOMSCULK_VINE = NetherBlocks.defineBlock("gloomsculk_vine", BlockBlackVine::new)
            .addTrait(NetherRender.cutout())
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BaseSimpleVineBlock.BOTTOM,
                    List.of(
                            WeightedCrossModelTrait.cross(BetterNether.C.mk("block/gloomsculk_vine")),
                            WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/gloomsculk_vine"))
                    ),
                    List.of(
                            WeightedCrossModelTrait.cross(BetterNether.C.mk("block/gloomsculk_vine_bottom")),
                            WeightedCrossModelTrait.crossParent(BetterNether.C.mk("block/cross_inverted"), BetterNether.C.mk("block/gloomsculk_vine_bottom"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("block/gloomsculk_vine"))
            ))
            .addTrait(NetherLoot.blackVine())
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_GRAY))
            .instabreak()
            .lightLevel(bs -> 3)
            .addTrait(NetherTraits.vine())
            .buildAndRegister();

    // A gloomwisp: thin stalk, one elongated head with a small face on it. Grows upward off gloomsculk
    // (or any other sculk-like ground) rather than hanging, so it stacks like the nether cactus instead
    // of using the BOTTOM dispatch the vines above share.
    // Stands on anything solid. Worldgen still only grows it on the sculk floor - its placed feature
    // filters on SCULK_LIKE - so this only governs where a player may replant one.
    public static final BlockGloomwispVine GLOOMWISP_VINE = NetherBlocks
            .defineBlock("gloomwisp_vine", BlockGloomwispVine::new)
            .addTrait(NetherRender.cutout())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            // The two things about a wisp that are not visible on the block: PERSISTENT, set by shearing
            // the head, and OFFSET, cleared by placing while sneaking. Both are decisions a builder makes
            // before placing one, so they belong on the item rather than in an advancement.
            .addTrait(DescriptionBlockTrait.of(
                    "tooltip.betternether.gloomwisp_vine.shear",
                    "tooltip.betternether.gloomwisp_vine.center"
            ))
            .addTrait(NetherModels.gloomwispVineModelTrait())
            .addTrait(NetherLoot.onlyTopDrops())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_LIGHT_GRAY))
            // NetherMaterial.plant() is OffsetType.NONE; wisps want the grass-style random horizontal
            // offset so a stand of them does not sit on a visible grid
            .offsetType(BlockBehaviour.OffsetType.XZ)
            // Lit from the head, not along the whole plant: the stalk keeps a dim glow from the
            // gradient in its texture, and the soul fire in the head does the actual lighting.
            .lightLevel(bs -> bs.getValue(BlockGloomwispVine.SHAPE) == BlockProperties.TripleShape.TOP
                    ? 12
                    : 5)
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();

    // Small Plants
    // BlockSoulVein (WP6.12): its getDrops() override always returned a self-drop whether or not the tool
    // check passed - the "else" branch called super.getDrops(), which was BlockBase's inherited
    // unconditional self-drop, not a real fallback. No loot table json was generated for it either,
    // reproduced explicitly as NetherLoot.dropSelfNoExplosion() so the else branch keeps behaving
    // identically now that super.getDrops() is Block's (loot-table-driven) implementation.
    // The override itself is gone now: with both of its branches yielding the same single self-drop, it
    // only restated the table below it.
    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant().
    public static final Block SOUL_VEIN = NetherBlocks.defineBlock("soul_vein", BlockSoulVein::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherSand())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_PURPLE))
            .randomTicks()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    // Cutout, same BaseVineBlock gap as the vines above.
    // Former Materials.staticVine(COLOR_RED) preset, folded onto NetherMaterial.staticVine().
    public static final Block WHISPERING_GOURD_VINE = NetherBlocks.defineBlock("whispering_gourd_vine", BlockWhisperingGourdVine::new)
            .addTrait(NetherRender.cutout())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherLoot.whisperingGourdVine())
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_RED))
            .strength(0.2f)
            .randomTicks()
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    // Former Materials.staticVine(COLOR_GREEN) preset, folded onto NetherMaterial.staticVine().
    public static final Block ANCHOR_TREE_VINE = NetherBlocks.defineBlock("anchor_tree_vine", BlockAnchorTreeVine::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_GREEN))
            .strength(0.2f)
            .noLootTable()
            .lightLevel(BlockAnchorTreeVine::getLuminance)
            .addTrait(WeightedCrossModelTrait.propertyDispatch(
                    BlockAnchorTreeVine.SHAPE,
                    List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine_end_2")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine_end_2"))
                            )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine_end_1")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine_end_1"))
                            )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                                    WeightedCrossModelTrait.cross(BetterNether.C.mk("block/anchor_tree_vine")),
                                    WeightedCrossModelTrait.crossParent(
                                            BetterNether.C.mk("block/cross_inverted"),
                                            BetterNether.C.mk("block/anchor_tree_vine"))
                            ))
                    ),
                    WeightedCrossModelTrait.Item.delegated()
            ))
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    // eye_vine has no block item (its clone item is EYE_SEED); it drops nothing, so no loot table is generated.
    // Cutout, same BaseVineBlock gap as the vines above.
    // Former Materials.staticVine(COLOR_RED) preset, folded onto NetherMaterial.staticVine().
    public static final Block EYE_VINE = NetherBlocks.defineBlock("eye_vine", BlockEyeVine::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.staticVine(MapColor.COLOR_RED))
            .strength(0.2f)
            .addTrait(WeightedCrossModelTrait.simple(
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/eye_vine"))),
                    WeightedCrossModelTrait.Item.delegated()
            ))
            .addTrait(NetherTraits.vine())
            .buildAndRegister();


    // DEFERED BLOCKS //
    // Former Materials.netherSapling() preset shared by the two lumabus seeds below, both of which
    // hardcode the same MapColor.COLOR_RED - folded onto NetherMaterial.sapling().
    // BlockLumabusSeed (WP6.12): both always dropped themselves unconditionally via BlockBase's inherited
    // getDrops() override (no loot table json was generated for either), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    public static final Block LUMABUS_SEED = NetherBlocks.defineBlock(
            "lumabus_seed",
            p -> new BlockLumabusSeed(p, LUMABUS_VINE, () -> NetherVines.LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    )
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_RED))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.seed())
            .buildAndRegister();

    public static final Block GOLDEN_LUMABUS_SEED = NetherBlocks.defineBlock(
            "golden_lumabus_seed",
            p -> new BlockLumabusSeed(p, GOLDEN_LUMABUS_VINE, () -> NetherVines.GOLDEN_LUMABUS_VINE.getHolder(WorldState.registryAccess()))
    )
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_RED))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.seed())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
