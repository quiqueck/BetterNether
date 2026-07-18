package org.betterx.betternether.blocks;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;

import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
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
    public static BlockModelTrait basaltBricks() {
        return ModCore.isDatagen() ? Impl.basaltBricks() : null;
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
    public static BlockModelTrait slab(
            Supplier<Block> source,
            ResourceLocation top,
            ResourceLocation bottom,
            ResourceLocation side
    ) {
        return ModCore.isDatagen() ? Impl.slab(source, top, bottom, side) : null;
    }

    /**
     * A slab whose half-slab faces AND whose double-slab (full block) state both use bespoke slab textures,
     * because the doubled block does not match the source block's own texture. The half-slab top/bottom faces
     * and the doubled block's up/down faces use {@code topBottom}; every side face uses {@code side}. The
     * {@code type=double} state is a {@code cube_column} built from those two textures rather than the source
     * block's model (e.g. bone / nether-ruby / cincinnasite / nether-brick-tile slabs, whose doubled block
     * carries the same slab seam texture as the half slab).
     */
    public static BlockModelTrait slabColumnDouble(
            ResourceLocation topBottom,
            ResourceLocation side
    ) {
        return ModCore.isDatagen() ? Impl.slabColumnDouble(topBottom, side) : null;
    }

    /** Soul sandstone stairs, textured top/side/bottom from the soul-sandstone set. */
    public static BlockModelTrait soulSandstoneStairs() {
        return ModCore.isDatagen() ? Impl.soulSandstoneStairs() : null;
    }

    /** Smooth soul sandstone stairs: the top texture on every face. */
    public static BlockModelTrait soulSandstoneSmoothStairs() {
        return ModCore.isDatagen() ? Impl.soulSandstoneSmoothStairs() : null;
    }

    /** Cut soul sandstone stairs. */
    public static BlockModelTrait soulSandstoneCutStairs() {
        return ModCore.isDatagen() ? Impl.soulSandstoneCutStairs() : null;
    }

    /**
     * Reed stairs: the reed planks are axis-aware and top-textured, so the stairs need the plank texture on
     * the sides and a dedicated {@code _top} texture on top, rather than the slot's single-texture default.
     */
    public static BlockModelTrait reedStairs() {
        return ModCore.isDatagen() ? Impl.reedStairs() : null;
    }

    /** A pressure plate using an explicit texture rather than its material block's. */
    public static BlockModelTrait pressurePlate(String texture) {
        return ModCore.isDatagen() ? Impl.pressurePlate(texture) : null;
    }

    /** A button using an explicit texture rather than its material block's. */
    public static BlockModelTrait button(String texture) {
        return ModCore.isDatagen() ? Impl.button(texture) : null;
    }

    /** The cincinnasite chair: a chair block model plus a hand-built item model with a view transform. */
    public static BlockModelTrait chairCincinnasite() {
        return ModCore.isDatagen() ? Impl.chairCincinnasite() : null;
    }

    /** The cincinnasite bar stool. */
    public static BlockModelTrait barStoolCincinnasite() {
        return ModCore.isDatagen() ? Impl.barStoolCincinnasite() : null;
    }

    /** The cincinnasite taburet. */
    public static BlockModelTrait taburetCincinnasite() {
        return ModCore.isDatagen() ? Impl.taburetCincinnasite() : null;
    }

    /**
     * A nether-grass-family block built from {@code <name>_1..<variants>} cross textures - swamp/bone/sepia
     * bone grass (3 variants) and soul grass (2).
     */
    public static BlockModelTrait grass(String name, int variants) {
        return ModCore.isDatagen() ? Impl.grass(name, variants) : null;
    }

    /** Nether grass: two weighted grass fans plus a cross, all equally likely. */
    public static BlockModelTrait netherGrass() {
        return ModCore.isDatagen() ? Impl.netherGrass() : null;
    }

    /** Jungle plant: a weighted mix of two crosses, a crop model and a 4-way-rotated jungle-plant model. */
    public static BlockModelTrait junglePlant() {
        return ModCore.isDatagen() ? Impl.junglePlant() : null;
    }

    /**
     * Quartz glass and its framed/coloured variants. The plain and framed blocks use their own cube texture;
     * every coloured variant re-points {@code quartz_glass_<colour>} at the {@code quartz_stained_glass_<colour>}
     * texture. Plain {@code quartz_glass} additionally gets a hand-built item model off its item texture.
     */
    public static BlockModelTrait quartzGlass() {
        return ModCore.isDatagen() ? Impl.quartzGlass() : null;
    }

    /** The weeping/crying obsidian variants. */
    public static BlockModelTrait obsidianVariants() {
        return ModCore.isDatagen() ? Impl.obsidianVariants() : null;
    }

    /**
     * A dev-only {@link org.betterx.bclib.items.DebugDataItem}, rendered as a flat icon off {@code icon}'s
     * texture.
     * <p>
     * {@code DebugDataItem} is {@code implements ItemModelProvider} and built its model at runtime from the
     * same icon ({@code getItemModel} -> {@code ModelsHelper.createItemModel(icon)}). Runtime model building
     * is gone in 1.21.4+, so without this trait the item's generated model points at a
     * {@code betternether:item/debug/<name>} texture that has never existed.
     *
     * @param icon supplies the item whose texture stands in for the debug item
     */
    public static ItemModelTrait debugItem(Supplier<Item> icon) {
        return ModCore.isDatagen() ? Impl.debugItem(icon) : null;
    }

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static ItemModelTrait debugItem(Supplier<Item> icon) {
            return ModelTraitLibrary.itemModel(icon);
        }

        private static BlockModelTrait grass(String name, int variants) {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    BNModels.provideGrassBlockModels(generator, block, name, variants));
        }

        private static BlockModelTrait netherGrass() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation T1 = BetterNether.C.mk("block/ngrass_1");
                final ResourceLocation T2 = BetterNether.C.mk("block/ngrass_2");
                final ResourceLocation T3 = BetterNether.C.mk("block/ngrass_3");

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
            });
        }

        private static BlockModelTrait junglePlant() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation JP1 = BetterNether.C.mk("block/jungle_plant_1");
                final ResourceLocation JP2 = BetterNether.C.mk("block/jungle_plant_2");
                final ResourceLocation JP3 = BetterNether.C.mk("block/jungle_plant_3");
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
            });
        }

        private static BlockModelTrait quartzGlass() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation resource = TextureMapping.getBlockTexture(block);
                if (!resource.getPath().equals("block/quartz_glass")
                        && !resource.getPath().equals("block/quartz_glass_framed")) {
                    final var model = TexturedModel.CUBE.get(block);
                    final var mapping = WoverBlockModelGenerators.textureMappingOf(
                            TextureSlot.ALL,
                            ResourceLocation.fromNamespaceAndPath(
                                    resource.getNamespace(),
                                    resource.getPath().replace("quartz_glass_", "quartz_stained_glass_")
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

                if (resource.getPath().equals("block/quartz_glass")) {
                    final var mapping = WoverBlockModelGenerators.textureMappingOf(
                            TextureSlot.ALL,
                            BetterNether.C.mk("item/quartz_glass")
                    );
                    final var template = new ModelTemplate(
                            Optional.of(ModelLocationUtils.getModelLocation(block)),
                            Optional.empty(),
                            TextureSlot.ALL
                    );

                    final ResourceLocation itemModel = ModelLocationUtils.getModelLocation(block.asItem());
                    template.create(itemModel, mapping, generator.vanillaGenerator.modelOutput);
                    // The template.create above only writes the item MODEL (models/item/quartz_glass.json)
                    // through modelOutput. Register the item-model DEFINITION (items/quartz_glass.json)
                    // pointing at it via delegateItemModel, which also marks this block as having its item
                    // model provided so the flat-item fallback in NetherModelProvider.bootstrapItemModels
                    // doesn't re-generate (and collide with) this model.
                    generator.delegateItemModel(block, itemModel);
                }
            });
        }

        private static BlockModelTrait obsidianVariants() {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    generator.createObsidianVariants(generator, block));
        }

        private static BlockModelTrait basaltBricks() {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    BNModels.provideSimpleMultiStateBlock(generator, block, "", "_cracked"));
        }

        private static BlockModelTrait slab(
                Supplier<Block> source,
                ResourceLocation top,
                ResourceLocation bottom,
                ResourceLocation side
        ) {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    generator.createSlab(block, source.get(), new TextureMapping()
                            .put(TextureSlot.TOP, top)
                            .put(TextureSlot.BOTTOM, bottom)
                            .put(TextureSlot.SIDE, side)));
        }

        private static BlockModelTrait slabColumnDouble(
                ResourceLocation topBottom,
                ResourceLocation side
        ) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final TextureMapping halfMapping = new TextureMapping()
                        .put(TextureSlot.TOP, topBottom)
                        .put(TextureSlot.BOTTOM, topBottom)
                        .put(TextureSlot.SIDE, side);
                final ResourceLocation bottomModel = ModelTemplates.SLAB_BOTTOM.create(
                        block, halfMapping, generator.vanillaGenerator.modelOutput);
                final ResourceLocation topModel = ModelTemplates.SLAB_TOP.create(
                        block, halfMapping, generator.vanillaGenerator.modelOutput);
                // The doubled block reuses the slab's own textures (up/down = topBottom, sides = side), so it
                // cannot delegate to the source block's model the way ModelTraitLibrary.slab does. cube_column
                // (parent block/cube) binds up/down to #end and the four sides to #side, reproducing the
                // hand-authored block/cube double model's faces exactly.
                final ResourceLocation doubleModel = ModelTemplates.CUBE_COLUMN.createWithSuffix(
                        block, "_double",
                        new TextureMapping()
                                .put(TextureSlot.END, topBottom)
                                .put(TextureSlot.SIDE, side),
                        generator.vanillaGenerator.modelOutput);
                generator.acceptBlockState(BlockModelGenerators.createSlab(
                        block,
                        BlockModelGenerators.plainVariant(bottomModel),
                        BlockModelGenerators.plainVariant(topModel),
                        BlockModelGenerators.plainVariant(doubleModel)));
                generator.delegateItemModel(block, bottomModel);
            });
        }

        private static BlockModelTrait soulSandstoneStairs() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> generator.createStairs(
                    block,
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_slabs"),
                    BetterNether.C.mk("block/soul_sandstone_bottom")
            ));
        }

        private static BlockModelTrait soulSandstoneSmoothStairs() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation top = BetterNether.C.mk("block/soul_sandstone_top");
                generator.createStairs(block, top, top, top);
            });
        }

        private static BlockModelTrait soulSandstoneCutStairs() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation top = BetterNether.C.mk("block/soul_sandstone_top");
                generator.createStairs(block, top, BetterNether.C.mk("block/soul_sandstone_cut_slabs"), top);
            });
        }

        private static BlockModelTrait reedStairs() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final ResourceLocation planks = TextureMapping.getBlockTexture(
                        NetherBlocks.MAT_REED.getBlock(SlotType.PLANKS)
                );
                generator.createStairs(block, planks, planks, BetterNether.C.mk("block/nether_reed_planks_top"));
            });
        }

        private static BlockModelTrait pressurePlate(String texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    generator.createPressurePlate(block, BetterNether.C.mk(texture)));
        }

        private static BlockModelTrait button(String texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    generator.createButton(block, BetterNether.C.mk(texture)));
        }

        private static BlockModelTrait chairCincinnasite() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
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
                        NetherBlocks.CINCINNASITE_FORGED,
                        NetherBlocks.NETHER_BRICK_TILE_LARGE
                );
            });
        }

        private static BlockModelTrait barStoolCincinnasite() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> BCLModels.createBarStoolBlockModel(
                    generator,
                    block,
                    NetherBlocks.CINCINNASITE_FORGED,
                    NetherBlocks.NETHER_BRICK_TILE_LARGE
            ));
        }

        private static BlockModelTrait taburetCincinnasite() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> BCLModels.createTaburetBlockModel(
                    generator,
                    block,
                    NetherBlocks.CINCINNASITE_FORGED
            ));
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
