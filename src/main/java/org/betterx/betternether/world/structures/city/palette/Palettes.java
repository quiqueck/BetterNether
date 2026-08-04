package org.betterx.betternether.world.structures.city.palette;

import org.betterx.betternether.registry.block.NetherDecorBlocks;
import org.betterx.betternether.registry.block.NetherGlassBlocks;
import org.betterx.betternether.registry.block.NetherLightBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import de.ambertation.wover.sets.api.blocks.SlotType;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;

public class Palettes {
    private static final HashMap<String, CityPalette> REGISTRY = new HashMap<String, CityPalette>();
    private static final ArrayList<CityPalette> PALETTES = new ArrayList<CityPalette>();

    public static final CityPalette EMPTY = register(new CityPalette("empty"));

    public static final CityPalette RED = register(new CityPalette("red")
            .addRoofBlocks(NetherWoodBlocks.MAT_WART.getBlock(NetherSlots.ROOF))
            .addRoofSlabs(NetherWoodBlocks.MAT_WART.getBlock(NetherSlots.ROOF_SLAB))
            .addRoofStairs(NetherWoodBlocks.MAT_WART.getBlock(NetherSlots.ROOF_STAIRS))
            .addPlanksBlocks(NetherWoodBlocks.MAT_WART.getPlanks())
            .addPlanksSlabs(NetherWoodBlocks.MAT_WART.getSlab())
            .addPlanksStairs(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.STAIRS))
            .addFences(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.FENCE))
            .addGates(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.GATE))
            .addWalls(NetherStoneBlocks.NETHER_BRICK_WALL)
            .addLogs(
                    NetherWoodBlocks.MAT_WART.getLog(),
                    NetherWoodBlocks.MAT_WILLOW.getBlock(SlotType.LOG),
                    NetherWoodBlocks.MAT_WART.getStrippedLog()
            )
            .addBark(
                    NetherWoodBlocks.MAT_WART.getBark(),
                    NetherWoodBlocks.MAT_WILLOW.getBlock(SlotType.BARK),
                    NetherWoodBlocks.MAT_WART.getStrippedBark()
            )
            .addStoneBlocks(
                    Blocks.NETHER_BRICKS,
                    Blocks.NETHER_WART_BLOCK,
                    NetherStoneBlocks.NETHER_BRICK_TILE_LARGE,
                    NetherStoneBlocks.NETHER_BRICK_TILE_SMALL
            )
            .addStoneSlabs(
                    Blocks.NETHER_BRICK_SLAB,
                    NetherStoneBlocks.NETHER_BRICK_TILE_SLAB
            )
            .addStoneStairs(
                    Blocks.NETHER_BRICK_STAIRS,
                    NetherStoneBlocks.NETHER_BRICK_TILE_STAIRS
            )
            .addGlowingBlocks(
                    Blocks.GLOWSTONE,
                    NetherLightBlocks.CINCINNASITE_LANTERN
            )
            .addCeilingLights(
                    Blocks.LANTERN,
                    NetherLightBlocks.CINCINNASITE_LANTERN_SMALL
            )
            .addWallLights(
                    Blocks.WALL_TORCH,
                    NetherLightBlocks.CINCINNASITE_LANTERN_SMALL
            )
            .addFloorLights(
                    Blocks.TORCH,
                    NetherLightBlocks.CINCINNASITE_LANTERN_SMALL
            )
            .addDoors(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.DOOR))
            .addTrapdoors(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.TRAPDOOR))
            .addGlassBlocks(
                    NetherGlassBlocks.QUARTZ_GLASS_FRAMED_COLORED.red,
                    NetherGlassBlocks.QUARTZ_GLASS_COLORED.red,
                    NetherDecorBlocks.CINCINNASITE_FRAME
            )
            .addGlassPanes(
                    NetherGlassBlocks.QUARTZ_GLASS_FRAMED_PANE_COLORED.red,
                    NetherGlassBlocks.QUARTZ_GLASS_PANE_COLORED.red,
                    NetherMetalBlocks.CINCINNASITE_BARS
            )
            .addWoodPlates(NetherWoodBlocks.MAT_WART.getBlock(
                    SlotType.PRESSURE_PLATE))
            .addPotsPanes(NetherDecorBlocks.BRICK_POT));

    private static CityPalette register(CityPalette palette) {
        REGISTRY.put(palette.getName(), palette);
        PALETTES.add(palette);
        return palette;
    }

    public static CityPalette getPalette(String name) {
        CityPalette palette = REGISTRY.get(name);
        return palette == null ? EMPTY : palette;
    }

    public static CityPalette getRandom(RandomSource random) {
        return random.nextBoolean() ? EMPTY : PALETTES.get(random.nextInt(PALETTES.size()));
    }
}
