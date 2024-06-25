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
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class NetherModelProvider extends WoverModelProvider {


    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.addFromRegistry(
                generator,
                BlockRegistry.forMod(BetterNether.C),
                true,
                List.of(
                        NetherBlocks.BASALT_BRICKS,
                        NetherBlocks.MAT_REED.getBlock(WoodSlots.STAIRS),
                        NetherBlocks.SOUL_SANDSTONE_STAIRS,
                        NetherBlocks.SOUL_SANDSTONE_SMOOTH_STAIRS,
                        NetherBlocks.SOUL_SANDSTONE_CUT_STAIRS,
                        NetherBlocks.BONE_PLATE,
                        NetherBlocks.BONE_BUTTON,
                        NetherBlocks.CINCINNASITE_PLATE,
                        NetherBlocks.CINCINNASITE_BUTTON,
                        NetherBlocks.CHAIR_CINCINNASITE,
                        NetherBlocks.BAR_STOOL_CINCINNASITE,
                        NetherBlocks.TABURET_CINCINNASITE,
                        NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.PRESSURE_PLATE),
                        NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.BUTTON)
                )
        );

        addCinFurniture(generator);


        //Special Blocks
        BNModels.provideSimpleMultiStateBlock(generator, NetherBlocks.BASALT_BRICKS, "", "_cracked");


        //special Stairs
        final Block reedPlanks = NetherBlocks.MAT_REED.getBlock(WoodSlots.PLANKS);

        final ResourceLocation SOUL_SANDSTONE_BOTTOM = BetterNether.C.mk("block/soul_sandstone_bottom");
        final ResourceLocation SOUL_SANDSTONE_TOP = BetterNether.C.mk("block/soul_sandstone_top");
        final ResourceLocation SOUL_SANDSTONE_SLABS = BetterNether.C.mk("block/soul_sandstone_slabs");
        final ResourceLocation SOUL_SANDSTONE_CUT_SLABS = BetterNether.C.mk("block/soul_sandstone_cut_slabs");
        final ResourceLocation NETHER_REED_PLANKS_TOP = BetterNether.C.mk("block/nether_reed_planks_top");
        final ResourceLocation NETHER_REED_PLANKS = TextureMapping.getBlockTexture(reedPlanks);

        generator.modelFor(NetherBlocks.SOUL_SANDSTONE, new TextureMapping()
                         .put(TextureSlot.TOP, SOUL_SANDSTONE_TOP)
                         .put(TextureSlot.BOTTOM, SOUL_SANDSTONE_BOTTOM)
                         .put(TextureSlot.SIDE, SOUL_SANDSTONE_SLABS))
                 .createStairs(NetherBlocks.SOUL_SANDSTONE_STAIRS);

        generator.modelFor(NetherBlocks.SOUL_SANDSTONE, new TextureMapping()
                         .put(TextureSlot.TOP, SOUL_SANDSTONE_TOP)
                         .put(TextureSlot.BOTTOM, SOUL_SANDSTONE_TOP)
                         .put(TextureSlot.SIDE, SOUL_SANDSTONE_CUT_SLABS))
                 .createStairs(NetherBlocks.SOUL_SANDSTONE_CUT_STAIRS);

        generator.modelFor(NetherBlocks.SOUL_SANDSTONE, new TextureMapping()
                         .put(TextureSlot.TOP, SOUL_SANDSTONE_TOP)
                         .put(TextureSlot.BOTTOM, SOUL_SANDSTONE_TOP)
                         .put(TextureSlot.SIDE, SOUL_SANDSTONE_TOP))
                 .createStairs(NetherBlocks.SOUL_SANDSTONE_SMOOTH_STAIRS);

        generator.modelFor(reedPlanks, new TextureMapping()
                         .put(TextureSlot.TOP, NETHER_REED_PLANKS)
                         .put(TextureSlot.BOTTOM, NETHER_REED_PLANKS_TOP)
                         .put(TextureSlot.SIDE, NETHER_REED_PLANKS))
                 .createStairs(NetherBlocks.MAT_REED.getBlock(WoodSlots.STAIRS));


        generator.modelFor(NetherBlocks.BONE_PLATE, new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/bone_block_plate")))
                 .createPressurePlate(NetherBlocks.BONE_PLATE);

        generator.modelFor(NetherBlocks.CINCINNASITE_PLATE, new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/cincinnasite_plate_up")))
                 .createPressurePlate(NetherBlocks.CINCINNASITE_PLATE);

        generator.modelFor(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.PRESSURE_PLATE), new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/nether_mushroom_plate")))
                 .createPressurePlate(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.PRESSURE_PLATE));

        generator.modelFor(NetherBlocks.BONE_BUTTON, new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/bone_button")))
                 .createButton(NetherBlocks.BONE_BUTTON);

        generator.modelFor(NetherBlocks.CINCINNASITE_BUTTON, new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/cincinnasite_button")))
                 .createButton(NetherBlocks.CINCINNASITE_BUTTON);

        generator.modelFor(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.BUTTON), new TextureMapping()
                         .put(TextureSlot.TEXTURE, BetterNether.C.mk("block/nether_mushroom_button")))
                 .createButton(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(WoodSlots.BUTTON));
    }

    private static void addCinFurniture(WoverBlockModelGenerators generator) {
        //this was a custom Item with a view transform, it is easier to recreate the json instead of finding
        //an appropriate API call that fits this special case...
        generator.acceptModelOutput(ModelLocationUtils.getModelLocation(NetherBlocks.CHAIR_CINCINNASITE.asItem()), () -> {
            JsonObject root = new JsonObject();
            JsonObject display = new JsonObject();
            JsonObject gui = new JsonObject();
            JsonObject fixed = new JsonObject();

            root.addProperty("parent", ModelLocationUtils.getModelLocation(NetherBlocks.CHAIR_CINCINNASITE).toString());
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
        addFurniture(generator, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE, NetherBlocks.BAR_STOOL_CINCINNASITE, NetherBlocks.CHAIR_CINCINNASITE, NetherBlocks.TABURET_CINCINNASITE);
    }

    public NetherModelProvider(ModCore modCore) {
        super(modCore);
    }

    private static void addFurniture(
            WoverBlockModelGenerators generator,
            Block base,
            Block cloth,
            Block barStool,
            Block chair,
            Block taburet
    ) {
        BCLModels.createBarStoolBlockModel(generator, barStool, base, cloth);
        BCLModels.createChairBlockModel(generator, chair, base, cloth);
        BCLModels.createTaburetBlockModel(generator, taburet, base);
    }

    private static JsonElement toArray(float... values) {
        JsonArray array = new JsonArray(values.length);
        for (float value : values) {
            array.add(value);
        }
        return array;
    }
}
