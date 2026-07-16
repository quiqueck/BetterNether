package org.betterx.betternether.blocks;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.core.api.ModCore;

import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static BlockModelTrait basaltBricks() {
            return ClientBlockTraits.MODEL.with((key, block, generator) ->
                    BNModels.provideSimpleMultiStateBlock(generator, block, "", "_cracked"));
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
