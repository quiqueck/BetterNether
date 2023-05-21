package org.betterx.betternether.tab;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SaplingBlock;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import java.util.List;

public class CreativeTabs {
    public static final CreativeModeTab TAB_BLOCKS;
    public static final CreativeModeTab TAB_ITEMS;


    public static final ResourceKey<CreativeModeTab> TAB_ITEMS_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            BetterNether.makeID("item_tab")
    );
    public static final ResourceKey<CreativeModeTab> TAB_BLOCKS_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            BetterNether.makeID("block_tab")
    );

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TAB_ITEMS_KEY,
                TAB_ITEMS
        );

        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TAB_BLOCKS_KEY,
                TAB_BLOCKS
        );
    }

    static {
        TAB_BLOCKS = FabricItemGroup
                .builder()
                .icon(() -> new ItemStack(NetherBlocks.NETHER_GRASS))
                .title(Component.translatable("itemGroup.betternether.blocks"))
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
                .builder()
                .icon(() -> new ItemStack(NetherItems.BLACK_APPLE))
                .title(Component.translatable("itemGroup.betternether.items"))
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
