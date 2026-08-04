package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.function.Supplier;

public class BNBlockProperties {
    public static final EnumProperty<CincinnasitPillarShape> PILLAR_SHAPE = EnumProperty.create(
            "shape",
            CincinnasitPillarShape.class
    );
    public static final EnumProperty<BrownMushroomShape> BROWN_MUSHROOM_SHAPE = EnumProperty.create(
            "shape",
            BrownMushroomShape.class
    );
    public static final EnumProperty<WillowBranchShape> WILLOW_SHAPE = EnumProperty.create(
            "shape",
            WillowBranchShape.class
    );
    public static final EnumProperty<JellyShape> JELLY_MUSHROOM_VISUAL = EnumProperty.create(
            "visual",
            JellyShape.class
    );
    public static final EnumProperty<EnumLucisShape> LUCIS_SHAPE = EnumProperty.create("shape", EnumLucisShape.class);
    public static final EnumProperty<PottedPlantShape> PLANT = EnumProperty.create("plant", PottedPlantShape.class);
    public static final EnumProperty<FoodShape> FOOD = EnumProperty.create("food", FoodShape.class);

    public static final BooleanProperty DESTRUCTED = BooleanProperty.create("destructed");
    public static final BooleanProperty FLOOR = BooleanProperty.create("floor");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty FIRE = BooleanProperty.create("fire");

    public enum CincinnasitPillarShape implements StringRepresentable {
        SMALL("small"),
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        final String name;

        CincinnasitPillarShape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum BrownMushroomShape implements StringRepresentable {
        TOP("top"),
        SIDE_N("side_n"),
        SIDE_S("side_s"),
        SIDE_E("side_e"),
        SIDE_W("side_w"),
        CORNER_N("corner_n"),
        CORNER_S("corner_s"),
        CORNER_E("corner_e"),
        CORNER_W("corner_w"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        final String name;

        BrownMushroomShape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum EnumLucisShape implements StringRepresentable {
        CORNER("corner"),
        SIDE("side"),
        CENTER("center");

        final String name;

        EnumLucisShape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum JellyShape implements StringRepresentable {
        NORMAL("normal"), SEPIA("sepia"), POOR("poor");

        final String name;

        JellyShape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum FoodShape implements StringRepresentable {
        NONE("none"), WART("wart"), MUSHROOM("mushroom"), APPLE("apple");

        private final String name;
        private Item item;

        FoodShape(String name) {
            this.name = name;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public Item getItem() {
            return item;
        }
    }

    public enum PottedPlantShape implements StringRepresentable {
        AGAVE("agave", () -> NetherPlantBlocks.AGAVE),
        BARREL_CACTUS("barrel_cactus", () -> NetherPlantBlocks.BARREL_CACTUS),
        BLACK_APPLE("black_apple", () -> NetherCropBlocks.BLACK_APPLE_SEED),
        BLACK_BUSH("black_bush", () -> NetherPlantBlocks.BLACK_BUSH),
        EGG_PLANT("egg_plant", () -> NetherPlantBlocks.EGG_PLANT),
        INK_BUSH("ink_bush", () -> NetherPlantBlocks.INK_BUSH_SEED),
        REEDS("reeds", () -> NetherWoodBlocks.MAT_REED.getStem()),
        NETHER_CACTUS("nether_cactus", () -> NetherPlantBlocks.NETHER_CACTUS),
        NETHER_GRASS("nether_grass", () -> NetherPlantBlocks.NETHER_GRASS),
        ORANGE_MUSHROOM("orange_mushroom", () -> NetherMushroomBlocks.ORANGE_MUSHROOM),
        RED_MOLD("red_mold", () -> NetherMushroomBlocks.RED_MOLD),
        GRAY_MOLD("gray_mold", () -> NetherMushroomBlocks.GRAY_MOLD),
        MAGMA_FLOWER("magma_flower", () -> NetherPlantBlocks.MAGMA_FLOWER),
        NETHER_WART("nether_wart", () -> NetherWoodBlocks.MAT_WART.getSeed()),
        WILLOW("willow", () -> NetherWoodBlocks.MAT_WILLOW.getSapling()),
        SMOKER("smoker", () -> NetherMushroomBlocks.SMOKER),
        WART("wart", () -> Blocks.NETHER_WART),
        JUNGLE_PLANT("jungle_plant", () -> NetherPlantBlocks.JUNGLE_PLANT),
        JELLYFISH_MUSHROOM("jellyfish_mushroom", () -> NetherSaplingBlocks.JELLYFISH_MUSHROOM_SAPLING),
        SWAMP_GRASS("swamp_grass", () -> NetherPlantBlocks.SWAMP_GRASS),
        SOUL_GRASS("soul_grass", () -> NetherPlantBlocks.SOUL_GRASS),
        BONE_GRASS("bone_grass", () -> NetherPlantBlocks.BONE_GRASS),
        BONE_MUSHROOM("bone_mushroom", () -> NetherMushroomBlocks.BONE_MUSHROOM);

        private final Supplier<Block> block;
        private final String name;

        PottedPlantShape(String name, Supplier<Block> block) {
            this.name = name;
            this.block = block;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public Item getItem() {
            return block.get().asItem();
        }

        public Block getBlock() {
            return block.get();
        }
    }

    public enum WillowBranchShape implements StringRepresentable {
        END("end"),
        MIDDLE("middle");

        final String name;

        WillowBranchShape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
