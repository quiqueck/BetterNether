package org.betterx.betternether.tab;

import org.betterx.bclib.creativetab.BCLCreativeTab;
import org.betterx.bclib.items.complex.EquipmentSet;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.WillowMaterial;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.item.api.tabs.CreativeTabManager;

public class CreativeTabs {
    public static void register() {
        CreativeTabManager.start(BetterNether.C)
                          .createTab("nature")
                          .setPredicate(item -> BCLCreativeTab.NATURE.contains(item)
                                  || item == NetherItems.AGAVE_LEAF
                                  || item == NetherItems.BLACK_APPLE
                                  || item == NetherBlocks.MAGMA_FLOWER.asItem()
                                  || item == NetherBlocks.MAT_RUBEUS.getBlockItem(NetherSlots.CONE)
                                  || item == NetherBlocks.MAT_WILLOW.getBlockItem(WillowMaterial.BLOCK_TORCH))
                          .setIcon(NetherItems.BLACK_APPLE)
                          .build()
                          .createBlockTab(NetherBlocks.JUNGLE_GRASS)
                          .build()
                          .createItemsTab(NetherItems.FLAMING_RUBY_SET.getSlot(EquipmentSet.PICKAXE_SLOT))
                          .build()
                          .processRegistries()
                          .registerAll();
    }
}
