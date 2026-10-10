package org.betterx.betternether.blocks;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.client.model.ClientBlockModelRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait.ModelFactory;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelKey;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;
import de.ambertation.wover.item.api.trait.ItemTrait;

import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Quadrant;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * BetterNether's own {@link BlockModelTrait} factories, for the blocks whose models are too specific for
 * {@link ModelTraitLibrary}. These replace the per-block entries of {@code NetherModelProvider}'s legacy
 * {@code ModelOverides} map: the model now travels with the block's definition at registration instead of
 * being looked up centrally by block identity.
 * <p>
 * Every method here is meant to be called unconditionally from common registration code, so - exactly like
 * {@link ModelTraitLibrary}, whose shape this copies - this class must stay free of any direct reference to
 * client-only datagen types ({@code WoverBlockModelGenerators} and friends are {@code @Environment(CLIENT)}).
 * Those live in {@link Impl}, which is only ever loaded when {@link ModCore#isDatagen()} is true. A
 * dedicated server that loads a class annotated {@code @Environment(CLIENT)} (or one transitively dragging
 * in vanilla client-only types) throws during verification, whether or not the code path is reachable -
 * and javac does NOT copy {@code @Environment} onto the synthetic method it generates for a lambda body,
 * so the lambdas themselves must live inside {@link Impl} rather than in a merely annotated method here.
 */
public class NetherModels {
    /** Basalt bricks: a plain block plus a randomised {@code _cracked} variant. */
    public static BlockModelBinding basaltBricks() {
        return BlockModelBinding.of(MODEL_BASALT_BRICKS, null);
    }

    /**
     * A slab whose half-slab faces use explicit textures (not simply the source block's own texture), but
     * whose double-slab (full block) state reuses the plain source block's model. Use this for slabs whose
     * bottom/top/side faces are dedicated slab textures while the doubled block is genuinely the source block
     * (e.g. soul-sandstone slabs, whose double is the full soul-sandstone block).
     * <p>
     * {@code top}/{@code bottom}/{@code side} are the per-face textures of the half-slab models; the standard
     * generator derives the {@code type=top} state as a {@code slab_top} model from the same mapping (render-
     * equivalent to the legacy "bottom model rotated x=180 uvlock" form).
     *
     * @param source supplies the block whose own model backs the {@code type=double} state
     */
    public static BlockModelBinding slab(
            Supplier<Block> source,
            Identifier top,
            Identifier bottom,
            Identifier side
    ) {
        return BlockModelBinding.of(MODEL_SLAB, new SlabParams(source, top, bottom, side));
    }

    /**
     * A slab whose half-slab faces AND whose double-slab (full block) state both use bespoke slab textures,
     * because the doubled block does not match the source block's own texture. The half-slab top/bottom faces
     * and the doubled block's up/down faces use {@code topBottom}; every side face uses {@code side}. The
     * {@code type=double} state is a {@code cube_column} built from those two textures rather than the source
     * block's model (e.g. bone / nether-ruby / cincinnasite / nether-brick-tile slabs, whose doubled block
     * carries the same slab seam texture as the half slab).
     */
    public static BlockModelBinding slabColumnDouble(
            Identifier topBottom,
            Identifier side
    ) {
        return BlockModelBinding.of(MODEL_SLAB_COLUMN_DOUBLE, new SlabColumnDoubleParams(topBottom, side));
    }

    /** Soul sandstone stairs, textured top/side/bottom from the soul-sandstone set. */
    public static BlockModelBinding soulSandstoneStairs() {
        return BlockModelBinding.of(MODEL_SOUL_SANDSTONE_STAIRS, null);
    }

    /** Smooth soul sandstone stairs: the top texture on every face. */
    public static BlockModelBinding soulSandstoneSmoothStairs() {
        return BlockModelBinding.of(MODEL_SOUL_SANDSTONE_SMOOTH_STAIRS, null);
    }

    /** Cut soul sandstone stairs. */
    public static BlockModelBinding soulSandstoneCutStairs() {
        return BlockModelBinding.of(MODEL_SOUL_SANDSTONE_CUT_STAIRS, null);
    }

    /**
     * Reed stairs: the reed planks are axis-aware and top-textured, so the stairs need the plank texture on
     * the sides and a dedicated {@code _top} texture on top, rather than the slot's single-texture default.
     */
    public static BlockModelBinding reedStairs() {
        return BlockModelBinding.of(MODEL_REED_STAIRS, null);
    }

    /** A pressure plate using an explicit texture rather than its material block's. */
    public static BlockModelBinding pressurePlate(String texture) {
        return BlockModelBinding.of(MODEL_PRESSURE_PLATE, new PressurePlateParams(texture));
    }

    /** A button using an explicit texture rather than its material block's. */
    public static BlockModelBinding button(String texture) {
        return BlockModelBinding.of(MODEL_BUTTON, new ButtonParams(texture));
    }

    /** The cincinnasite chair: a chair block model plus a hand-built item model with a view transform. */
    public static BlockModelBinding chairCincinnasite() {
        return BlockModelBinding.of(MODEL_CHAIR_CINCINNASITE, null);
    }

    /** The cincinnasite bar stool. */
    public static BlockModelBinding barStoolCincinnasite() {
        return BlockModelBinding.of(MODEL_BAR_STOOL_CINCINNASITE, null);
    }

    /** The cincinnasite taburet. */
    public static BlockModelBinding taburetCincinnasite() {
        return BlockModelBinding.of(MODEL_TABURET_CINCINNASITE, null);
    }

    /**
     * A nether-grass-family block built from {@code <name>_1..<variants>} cross textures - swamp/bone/sepia
     * bone grass (3 variants) and soul grass (2).
     */
    public static BlockModelBinding grass(String name, int variants) {
        return BlockModelBinding.of(MODEL_GRASS, new GrassParams(name, variants));
    }

    /** Nether grass: two weighted grass fans plus a cross, all equally likely. */
    public static BlockModelBinding netherGrass() {
        return BlockModelBinding.of(MODEL_NETHER_GRASS, null);
    }

    /**
     * A terrain cover block: {@code <name>_top} / {@code <name>_side} over the given block's texture on the
     * bottom face, with the top randomly rotated in four directions to break up the repeat.
     * <p>
     * The older terrain blocks here ({@code netherrack_moss}, {@code nether_mycelium}, ...) still carry
     * hand-authored blockstate and model json in resources; this is the generated equivalent, and new
     * terrain should use it.
     *
     * @param bottom supplies the block whose texture covers the bottom face - a supplier because the
     *               bottom is usually another registered block, which may not exist yet when the trait is
     *               built at class-init time
     */
    public static BlockModelBinding terrainCover(Supplier<Block> bottom) {
        return BlockModelBinding.of(MODEL_TERRAIN_COVER, new TerrainCoverParams(bottom));
    }

    /**
     * A terrain cover block with several interchangeable texture sets, each emitted in the same four
     * rotations {@link #terrainCover(Supplier)} uses.
     * <p>
     * Rotating one texture only moves a pattern around; every block still animates off the same sprite, so
     * an animated cover pulses in lockstep across the whole floor. Separate sprites per variant give the
     * animation somewhere to differ - {@code bleached_gloomsculk} puts its glimmers on different pixels and
     * a different phase in each one.
     *
     * @param name     the block's texture base name; variants read {@code <name>_top<suffix>} and
     *                 {@code <name>_side<suffix>}
     * @param bottom   the texture for the bottom face
     * @param suffixes one per variant, in order; {@code ""} for the unsuffixed base pair
     */
    public static BlockModelBinding terrainCoverVariants(String name, Identifier bottom, List<String> suffixes) {
        final List<WeightedTemplateModelTrait.Layer> variants = new java.util.ArrayList<>();
        for (String suffix : suffixes) {
            final var layer = WeightedTemplateModelTrait.child(
                    Identifier.withDefaultNamespace("block/cube_bottom_top"),
                    Map.of(
                            "top", BetterNether.C.mk("block/" + name + "_top" + suffix),
                            "side", BetterNether.C.mk("block/" + name + "_side" + suffix),
                            "bottom", bottom
                    )
            );
            for (int y : new int[]{0, 90, 180, 270}) variants.add(layer.rotated(0, y));
        }
        // The item shows the first variant: child models are named <block>_t0, _t1, ... in first-seen order.
        return WeightedTemplateModelTrait.simple(
                variants,
                WeightedTemplateModelTrait.Item.delegatedTo(BetterNether.C.mk("block/" + name + "_t0"))
        );
    }

    /**
     * A cube whose four side faces carry the block's own texture while the top and bottom faces are borrowed
     * from two other blocks.
     * <p>
     * For a block whose own texture is a vertical gradient between two materials: the gradient belongs on the
     * sides, where it is read as a transition, but on the top and bottom faces it is seen end-on and reads as
     * a smear. Those two faces get the flat texture of whichever material the gradient arrives at - which also
     * means a stack of them tiles, the light top of one meeting the dark bottom of the next.
     *
     * @param top    supplies the block whose texture covers the top face
     * @param bottom supplies the block whose texture covers the bottom face
     */
    public static BlockModelBinding cubeWithBorrowedTopAndBottom(Supplier<Block> top, Supplier<Block> bottom) {
        return BlockModelBinding.of(
                MODEL_CUBE_WITH_BORROWED_TOP_AND_BOTTOM,
                new CubeWithBorrowedTopAndBottomParams(top, bottom)
        );
    }

    /** Jungle plant: a weighted mix of two crosses, a crop model and a 4-way-rotated jungle-plant model. */
    public static BlockModelBinding junglePlant() {
        return BlockModelBinding.of(MODEL_JUNGLE_PLANT, null);
    }

    /**
     * Quartz glass and its framed/coloured variants. The plain and framed blocks use their own cube texture;
     * every coloured variant re-points {@code quartz_glass_<colour>} at the {@code quartz_stained_glass_<colour>}
     * texture. Plain {@code quartz_glass} additionally gets a hand-built item model off its item texture.
     */
    public static BlockModelBinding quartzGlass() {
        return BlockModelBinding.of(MODEL_QUARTZ_GLASS, null);
    }

    /** The weeping/crying obsidian variants. */
    public static BlockModelBinding obsidianVariants() {
        return BlockModelBinding.of(MODEL_OBSIDIAN_VARIANTS, null);
    }

    /**
     * A dev-only {@link org.betterx.bclib.items.DebugDataItem}, rendered as a flat icon off {@code icon}'s
     * texture.
     * <p>
     * {@code DebugDataItem} used to build its model at runtime from the same icon (via the old
     * {@code ItemModelProvider#getItemModel} -> {@code ModelsHelper.createItemModel(icon)}). Runtime model
     * building is gone in 1.21.4+, so without this trait the item's generated model points at a
     * {@code betternether:item/debug/<name>} texture that has never existed.
     *
     * @param icon supplies the item whose texture stands in for the debug item
     */
    public static ItemTrait<Item, ?> debugItem(Supplier<Item> icon) {
        return ModCore.isDatagen() ? Impl.debugItem(icon) : null;
    }

    /**
     * The lumabus-vine blockstate is dispatched over the {@code shape} (top/middle/bottom) property. Its {@code
     * bottom} state is a four-entry weighted list of bulb models where {@code <prefix>_bulb_2/_3/_4} are already
     * texture-swap children (particle+texture) of the hand-authored {@code <prefix>_bulb_1} mesh - those three
     * are generated; {@code bulb_1} stays the kept template. The {@code top} ({@code <prefix>_roots}) and {@code
     * middle} ({@code <prefix>_vine} plus the {@code _vine_mirrored} variant, whose UVs differ in the mesh and so
     * is a kept sibling) models stay hand-authored and are referenced directly. No item ({@code registerBlockNI}).
     * <p>
     * Relocated from {@code NetherBlocks} (WP7.6-7.8, docs/registry-split-map.md section 4): needs no
     * {@code ModCore.isDatagen()}/{@link Impl} gating since it never references client-only datagen types.
     */
    /**
     * The gloomwisp blockstate, dispatched over {@link BlockGloomwispVine#SHAPE}: the wisp's head at the
     * tip, and a generated {@code block/cross} child on one of two stalk textures below it. The stalk
     * directly under the head carries the bright half of the gloomwood gradient and the one below it the
     * dark half, so a full-height wisp reads as a single gradient running down to the ground rather than
     * as the same stalk repeated.
     * <p>
     * Only {@code block/gloomwisp_vine_head} is hand-authored, and for the same reason as the lumabus bulb
     * above - it is a mesh (an upright 10x14x10 box with its own UVs), which no model generator can express.
     * Both stalk models, the four-way rotation and the item model are generated.
     */
    public static BlockModelBinding gloomwispVineModelTrait() {
        final var head = BetterNether.C.mk("block/gloomwisp_vine_head");
        // spelled out rather than WoverBlockModelGenerators.CROSS: that class is @Environment(CLIENT),
        // and this method is not behind the isDatagen() gate, so touching its constant would make a
        // dedicated server resolve a client-only type
        final var cross = Identifier.withDefaultNamespace("block/cross");
        final java.util.function.Function<String, WeightedTemplateModelTrait.Layer> stalk = name -> {
            final var tex = BetterNether.C.mk("block/" + name);
            return WeightedTemplateModelTrait.child(cross, Map.of("cross", tex, "particle", tex));
        };
        // Four blink phases times four rotations.
        //
        // The rotation is what decides which way a wisp looks. The phase is a separate problem with the
        // same cause as the molten log's: a texture animates per sprite, so a single head sprite had
        // every wisp in a stand blinking on the same beat. The four sprites are pixel-identical and
        // differ only in their mcmeta, which is enough - the animation state is per sprite, not per
        // texture content. Each non-default one is a texture-override child of the hand-authored head,
        // so the mesh itself is still declared once.
        //
        // The dispatch is over SHAPE, ROTATION and SPENT together. ROTATION.RANDOM keeps the whole sixteen-entry
        // list, so a worldgen wisp is still the client picking a rotation off the block position exactly as it did
        // before the property existed; each compass value narrows the list to that one rotation's four phases, which
        // is what makes a hand-placed wisp stay where it was aimed. Only the head turns - the stalks are a plain
        // cross, and rotating one would be four identical models - so their cases ignore ROTATION entirely, and
        // being spent is a thing the head does, so they ignore that too.
        //
        // A spent head takes one static face instead of the four phases: gloomwisp_vine_head_spent is the closed-eye
        // frame of the blink strip, cut out as its own sprite with no mcmeta. That is the only way to hold that frame
        // - animation runs per sprite and cannot be paused for one block - and it is also why the phases are pointless
        // there: they exist to desync a blink that a spent wisp is not doing.
        final java.util.function.BiFunction<Integer, Boolean, List<WeightedTemplateModelTrait.Layer>> tipAt = (rot, spent) -> {
            final java.util.List<WeightedTemplateModelTrait.Layer> layers = new java.util.ArrayList<>();
            for (String suffix : spent ? List.of("_spent") : List.of("", "_b", "_c", "_d")) {
                final var face = BetterNether.C.mk("block/gloomwisp_vine_head" + suffix);
                final var layer = suffix.isEmpty()
                        ? WeightedTemplateModelTrait.model(head)
                        : WeightedTemplateModelTrait.child(head, Map.of("face", face, "particle", face));
                if (rot == null) {
                    for (int y : new int[]{0, 90, 180, 270}) layers.add(layer.rotated(0, y));
                } else {
                    layers.add(layer.rotated(0, rot));
                }
            }
            return layers;
        };

        final var cases = new java.util.ArrayList<WeightedTemplateModelTrait.Case3<BlockProperties.TripleShape, WispRotation, Boolean>>();
        for (WispRotation rotation : WispRotation.values()) {
            for (boolean spent : new boolean[]{false, true}) {
                cases.add(WeightedTemplateModelTrait.Case3.of(
                        BlockProperties.TripleShape.TOP,
                        rotation,
                        spent,
                        tipAt.apply(rotation == WispRotation.RANDOM ? null : rotation.yRotation(), spent)
                ));
                cases.add(WeightedTemplateModelTrait.Case3.of(
                        BlockProperties.TripleShape.MIDDLE,
                        rotation,
                        spent,
                        List.of(stalk.apply("gloomwisp_vine_stem"))
                ));
                cases.add(WeightedTemplateModelTrait.Case3.of(
                        BlockProperties.TripleShape.BOTTOM,
                        rotation,
                        spent,
                        List.of(stalk.apply("gloomwisp_vine_stem_lower"))
                ));
            }
        }

        return WeightedTemplateModelTrait.propertyDispatch(
                BlockGloomwispVine.SHAPE,
                BlockGloomwispVine.ROTATION,
                BlockGloomwispVine.SPENT,
                cases,
                // A hand-authored still of the head: the block model's head texture blinks and its soul
                // fire is animated, neither of which belongs on an inventory icon, and the fire sat in
                // front of the face the icon exists to show.
                WeightedTemplateModelTrait.Item.delegatedTo(BetterNether.C.mk("item/gloomwisp_vine"))
        );
    }

    public static BlockModelBinding lumabusVineModelTrait(String prefix) {
        final var roots = BetterNether.C.mk("block/" + prefix + "_roots");
        final var vine = BetterNether.C.mk("block/" + prefix + "_vine");
        final var mirrored = BetterNether.C.mk("block/" + prefix + "_vine_mirrored");
        final var bulb1 = BetterNether.C.mk("block/" + prefix + "_bulb_1");
        final java.util.function.Function<Integer, WeightedTemplateModelTrait.Layer> bulb = i -> {
            final var tex = BetterNether.C.mk("block/" + prefix + "_bulb_" + i);
            return WeightedTemplateModelTrait.child(bulb1, java.util.Map.of("particle", tex, "texture", tex));
        };
        return WeightedTemplateModelTrait.propertyDispatch(
                BlockProperties.TRIPLE_SHAPE,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(
                                BlockProperties.TripleShape.TOP,
                                java.util.List.of(WeightedTemplateModelTrait.model(roots))),
                        WeightedTemplateModelTrait.Case.of(
                                BlockProperties.TripleShape.MIDDLE,
                                java.util.List.of(
                                        WeightedTemplateModelTrait.model(vine),
                                        WeightedTemplateModelTrait.model(mirrored))),
                        WeightedTemplateModelTrait.Case.of(
                                BlockProperties.TripleShape.BOTTOM,
                                java.util.List.of(
                                        WeightedTemplateModelTrait.model(bulb1),
                                        bulb.apply(2), bulb.apply(3), bulb.apply(4)))
                ),
                WeightedTemplateModelTrait.Item.none());
    }

    /**
     * The nether-sakura-leaves blockstate is a 28-entry weighted variant list (equal weight): a base leaf model
     * plus six flower-overlay members, each placed at the four Y rotations. The six flower members are
     * texture-permutation children of just two hand-authored geometry templates - {@code
     * nether_sakura_leaves_flowers_3} (flower box UV {@code [0,0,16,16]}) parents {@code _flowers_1}/{@code _2};
     * {@code nether_sakura_leaves_flowers_6} (flower box UV {@code [16,16,0,0]}) parents {@code _flowers_4}/{@code
     * _5}. The base leaf model, the two flower templates and the custom-display item model stay hand-authored;
     * the four sibling children and the blockstate are generated. Item is kept ({@link
     * WeightedTemplateModelTrait.Item#none()}) because {@code item/nether_sakura_leaves} carries a bespoke display
     * transform the default item fallback preserves.
     * <p>
     * Relocated from {@code NetherBlocks} (WP7.6-7.8, docs/registry-split-map.md section 4).
     */
    /**
     * A plain cube blockstate, model and flat item for a block whose texture is not named after it.
     * <p>
     * Pair it with {@code generateModel = false} on the {@code LeavesBlockTrait} so the default cube
     * model does not also get emitted.
     * <p>
     * Currently unused. It was written for {@code gloomwood_bleached_leaves} back when that block borrowed
     * {@code gloomwood_leaves_alt}'s texture; the two have since been split into their own files, so the
     * bleached leaves now follow the model generator's block-name convention and need no override. Kept
     * because the situation it solves - a block whose texture is not named after it - is a general one.
     */
    public static BlockModelBinding cubeFromTexture(Identifier texture) {
        // Same shape as LeavesBlockTrait.ClientModel, and for the same reason: the lambda below takes a
        // WoverBlockModelGenerators, so merely *creating* it makes the JVM resolve that client-only type
        // at the invokedynamic bootstrap - which throws on a dedicated server before the lambda is ever
        // called. It has to live in a separate @Environment(CLIENT) class that the server never reaches,
        // and the isDatagen() gate is what stops the server executing the bootstrap instruction at all.
        // Models are only ever needed while datagen is running; they ship pre-generated otherwise.
        return BlockModelBinding.of(MODEL_BUILD, new BuildParams(texture));
    }

    private record BuildParams(Identifier texture) {
    }

    private static final ModelKey<BuildParams> MODEL_BUILD = NetherModelKeys.key("nether_models/build");

    private record GrassParams(String name, int variants) {
    }

    private static final ModelKey<GrassParams> MODEL_GRASS = NetherModelKeys.key("nether_models/grass");

    private record TerrainCoverParams(Supplier<Block> bottom) {
    }

    private static final ModelKey<TerrainCoverParams> MODEL_TERRAIN_COVER =
            NetherModelKeys.key("nether_models/terrain_cover");

    private record CubeWithBorrowedTopAndBottomParams(Supplier<Block> top, Supplier<Block> bottom) {
    }

    private static final ModelKey<CubeWithBorrowedTopAndBottomParams> MODEL_CUBE_WITH_BORROWED_TOP_AND_BOTTOM =
            NetherModelKeys.key("nether_models/cube_with_borrowed_top_and_bottom");

    private static final ModelKey<Void> MODEL_NETHER_GRASS = NetherModelKeys.key("nether_models/nether_grass");

    private static final ModelKey<Void> MODEL_JUNGLE_PLANT = NetherModelKeys.key("nether_models/jungle_plant");

    private static final ModelKey<Void> MODEL_QUARTZ_GLASS = NetherModelKeys.key("nether_models/quartz_glass");

    private static final ModelKey<Void> MODEL_OBSIDIAN_VARIANTS =
            NetherModelKeys.key("nether_models/obsidian_variants");

    private static final ModelKey<Void> MODEL_BASALT_BRICKS = NetherModelKeys.key("nether_models/basalt_bricks");

    private record SlabParams(Supplier<Block> source, Identifier top, Identifier bottom, Identifier side) {
    }

    private static final ModelKey<SlabParams> MODEL_SLAB = NetherModelKeys.key("nether_models/slab");

    private record SlabColumnDoubleParams(Identifier topBottom, Identifier side) {
    }

    private static final ModelKey<SlabColumnDoubleParams> MODEL_SLAB_COLUMN_DOUBLE =
            NetherModelKeys.key("nether_models/slab_column_double");

    private static final ModelKey<Void> MODEL_SOUL_SANDSTONE_STAIRS =
            NetherModelKeys.key("nether_models/soul_sandstone_stairs");

    private static final ModelKey<Void> MODEL_SOUL_SANDSTONE_SMOOTH_STAIRS =
            NetherModelKeys.key("nether_models/soul_sandstone_smooth_stairs");

    private static final ModelKey<Void> MODEL_SOUL_SANDSTONE_CUT_STAIRS =
            NetherModelKeys.key("nether_models/soul_sandstone_cut_stairs");

    private static final ModelKey<Void> MODEL_REED_STAIRS = NetherModelKeys.key("nether_models/reed_stairs");

    private record PressurePlateParams(String texture) {
    }

    private static final ModelKey<PressurePlateParams> MODEL_PRESSURE_PLATE =
            NetherModelKeys.key("nether_models/pressure_plate");

    private record ButtonParams(String texture) {
    }

    private static final ModelKey<ButtonParams> MODEL_BUTTON = NetherModelKeys.key("nether_models/button");

    private static final ModelKey<Void> MODEL_CHAIR_CINCINNASITE =
            NetherModelKeys.key("nether_models/chair_cincinnasite");

    private static final ModelKey<Void> MODEL_BAR_STOOL_CINCINNASITE =
            NetherModelKeys.key("nether_models/bar_stool_cincinnasite");

    private static final ModelKey<Void> MODEL_TABURET_CINCINNASITE =
            NetherModelKeys.key("nether_models/taburet_cincinnasite");

    /**
     * Registers the client generators behind this class's model keys. Called from the mod's
     * {@code wover.client.traits} entrypoint.
     */
    @Environment(EnvType.CLIENT)
    public static void registerClientModels() {
        ClientCubeModel.register();
        Impl.register();
    }

    @Environment(EnvType.CLIENT)
    private static class ClientCubeModel {
        private static void register() {
            ClientBlockModelRegistry.register(
                    MODEL_BUILD,
                    (key, block, generator, payload) -> buildFrom(payload).provideBlockModels(key, block, generator)
            );
        }

        private static ModelFactory buildFrom(BuildParams p) {
            return build(p.texture());
        }

        private static ModelFactory build(Identifier texture) {
            return (key, block, generator) -> {
                generator.createSimpleTemplatedBlock(
                        block,
                        ModelTemplates.CUBE_ALL,
                        TextureMapping.cube(new Material(texture))
                );
                generator.createFlatItem(block, texture);
            };
        }
    }

    public static BlockModelBinding netherSakuraLeavesModelTrait() {
        final var base = BetterNether.C.mk("block/nether_sakura_leaves");
        final var tmplP = BetterNether.C.mk("block/nether_sakura_leaves_flowers_3");
        final var tmplM = BetterNether.C.mk("block/nether_sakura_leaves_flowers_6");
        final var nsf1 = BetterNether.C.mk("block/nether_sakura_flowers_1");
        final var nsf2 = BetterNether.C.mk("block/nether_sakura_flowers_2");
        final var nsf3 = BetterNether.C.mk("block/nether_sakura_flowers_3");
        // permutation A (flowers_1 / flowers_4): flowers1=nsf3, flowers2=nsf1, flowers3=nsf2
        final var permA = java.util.Map.of("flowers1", nsf3, "flowers2", nsf1, "flowers3", nsf2);
        // permutation B (flowers_2 / flowers_5): flowers1=nsf2, flowers2=nsf3, flowers3=nsf1
        final var permB = java.util.Map.of("flowers1", nsf2, "flowers2", nsf3, "flowers3", nsf1);
        final java.util.List<WeightedTemplateModelTrait.Layer> members = java.util.List.of(
                WeightedTemplateModelTrait.model(base),
                WeightedTemplateModelTrait.child(tmplP, permA),
                WeightedTemplateModelTrait.child(tmplP, permB),
                WeightedTemplateModelTrait.model(tmplP),
                WeightedTemplateModelTrait.child(tmplM, permA),
                WeightedTemplateModelTrait.child(tmplM, permB),
                WeightedTemplateModelTrait.model(tmplM)
        );
        final java.util.List<WeightedTemplateModelTrait.Layer> variants = new java.util.ArrayList<>();
        for (WeightedTemplateModelTrait.Layer m : members) {
            for (int y : new int[]{0, 90, 180, 270}) {
                variants.add(m.rotated(0, y));
            }
        }
        return WeightedTemplateModelTrait.simple(variants, WeightedTemplateModelTrait.Item.none());
    }

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static void register() {
            ClientBlockModelRegistry.register(
                    MODEL_GRASS,
                    (key, block, generator, payload) -> grassFrom(payload).provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_TERRAIN_COVER,
                    (key, block, generator, payload) -> terrainCoverFrom(payload)
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_CUBE_WITH_BORROWED_TOP_AND_BOTTOM,
                    (key, block, generator, payload) -> cubeWithBorrowedTopAndBottomFrom(payload)
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_NETHER_GRASS,
                    (key, block, generator, payload) -> netherGrass().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_JUNGLE_PLANT,
                    (key, block, generator, payload) -> junglePlant().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_QUARTZ_GLASS,
                    (key, block, generator, payload) -> quartzGlass().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_OBSIDIAN_VARIANTS,
                    (key, block, generator, payload) -> obsidianVariants().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_BASALT_BRICKS,
                    (key, block, generator, payload) -> basaltBricks().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_SLAB,
                    (key, block, generator, payload) -> slabFrom(payload).provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_SLAB_COLUMN_DOUBLE,
                    (key, block, generator, payload) -> slabColumnDoubleFrom(payload)
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_SOUL_SANDSTONE_STAIRS,
                    (key, block, generator, payload) -> soulSandstoneStairs().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_SOUL_SANDSTONE_SMOOTH_STAIRS,
                    (key, block, generator, payload) -> soulSandstoneSmoothStairs()
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_SOUL_SANDSTONE_CUT_STAIRS,
                    (key, block, generator, payload) -> soulSandstoneCutStairs()
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_REED_STAIRS,
                    (key, block, generator, payload) -> reedStairs().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_PRESSURE_PLATE,
                    (key, block, generator, payload) -> pressurePlateFrom(payload)
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_BUTTON,
                    (key, block, generator, payload) -> buttonFrom(payload).provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_CHAIR_CINCINNASITE,
                    (key, block, generator, payload) -> chairCincinnasite().provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_BAR_STOOL_CINCINNASITE,
                    (key, block, generator, payload) -> barStoolCincinnasite()
                            .provideBlockModels(key, block, generator)
            );
            ClientBlockModelRegistry.register(
                    MODEL_TABURET_CINCINNASITE,
                    (key, block, generator, payload) -> taburetCincinnasite().provideBlockModels(key, block, generator)
            );
        }

        private static ModelFactory grassFrom(GrassParams p) {
            return grass(p.name(), p.variants());
        }

        private static ModelFactory terrainCoverFrom(TerrainCoverParams p) {
            return terrainCover(p.bottom());
        }

        private static ModelFactory cubeWithBorrowedTopAndBottomFrom(CubeWithBorrowedTopAndBottomParams p) {
            return cubeWithBorrowedTopAndBottom(p.top(), p.bottom());
        }

        private static ModelFactory slabFrom(SlabParams p) {
            return slab(p.source(), p.top(), p.bottom(), p.side());
        }

        private static ModelFactory slabColumnDoubleFrom(SlabColumnDoubleParams p) {
            return slabColumnDouble(p.topBottom(), p.side());
        }

        private static ModelFactory pressurePlateFrom(PressurePlateParams p) {
            return pressurePlate(p.texture());
        }

        private static ModelFactory buttonFrom(ButtonParams p) {
            return button(p.texture());
        }

        private static ItemTrait<Item, ?> debugItem(Supplier<Item> icon) {
            return ModelTraitLibrary.itemModel(icon);
        }

        private static ModelFactory grass(String name, int variants) {
            return (key, block, generator) ->
                    BNModels.provideGrassBlockModels(generator, block, name, variants);
        }

        private static ModelFactory terrainCover(Supplier<Block> bottom) {
            return (key, block, generator) ->
                    generator.createBlockTopSideBottom(bottom.get(), block, true);
        }

        private static ModelFactory cubeWithBorrowedTopAndBottom(Supplier<Block> top, Supplier<Block> bottom) {
            return (key, block, generator) -> {
                // The suppliers are resolved here rather than when the trait is built: the blocks they point at
                // are usually siblings that have not been registered yet at class-init time.
                final Identifier model = generator.createSimpleTemplatedBlock(
                        block,
                        ModelTemplates.CUBE_BOTTOM_TOP,
                        new TextureMapping()
                                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(top.get()))
                                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(bottom.get()))
                                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
                );
                // block/cube_bottom_top already binds particle to #side, so the item model only has to point
                // at the block model - the same delegation ModelTraitLibrary.planks() does.
                generator.delegateItemModel(block, model);
            };
        }

        private static ModelFactory netherGrass() {
            return (key, block, generator) -> {
                final Identifier T1 = BetterNether.C.mk("block/ngrass_1");
                final Identifier T2 = BetterNether.C.mk("block/ngrass_2");
                final Identifier T3 = BetterNether.C.mk("block/ngrass_3");

                BNModels.createComplex(
                        generator,
                        block,
                        List.of(
                                BNModels.ModelSource.of(
                                        BNModels.GRASS_FAN_MODEL_LOCATION,
                                        "_1",
                                        List.of((id, all) -> new Weighted<>(BlockModelGenerators.plainModel(id), 1)),
                                        BNModels.TextureSource.of(TextureSlot.TEXTURE, T1)
                                ),
                                BNModels.ModelSource.of(
                                        WoverBlockModelGenerators.CROSS,
                                        "_2",
                                        List.of((id, all) -> new Weighted<>(BlockModelGenerators.plainModel(id), 1)),
                                        BNModels.TextureSource.of(TextureSlot.CROSS, T2)
                                ),
                                BNModels.ModelSource.of(
                                        BNModels.GRASS_FAN_MODEL_LOCATION,
                                        "_3",
                                        List.of((id, all) -> new Weighted<>(BlockModelGenerators.plainModel(id), 1)),
                                        BNModels.TextureSource.of(TextureSlot.TEXTURE, T3)
                                )
                        )
                );
            };
        }

        private static ModelFactory junglePlant() {
            return (key, block, generator) -> {
                final Identifier JP1 = BetterNether.C.mk("block/jungle_plant_1");
                final Identifier JP2 = BetterNether.C.mk("block/jungle_plant_2");
                final Identifier JP3 = BetterNether.C.mk("block/jungle_plant_3");
                BNModels.createComplex(
                        generator,
                        block,
                        List.of(
                                BNModels.ModelSource.of(
                                        WoverBlockModelGenerators.CROSS,
                                        "_1_a",
                                        List.of((id, all) -> new Weighted<>(
                                                BlockModelGenerators.plainModel(id),
                                                10
                                        )),
                                        BNModels.TextureSource.of(TextureSlot.CROSS, JP1)
                                ),
                                BNModels.ModelSource.of(
                                        BNModels.CROP_BLOCK_MODEL_LOCATION,
                                        "_1_b",
                                        List.of((id, all) -> new Weighted<>(
                                                BlockModelGenerators.plainModel(id),
                                                10
                                        )),
                                        BNModels.TextureSource.of(TextureSlot.TEXTURE, JP1)
                                ),
                                BNModels.ModelSource.of(
                                        BNModels.JUNGLE_PLANT_MODEL_LOCATION,
                                        "_2",
                                        List.of(
                                                (id, all) -> new Weighted<>(BlockModelGenerators.plainModel(id), 1),
                                                (id, all) -> new Weighted<>(
                                                        BlockModelGenerators.plainModel(id).withYRot(Quadrant.R90),
                                                        1
                                                ),
                                                (id, all) -> new Weighted<>(
                                                        BlockModelGenerators.plainModel(id).withYRot(Quadrant.R180),
                                                        1
                                                ),
                                                (id, all) -> new Weighted<>(
                                                        BlockModelGenerators.plainModel(id).withYRot(Quadrant.R270),
                                                        1
                                                )
                                        ),
                                        BNModels.TextureSource.of(TextureSlot.PARTICLE, JP2),
                                        BNModels.TextureSource.of(TextureSlot.TEXTURE, JP3)
                                ),
                                BNModels.ModelSource.of(
                                        WoverBlockModelGenerators.CROSS,
                                        "_3",
                                        List.of((id, all) -> new Weighted<>(
                                                BlockModelGenerators.plainModel(id),
                                                2
                                        )),
                                        BNModels.TextureSource.of(TextureSlot.CROSS, JP3)
                                )
                        )
                );
            };
        }

        private static ModelFactory quartzGlass() {
            return (key, block, generator) -> {
                final Material resource = TextureMapping.getBlockTexture(block);
                if (!resource.sprite().getPath().equals("block/quartz_glass")
                        && !resource.sprite().getPath().equals("block/quartz_glass_framed")) {
                    final var model = TexturedModel.CUBE.get(block);
                    final var mapping = WoverBlockModelGenerators.textureMappingOf(
                            TextureSlot.ALL,
                            Identifier.fromNamespaceAndPath(
                                    resource.sprite().getNamespace(),
                                    resource.sprite().getPath().replace("quartz_glass_", "quartz_stained_glass_")
                            )
                    );

                    final var loc = model.getTemplate()
                                         .create(block, mapping, generator.vanillaGenerator.modelOutput);

                    generator.acceptBlockState(
                            BlockModelGenerators.createSimpleBlock(
                                    block,
                                    BlockModelGenerators.plainVariant(loc)
                            )
                    );
                } else {
                    generator.modelFor(TexturedModel.CUBE.get(block)).createFullBlock(block);
                }

                if (resource.sprite().getPath().equals("block/quartz_glass")) {
                    final var mapping = WoverBlockModelGenerators.textureMappingOf(
                            TextureSlot.ALL,
                            BetterNether.C.mk("item/quartz_glass")
                    );
                    final var template = new ModelTemplate(
                            Optional.of(ModelLocationUtils.getModelLocation(block)),
                            Optional.empty(),
                            TextureSlot.ALL
                    );

                    final Identifier itemModel = ModelLocationUtils.getModelLocation(block.asItem());
                    template.create(itemModel, mapping, generator.vanillaGenerator.modelOutput);
                    // The template.create above only writes the item MODEL (models/item/quartz_glass.json)
                    // through modelOutput. Register the item-model DEFINITION (items/quartz_glass.json)
                    // pointing at it via delegateItemModel, which also marks this block as having its item
                    // model provided so the flat-item fallback in NetherModelProvider.bootstrapItemModels
                    // doesn't re-generate (and collide with) this model.
                    generator.delegateItemModel(block, itemModel);
                }
            };
        }

        private static ModelFactory obsidianVariants() {
            return (key, block, generator) ->
                    generator.createObsidianVariants(generator, block);
        }

        private static ModelFactory basaltBricks() {
            return (key, block, generator) ->
                    BNModels.provideSimpleMultiStateBlock(generator, block, "", "_cracked");
        }

        private static ModelFactory slab(
                Supplier<Block> source,
                Identifier top,
                Identifier bottom,
                Identifier side
        ) {
            return (key, block, generator) ->
                    generator.createSlab(block, source.get(), new TextureMapping()
                            .put(TextureSlot.TOP, new Material(top))
                            .put(TextureSlot.BOTTOM, new Material(bottom))
                            .put(TextureSlot.SIDE, new Material(side)));
        }

        private static ModelFactory slabColumnDouble(
                Identifier topBottom,
                Identifier side
        ) {
            return (key, block, generator) -> {
                final TextureMapping halfMapping = new TextureMapping()
                        .put(TextureSlot.TOP, new Material(topBottom))
                        .put(TextureSlot.BOTTOM, new Material(topBottom))
                        .put(TextureSlot.SIDE, new Material(side));
                final Identifier bottomModel = ModelTemplates.SLAB_BOTTOM.create(
                        block, halfMapping, generator.vanillaGenerator.modelOutput);
                final Identifier topModel = ModelTemplates.SLAB_TOP.create(
                        block, halfMapping, generator.vanillaGenerator.modelOutput);
                // The doubled block reuses the slab's own textures (up/down = topBottom, sides = side), so it
                // cannot delegate to the source block's model the way ModelTraitLibrary.slab does. cube_column
                // (parent block/cube) binds up/down to #end and the four sides to #side, reproducing the
                // hand-authored block/cube double model's faces exactly.
                final Identifier doubleModel = ModelTemplates.CUBE_COLUMN.createWithSuffix(
                        block, "_double",
                        new TextureMapping()
                                .put(TextureSlot.END, new Material(topBottom))
                                .put(TextureSlot.SIDE, new Material(side)),
                        generator.vanillaGenerator.modelOutput);
                generator.acceptBlockState(BlockModelGenerators.createSlab(
                        block,
                        BlockModelGenerators.plainVariant(bottomModel),
                        BlockModelGenerators.plainVariant(topModel),
                        BlockModelGenerators.plainVariant(doubleModel)));
                generator.delegateItemModel(block, bottomModel);
            };
        }

        private static ModelFactory soulSandstoneStairs() {
            return (key, block, generator) -> generator.createStairs(
                    block,
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_slabs"),
                    BetterNether.C.mk("block/soul_sandstone_bottom")
            );
        }

        private static ModelFactory soulSandstoneSmoothStairs() {
            return (key, block, generator) -> {
                final Identifier top = BetterNether.C.mk("block/soul_sandstone_top");
                generator.createStairs(block, top, top, top);
            };
        }

        private static ModelFactory soulSandstoneCutStairs() {
            return (key, block, generator) -> {
                final Identifier top = BetterNether.C.mk("block/soul_sandstone_top");
                generator.createStairs(block, top, BetterNether.C.mk("block/soul_sandstone_cut_slabs"), top);
            };
        }

        private static ModelFactory reedStairs() {
            return (key, block, generator) -> {
                final Identifier planks = TextureMapping.getBlockTexture(
                        NetherWoodBlocks.MAT_REED.getBlock(SlotType.PLANKS)
                ).sprite();
                generator.createStairs(block, planks, planks, BetterNether.C.mk("block/nether_reed_planks_top"));
            };
        }

        private static ModelFactory pressurePlate(String texture) {
            return (key, block, generator) ->
                    generator.createPressurePlate(block, BetterNether.C.mk(texture));
        }

        private static ModelFactory button(String texture) {
            return (key, block, generator) ->
                    generator.createButton(block, BetterNether.C.mk(texture));
        }

        private static ModelFactory chairCincinnasite() {
            return (key, block, generator) -> {
                //this was a custom Item with a view transform, it is easier to recreate the json instead of finding
                //an appropriate API call that fits this special case...
                generator.acceptModelOutput(ModelLocationUtils.getModelLocation(block.asItem()), () -> {
                    JsonObject root = new JsonObject();
                    JsonObject display = new JsonObject();
                    JsonObject gui = new JsonObject();
                    JsonObject fixed = new JsonObject();

                    root.addProperty("parent", ModelLocationUtils.getModelLocation(block).toString());
                    root.add("display", display);

                    display.add("gui", gui);
                    gui.add("rotation", toArray(30, 45, 0));
                    gui.add("translation", toArray(0, -1.4f, 0));
                    gui.add("scale", toArray(0.625f, 0.625f, 0.625f));

                    display.add("fixed", fixed);
                    fixed.add("rotation", toArray(0, 0, 0));
                    fixed.add("translation", toArray(0, 0, 0));
                    fixed.add("scale", toArray(0.5f, 0.5f, 0.5f));

                    return root;
                });
                // acceptModelOutput above only writes the item MODEL. Register the item-model
                // DEFINITION pointing at it (and mark the block's item model as provided) so the
                // flat-item fallback in bootstrapItemModels doesn't re-generate/collide with it.
                generator.delegateItemModel(block, ModelLocationUtils.getModelLocation(block.asItem()));
                BCLModels.createChairBlockModel(
                        generator,
                        block,
                        NetherMetalBlocks.CINCINNASITE_FORGED,
                        NetherStoneBlocks.NETHER_BRICK_TILE_LARGE
                );
            };
        }

        private static ModelFactory barStoolCincinnasite() {
            return (key, block, generator) -> BCLModels.createBarStoolBlockModel(
                    generator,
                    block,
                    NetherMetalBlocks.CINCINNASITE_FORGED,
                    NetherStoneBlocks.NETHER_BRICK_TILE_LARGE
            );
        }

        private static ModelFactory taburetCincinnasite() {
            return (key, block, generator) -> BCLModels.createTaburetBlockModel(
                    generator,
                    block,
                    NetherMetalBlocks.CINCINNASITE_FORGED
            );
        }

        private static JsonElement toArray(float... values) {
            JsonArray array = new JsonArray(values.length);
            for (float value : values) {
                array.add(value);
            }
            return array;
        }
    }
}
