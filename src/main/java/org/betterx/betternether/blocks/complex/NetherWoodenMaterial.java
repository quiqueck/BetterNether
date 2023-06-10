package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.BCLWoodTypeWrapper;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class NetherWoodenMaterial<T extends NetherWoodenMaterial<T>> extends WoodenComplexMaterial {
    public NetherWoodenMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(BetterNether.MOD_ID, name, "nether", woodColor, planksColor);
    }

    public T init() {
        return (T) super.init(
                NetherBlocks.getBlockRegistry(),
                NetherItems.getItemRegistry()
        );
    }

    @Override
    protected BCLWoodTypeWrapper.Builder createWoodTypeBuilder() {
        return super.createWoodTypeBuilder().setFlammable(false);
    }

    @Override
    protected FabricBlockSettings getBlockSettings() {
        return FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)
                                  .mapColor(planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
                NetherSlots.STRIPPED_LOG,
                NetherSlots.STRIPPED_BARK,
                NetherSlots.LOG,
                NetherSlots.BARK,
                WoodSlots.PLANKS,
                WoodSlots.STAIRS,
                WoodSlots.SLAB,
                WoodSlots.FENCE,
                WoodSlots.GATE,
                WoodSlots.BUTTON,
                WoodSlots.PRESSURE_PLATE,
                WoodSlots.TRAPDOOR,
                WoodSlots.DOOR,
                WoodSlots.LADDER,
                WoodSlots.SIGN,
                WoodSlots.HANGING_SIGN,
                WoodSlots.CHEST,
                WoodSlots.BARREL,
                WoodSlots.CRAFTING_TABLE,
                WoodSlots.BOOKSHELF,
                WoodSlots.COMPOSTER,
                WoodSlots.BOAT,
                WoodSlots.CHEST_BOAT,
                WoodSlots.TABURET,
                WoodSlots.CHAIR,
                WoodSlots.BAR_STOOL
        );
    }

    @Override
    protected void initFlammable(FlammableBlockRegistry registry) {
        //Nothing burns in the nether
    }

    public Block getPlanks() {
        return getBlock(WoodSlots.PLANKS);
    }

    public Block getSlab() {
        return getBlock(WoodSlots.SLAB);
    }

    public Block getLog() {
        return getBlock(WoodSlots.LOG);
    }

    public Block getBark() {
        return getBlock(WoodSlots.BARK);
    }

    public Block getStrippedLog() {
        return getBlock(WoodSlots.STRIPPED_LOG);
    }

    public Block getStrippedBark() {
        return getBlock(WoodSlots.STRIPPED_BARK);
    }
}
