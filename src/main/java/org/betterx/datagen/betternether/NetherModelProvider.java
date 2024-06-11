package org.betterx.datagen.betternether;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.furniture.block.AbstractChair;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
        AbstractChair.createBarStoolBlockModel(generator, barStool, base, cloth);
        AbstractChair.createChairBlockModel(generator, chair, base, cloth);
        AbstractChair.createTaburetBlockModel(generator, taburet, base);
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.addFromRegistry(generator, BlockRegistry.forMod(BetterNether.C), true);

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


        addFurniture(generator, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE, NetherBlocks.BAR_STOOL_CINCINNASITE, NetherBlocks.CHAIR_CINCINNASITE, NetherBlocks.TABURET_CINCINNASITE);

    }

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
