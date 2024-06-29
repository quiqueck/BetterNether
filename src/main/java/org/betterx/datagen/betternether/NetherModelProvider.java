package org.betterx.datagen.betternether;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NetherModelProvider extends WoverModelProvider {


    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        final Block reedPlanks = NetherBlocks.MAT_REED.getBlock(WoodSlots.PLANKS);
        final ResourceLocation NETHER_REED_PLANKS = TextureMapping.getBlockTexture(reedPlanks);
        final ResourceLocation NETHER_REED_PLANKS_TOP = BetterNether.C.mk("block/nether_reed_planks_top");

        final ResourceLocation SOUL_SANDSTONE_BOTTOM = BetterNether.C.mk("block/soul_sandstone_bottom");
        final ResourceLocation SOUL_SANDSTONE_TOP = BetterNether.C.mk("block/soul_sandstone_top");
        final ResourceLocation SOUL_SANDSTONE_SLABS = BetterNether.C.mk("block/soul_sandstone_slabs");
        final ResourceLocation SOUL_SANDSTONE_CUT_SLABS = BetterNether.C.mk("block/soul_sandstone_cut_slabs");

        this.addFromRegistry(
                generator,
                BlockRegistry.forMod(BetterNether.C),
                true,
                ModelOverides.create()
                             .override(NetherBlocks.BASALT_BRICKS, block -> BNModels.provideSimpleMultiStateBlock(generator, block, "", "_cracked"))
                             .override(NetherBlocks.MAT_REED.getBlock(WoodSlots.STAIRS), block -> generator.createStairs(block, NETHER_REED_PLANKS, NETHER_REED_PLANKS, NETHER_REED_PLANKS_TOP))
                             .override(NetherBlocks.SOUL_SANDSTONE_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_SLABS, SOUL_SANDSTONE_BOTTOM))
                             .override(NetherBlocks.SOUL_SANDSTONE_SMOOTH_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_TOP))
                             .override(NetherBlocks.SOUL_SANDSTONE_CUT_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_CUT_SLABS, SOUL_SANDSTONE_TOP))
                             .override(NetherBlocks.BONE_PLATE, block -> generator.createPressurePlate(block, BetterNether.C.mk("block/bone_block_plate")))
                             .override(NetherBlocks.BONE_BUTTON, block -> generator.createButton(block, BetterNether.C.mk("block/bone_button")))
                             .override(NetherBlocks.CINCINNASITE_PLATE, block -> generator.createPressurePlate(block, BetterNether.C.mk("block/cincinnasite_plate_up")))
                             .override(NetherBlocks.CINCINNASITE_BUTTON, block -> generator.createButton(block, BetterNether.C.mk("block/cincinnasite_button")))
                             .override(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.PRESSURE_PLATE), block -> generator.createPressurePlate(block, BetterNether.C.mk("block/nether_mushroom_plate")))
                             .override(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.BUTTON), block -> generator.createButton(block, BetterNether.C.mk("block/nether_mushroom_button")))
                             .override(NetherBlocks.CHAIR_CINCINNASITE, block -> {
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
                                 BCLModels.createChairBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE);
                             })
                             .override(NetherBlocks.BAR_STOOL_CINCINNASITE, block -> BCLModels.createBarStoolBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE))
                             .override(NetherBlocks.TABURET_CINCINNASITE, block -> BCLModels.createTaburetBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED))
                             .ignore(NetherBlocks.MAT_REED.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_ANCHOR_TREE.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_NETHER_SAKURA.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_MUSHROOM_FIR.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_STALAGNATE.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_RUBEUS.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_WILLOW.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.MAT_WART.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.WARPED_WOOD.getBlock(WoodSlots.LADDER))
                             .ignore(NetherBlocks.CRIMSON_WOOD.getBlock(WoodSlots.LADDER))

        );


    }

    public NetherModelProvider(ModCore modCore) {
        super(modCore);
    }

    private static JsonElement toArray(float... values) {
        JsonArray array = new JsonArray(values.length);
        for (float value : values) {
            array.add(value);
        }
        return array;
    }
}
