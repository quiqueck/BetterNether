package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Survival-mining checks for BetterNether's container blocks and the cincinnasite furniture:
 * <ul>
 *   <li>#234: the cincinnasite forge drops itself and its contents when mined with any pickaxe.</li>
 *   <li>#232: every BetterNether chest gets a real chest block entity (no block entity means nothing to
 *       render - an invisible chest) and drops itself plus its contents.</li>
 *   <li>The cincinnasite taburet, chair and bar stool are metal and copy the cincinnasite slab's
 *       "requires correct tool" property, so they must be pickaxe-mineable like the slab; they used to be
 *       tagged for the axe, so a pickaxe destroyed them without a drop.</li>
 * </ul>
 */
public class ContainerAndCincinnasiteDropGameTest {
    private static final BlockPos POS = new BlockPos(3, 2, 3);

    @GameTest
    public void cincinnasiteForgeDropsItselfAndContentsWithAPickaxe(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final Block forge = NetherFunctionalBlocks.CINCINNASITE_FORGE;
        final List<String> failures = new ArrayList<>();
        for (Item pickaxe : List.of(Items.WOODEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE)) {
            final Map<Item, Integer> drops = mine(helper, player, forge, new ItemStack(pickaxe), true);
            final Map<Item, Integer> expected = Map.of(
                    forge.asItem(), 1,
                    Items.RAW_IRON, 3,
                    Items.COAL, 5
            );
            if (!drops.equals(expected)) {
                failures.add("cincinnasite_forge with " + BuiltInRegistries.ITEM.getKey(pickaxe)
                        + ": expected " + expected + ", got " + drops);
            }
        }
        player.discard();
        finish(helper, failures);
    }

    @GameTest
    public void everyChestHasAChestBlockEntityAndDropsWithContents(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final List<String> failures = new ArrayList<>();
        int chests = 0;
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            final String id = entry.getKey().identifier().toString();
            if (!id.startsWith("betternether:") || !(entry.getValue() instanceof ChestBlock chest)) continue;
            chests++;
            if (!BlockEntityTypes.CHEST.isValid(chest.defaultBlockState())) {
                failures.add(id + " is not a valid block for the chest block entity type");
            }
            helper.setBlock(POS, chest.defaultBlockState());
            if (!(helper.getLevel().getBlockEntity(helper.absolutePos(POS)) instanceof ChestBlockEntity)) {
                failures.add(id + " has no chest block entity after placement (would render invisible)");
                helper.setBlock(POS, Blocks.AIR);
                continue;
            }
            helper.setBlock(POS, Blocks.AIR);
            final Map<Item, Integer> drops = mine(helper, player, chest, new ItemStack(Items.IRON_AXE), true);
            final Map<Item, Integer> expected = Map.of(chest.asItem(), 1, Items.RAW_IRON, 3, Items.COAL, 5);
            if (!drops.equals(expected)) {
                failures.add(id + " with an axe: expected " + expected + ", got " + drops);
            }
        }
        if (chests == 0) failures.add("found no betternether chests");
        player.discard();
        finish(helper, failures);
    }

    @GameTest
    public void cincinnasiteFurnitureIsMinedWithAPickaxe(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final List<String> failures = new ArrayList<>();
        for (Block block : List.of(
                NetherFurnitureBlocks.TABURET_CINCINNASITE,
                NetherFurnitureBlocks.CHAIR_CINCINNASITE,
                NetherFurnitureBlocks.BAR_STOOL_CINCINNASITE
        )) {
            final String id = BuiltInRegistries.BLOCK.getKey(block).toString();
            final Map<Item, Integer> drops = mine(helper, player, block, new ItemStack(Items.IRON_PICKAXE), false);
            if (!drops.equals(Map.of(block.asItem(), 1))) {
                failures.add(id + " with an iron pickaxe: expected 1x itself, got " + drops);
            }
            // Same rule as the cincinnasite slab it copies: metal needs a pickaxe, an axe gets nothing.
            final Map<Item, Integer> axeDrops = mine(helper, player, block, new ItemStack(Items.IRON_AXE), false);
            if (!axeDrops.isEmpty()) {
                failures.add(id + " with an iron axe: expected nothing (like cincinnasite_slab), got " + axeDrops);
            }
        }
        player.discard();
        finish(helper, failures);
    }

    /** Places {@code block} (optionally filled with raw iron and coal), mines it and collects every drop. */
    private static Map<Item, Integer> mine(
            GameTestHelper helper,
            ServerPlayer player,
            Block block,
            ItemStack tool,
            boolean fill
    ) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(POS);
        final AABB area = new AABB(abs).inflate(3.0);
        level.getEntitiesOfClass(ItemEntity.class, area).forEach(ItemEntity::discard);

        helper.setBlock(POS, block.defaultBlockState());
        if (fill) {
            if (!(level.getBlockEntity(abs) instanceof Container container)) {
                throw helper.assertionException(Component.literal(
                        BuiltInRegistries.BLOCK.getKey(block) + " has no container block entity"));
            }
            container.setItem(0, new ItemStack(Items.RAW_IRON, 3));
            container.setItem(1, new ItemStack(Items.COAL, 5));
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.gameMode.destroyBlock(abs);

        final Map<Item, Integer> drops = new LinkedHashMap<>();
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, area)) {
            drops.merge(e.getItem().getItem(), e.getItem().getCount(), Integer::sum);
            e.discard();
        }
        helper.setBlock(POS, Blocks.AIR);
        return drops;
    }

    private static void finish(GameTestHelper helper, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "drop regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
