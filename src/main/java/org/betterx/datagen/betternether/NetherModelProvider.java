package org.betterx.datagen.betternether;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class NetherModelProvider extends WoverModelProvider {
    public NetherModelProvider(ModCore modCore) {
        super(modCore);
    }

    protected void addFurniture(WoverBlockModelGenerators generator, ComplexMaterial mat, Block cloth) {
        addFurniture(
                generator,
                mat.getBlock(WoodSlots.PLANKS),
                cloth,
                mat.getBlock(WoodSlots.BAR_STOOL),
                mat.getBlock(WoodSlots.CHAIR),
                mat.getBlock(WoodSlots.TABURET)
        );
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
                        NetherBlocks.SOUL_SANDSTONE_CUT_STAIRS
                )
        );

        addFurniture(generator, NetherBlocks.ACACIA_WOOD, Blocks.BLACK_WOOL);
        addFurniture(generator, NetherBlocks.MAT_ANCHOR_TREE, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.BAMBOO_WOOD, Blocks.BROWN_WOOL);
        addFurniture(generator, NetherBlocks.BIRCH_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.CHERRY_WOOD, Blocks.WHITE_WOOL);
        addFurniture(generator, NetherBlocks.CRIMSON_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.DARK_OAK_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.JUNGLE_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.MANGROVE_WOOD, Blocks.BLACK_WOOL);
        addFurniture(generator, NetherBlocks.MAT_MUSHROOM_FIR, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.MAT_NETHER_MUSHROOM, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.MAT_REED, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.MAT_NETHER_SAKURA, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.OAK_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.MAT_RUBEUS, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.SPRUCE_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.MAT_STALAGNATE, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.WARPED_WOOD, Blocks.RED_WOOL);
        addFurniture(generator, NetherBlocks.MAT_WART, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addFurniture(generator, NetherBlocks.MAT_WILLOW, NetherBlocks.NETHER_BRICK_TILE_LARGE);
        addCinFurniture(generator);


        //Special Blocks
        BNModels.provideSimpleMultiStateBlock(generator, NetherBlocks.BASALT_BRICKS, "", "_cracked");


        //Nether Reed special handling

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

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
