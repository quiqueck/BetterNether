package org.betterx.betternether.tab;

import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherFoodItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.tabs.api.CreativeTabs;

public class BECreativeTabs {
    public static void register() {
        CreativeTabs
                .start(BetterNether.C)
                .createTab("nature")
                .setPredicate(item -> PlantLikeBlockTrait.TAB_PREDICATE.contains(item)
                        || item == NetherResourceItems.AGAVE_LEAF
                        || item == NetherFoodItems.BLACK_APPLE
                        || item == NetherPlantBlocks.MAGMA_FLOWER.asItem()
                        || item == NetherWoodBlocks.MAT_RUBEUS.getItem(NetherSlots.CONE)
                        || item == NetherWoodBlocks.MAT_WILLOW.getItem(NetherSlots.TORCH))
                .setIcon(NetherFoodItems.BLACK_APPLE)
                .buildAndAdd()
                .createBlockOnlyTab(NetherTerrainBlocks.JUNGLE_GRASS)
                .buildAndAdd()
                .createItemOnlyTab(NetherEquipmentItems.FLAMING_RUBY_SET.get(ToolSlot.PICKAXE_SLOT))
                .buildAndAdd()
                .processRegistries()
                .registerAllTabs();
    }
}
