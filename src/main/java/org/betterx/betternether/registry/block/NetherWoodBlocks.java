package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
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
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.betterx.betternether.registry.NetherBlocks;

public class NetherWoodBlocks {


    // Reed //
    // BlockNetherReed (a BlockBase subclass) always dropped itself unconditionally via the inherited
    // getDrops() override - no loot table json was generated for it. Reproduced explicitly (WP6.9) as
    // NetherLoot.dropSelfNoExplosion(). The class keeps its real behaviour (growth/canSurvive/updateShape),
    // just off BlockBase now.
    // WP: mineable-audit §5 reconciliation: this is the sole wover:mineable/sword member in either mod, and
    // it is left as-is rather than folded into minecraft:sword_efficient. The two tags are different
    // mechanics - wover:mineable/sword (MINEABLE_WITH.needsSword()) marks a sword as a "correct tool" like
    // mineable/pickaxe|axe|hoe do, while sword_efficient is purely a mining-speed bonus that never gates
    // drops - so merging them would conflate an existing, deliberate "sword can interact with this reed"
    // classification with the new speed-bonus convention. nether_reed_stem is not part of the shared
    // VegetationTagTrait plant/vine family this WP extends, so it is untouched either way.
    public static final Block NETHER_REED_STEM = NetherBlocks.defineBlock("nether_reed_stem", BlockNetherReed::new)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(BlockTraits.MINEABLE_WITH.needsSword())
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BlockNetherReed.TOP,
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/nether_reed_stem"))),
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/reeds_top"))),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/nether_reed_stem"))
            ))
            // Former Materials.netherPlant() preset, inlined at the registration site (WP3.1).
            .mapColor(MapColor.COLOR_CYAN)
            .noOcclusion()
            .noCollission()
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
            .randomTicks()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final NetherReedMaterial MAT_REED = new NetherReedMaterial().init();

    // Stalagnate //
    public static final StalagnateMaterial MAT_STALAGNATE = new StalagnateMaterial().init();

    // Willow //
    public static final WillowMaterial MAT_WILLOW = new WillowMaterial().init();

    // Wart //
    public static final WartMaterial MAT_WART = new WartMaterial(
            "wart",
            MapColor.COLOR_RED,
            MapColor.COLOR_RED
    ).init();

    public static final VanillaNetherWood WARPED_WOOD = new VanillaNetherWood(
            "warped",
            Blocks.WARPED_PLANKS.defaultMapColor(),
            MapColor.WARPED_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaNetherWood CRIMSON_WOOD = new VanillaNetherWood(
            "crimson",
            Blocks.CRIMSON_PLANKS.defaultMapColor(),
            MapColor.CRIMSON_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaWood OAK_WOOD = VanillaWood.create("oak", Blocks.RED_WOOL);
    public static final VanillaWood SPRUCE_WOOD = VanillaWood.create("spruce", Blocks.RED_WOOL);
    public static final VanillaWood BIRCH_WOOD = VanillaWood.create("birch", Blocks.RED_WOOL);
    public static final VanillaWood JUNGLE_WOOD = VanillaWood.create("jungle", Blocks.RED_WOOL);
    public static final VanillaWood ACACIA_WOOD = VanillaWood.create("acacia", Blocks.BLACK_WOOL);
    public static final VanillaWood DARK_OAK_WOOD = VanillaWood.create("dark_oak", Blocks.RED_WOOL);
    public static final VanillaWood CHERRY_WOOD = VanillaWood.create("cherry", Blocks.WHITE_WOOL);
    public static final VanillaWood BAMBOO_WOOD = VanillaWood.create("bamboo", Blocks.BROWN_WOOL);
    public static final VanillaWood MANGROVE_WOOD = VanillaWood.create("mangrove", Blocks.BLACK_WOOL);
    // Rubeus //
    public static final RubeusMaterial MAT_RUBEUS = new RubeusMaterial().init();
    // Mushroom Fir //
    public static final MushroomFirMaterial MAT_MUSHROOM_FIR = new MushroomFirMaterial().init();
    // Mushroom //
    public static final NetherMushroomMaterial MAT_NETHER_MUSHROOM = new NetherMushroomMaterial().init();
    // Anchor Tree
    public static final AnchorTreeMaterial MAT_ANCHOR_TREE = new AnchorTreeMaterial().init();
    // Nether Sakura
    public static final NetherSakuraMaterial MAT_NETHER_SAKURA = new NetherSakuraMaterial().init();

    // Gloomwood //
    // The trunk grows through two woods rather than one: a living, bone-crusted upper section and a fully
    // sculked lower one. They are separate materials, not variants of each other, so each gets its own full
    // set - minus the boat on the dark one.
    public static final GloomwoodMaterial MAT_GLOOMWOOD = new GloomwoodMaterial().init();
    public static final GloomwoodDarkMaterial MAT_GLOOMWOOD_DARK = new GloomwoodDarkMaterial().init();

    // The band where the sculk is still taking the trunk over. Not a wood set of its own: only the log, its
    // stripped form and planks exist, and each converts into the matching light or dark block by pairing it
    // with one of that block in the grid (2 in, 2 out - a conversion, not a duplication).
    public static final Block GLOOMWOOD_TRANSITION_STRIPPED_LOG = NetherBlocks
            .defineBlock("gloomwood_transition_stripped_log", RotatedPillarBlock::new)
            .addTrait(BlockTraits.LOG_BLOCK)
            .addTrait(ModelTraitLibrary.log(false))
            .addTags(BlockTags.LOGS)
            .addItemTags(ItemTags.LOGS)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_GRAY)
            .sound(SoundType.STEM)
            .addTrait(transitionConversion(
                    () -> MAT_GLOOMWOOD.getStrippedLog(),
                    () -> MAT_GLOOMWOOD_DARK.getStrippedLog()
            ))
            .buildAndRegister();

    public static final Block GLOOMWOOD_TRANSITION_LOG = NetherBlocks
            .defineBlock("gloomwood_transition_log", RotatedPillarBlock::new)
            // Carry the axis over, like the molten log below does: without it, stripping a sideways
            // transition log stood the stripped one upright.
            .addTrait(BlockTraits.LOG_BLOCK.with(
                    (oldState) -> GLOOMWOOD_TRANSITION_STRIPPED_LOG
                            .defaultBlockState()
                            .setValue(RotatedPillarBlock.AXIS, oldState.getValue(RotatedPillarBlock.AXIS))
            ))
            .addTrait(ModelTraitLibrary.log(false))
            .addTags(BlockTags.LOGS)
            .addItemTags(ItemTags.LOGS)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_GRAY)
            .sound(SoundType.STEM)
            .addTrait(transitionConversion(
                    () -> MAT_GLOOMWOOD.getLog(),
                    () -> MAT_GLOOMWOOD_DARK.getLog()
            ))
            .buildAndRegister();

    /**
     * The dark gloomwood log with lava still in it, from the root of a tree standing on molten ground.
     * <p>
     * A log and nothing more - no planks, stairs or slabs of its own. It strips to an ordinary dark
     * stripped log and crafts to ordinary dark planks, so it is a decorative find rather than a
     * material: whatever you carry it home for, what comes out the other side is the dark wood set.
     * <p>
     * Kept a {@link RotatedPillarBlock} even though its texture is directional. The fissures climb from
     * the bottom edge, so a sideways-placed one reads as heat coming from one end rather than from the
     * ground - odd, but not as odd as a log that refuses to rotate when every other log does.
     */
    public static final Block GLOOMWOOD_DARK_MOLTEN_LOG = NetherBlocks
            .defineBlock("gloomwood_dark_molten_log", BlockMoltenGloomwoodLog::new)
            .addTrait(BlockTraits.LOG_BLOCK.with(
                    (oldState) -> MAT_GLOOMWOOD_DARK
                            .getStrippedLog()
                            .defaultBlockState()
                            .setValue(RotatedPillarBlock.AXIS, oldState.getValue(RotatedPillarBlock.AXIS))
            ))
            // Four weighted variants over the phase-shifted side sprites. A texture animates per sprite,
            // so a single one would have every molten log in a forest pulsing on the same beat; the
            // sprites are the same fissures rolled to four points in the cycle and the model picks one
            // at random. The suffixes may never include "_e" - that is the emissive convention, and
            // gloomwood_dark_molten_log_side_e is already the emissive of the unsuffixed sprite.
            .addTrait(ModelTraitLibrary.log(false, "_b", "_c", "_d"))
            .addTags(BlockTags.LOGS)
            .addItemTags(ItemTags.LOGS)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_BLUE)
            .sound(SoundType.STEM)
            .lightLevel(bs -> 7)
            .addTrait(BlockTraits.RECIPE.with((key, block, context) -> {
                RecipeBuilder.crafting(key.identifier().withSuffix("_to_planks"), MAT_GLOOMWOOD_DARK.getPlanks())
                             .outputCount(4)
                             .shapeless()
                             .addMaterial('l', block)
                             .group("gloomwood_dark_planks")
                             .category(RecipeCategory.BUILDING_BLOCKS)
                             .build(context);
                // Redstone put back into dead wood - the way to make one rather than go looking for it.
                RecipeBuilder.crafting(key.identifier(), block)
                             .shapeless()
                             .addMaterial('l', MAT_GLOOMWOOD_DARK.getLog())
                             .addMaterial('r', Items.REDSTONE)
                             .category(RecipeCategory.BUILDING_BLOCKS)
                             .build(context);
            }))
            .buildAndRegister();

    public static final Block GLOOMWOOD_TRANSITION_PLANKS = NetherBlocks
            .defineBlock("gloomwood_transition_planks", Block::new)
            // Not the plain planks cube: the block's own texture is the light-to-dark gradient, and that only
            // reads as a transition on the side faces. Top and bottom take the flat plank texture of the wood
            // the gradient ends at, so a stack of them also tiles into one continuous run.
            .addTrait(NetherModels.cubeWithBorrowedTopAndBottom(
                    () -> MAT_GLOOMWOOD.getPlanks(),
                    () -> MAT_GLOOMWOOD_DARK.getPlanks()
            ))
            .addTags(BlockTags.PLANKS)
            .addItemTags(ItemTags.PLANKS)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_GRAY)
            .sound(SoundType.NETHER_WOOD)
            .addTrait(transitionConversion(
                    () -> MAT_GLOOMWOOD.getPlanks(),
                    () -> MAT_GLOOMWOOD_DARK.getPlanks()
            ))
            .buildAndRegister();

    /**
     * Both conversion recipes for one transition block: pairing it with a light block yields two of that light
     * block, pairing it with a dark one yields two dark. Two items in, two out, so neither recipe duplicates.
     * <p>
     * The targets are read lazily: the recipe trait is attached while the transition block is still being
     * defined, which is before the materials above have finished handing out their blocks.
     *
     * @param light the light-set block this converts into
     * @param dark  the dark-set block this converts into
     */
    private static BlockRecipeTrait transitionConversion(
            Supplier<Block> light,
            Supplier<Block> dark
    ) {
        return BlockTraits.RECIPE.with((key, block, context) -> {
            RecipeBuilder.crafting(key.identifier().withSuffix("_to_light"), light.get())
                         .outputCount(2)
                         .shapeless()
                         .addMaterial('t', block)
                         .addMaterial('l', light.get())
                         .group("gloomwood_transition")
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .build(context);
            RecipeBuilder.crafting(key.identifier().withSuffix("_to_dark"), dark.get())
                         .outputCount(2)
                         .shapeless()
                         .addMaterial('t', block)
                         .addMaterial('d', dark.get())
                         .group("gloomwood_transition")
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .build(context);
        });
    }

    public static void ensureLoaded() {}
}
