package org.betterx.betternether.tab;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import java.util.List;

public class CreativeTabs {
    public static final CreativeModeTab BN_TAB;
    public static final CreativeModeTab TAB_ITEMS;

    static {
        BN_TAB = new FabricItemGroup(BetterNether.makeID("blocks")) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(NetherBlocks.NETHER_GRASS);
            }

            @Override
            protected void generateDisplayItems(FeatureFlagSet featureFlagSet, Output output) {
                List<ItemStack> stacks = NetherBlocks.getModBlockItems()
                                                     .stream()
                                                     .map(ItemStack::new)
                                                     .toList();
                output.acceptAll(stacks);
            }
        };

        TAB_ITEMS = new FabricItemGroup(BetterNether.makeID("items")) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(NetherItems.BLACK_APPLE);
            }

            @Override
            protected void generateDisplayItems(FeatureFlagSet featureFlagSet, Output output) {
                List<ItemStack> stacks = NetherItems.getModItems()
                                                    .stream()
                                                    .map(ItemStack::new)
                                                    .toList();
                output.acceptAll(stacks);
            }
        };
    }
}
