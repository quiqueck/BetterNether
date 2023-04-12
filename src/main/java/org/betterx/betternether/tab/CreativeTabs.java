package org.betterx.betternether.tab;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SaplingBlock;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import java.util.List;

public class CreativeTabs {
    public static final CreativeModeTab BN_TAB;
    public static final CreativeModeTab TAB_ITEMS;

    static {
        BN_TAB = FabricItemGroup
                .builder(BetterNether.makeID("blocks"))
                .icon(() -> new ItemStack(NetherBlocks.NETHER_GRASS))
                .displayItems((featureFlagSet, output) -> {
                    List<ItemStack> stacks = NetherBlocks
                            .getModBlockItems()
                            .stream()
                            .filter(itm -> !(itm instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock))
                            .map(ItemStack::new)
                            .toList();
                    output.acceptAll(stacks);
                }).build();

        TAB_ITEMS = FabricItemGroup
                .builder(BetterNether.makeID("items"))
                .icon(() -> new ItemStack(NetherItems.BLACK_APPLE))
                .displayItems((featureFlagSet, output) -> {
                    List<ItemStack> saplings = NetherBlocks
                            .getModBlockItems()
                            .stream()
                            .filter(itm -> (itm instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock))
                            .map(ItemStack::new)
                            .toList();
                    List<ItemStack> stacks = NetherItems
                            .getModItems()
                            .stream()
                            .map(ItemStack::new)
                            .toList();

                    output.acceptAll(saplings);
                    output.acceptAll(stacks);
                }).build();
    }
}
