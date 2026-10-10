package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Issue #216: an enchanting table surrounded only by BetterNether bookshelves must offer level-30
 * enchantments, exactly like one surrounded by vanilla bookshelves.
 * <p>
 * Every block registered as {@code betternether:*_bookshelf} (chiseled ones excluded - vanilla's chiseled
 * bookshelf does not power a table either) is checked three ways:
 * <ul>
 *     <li>it is in {@code #minecraft:enchantment_power_provider}, which is what vanilla's
 *     {@code EnchantingTableBlock.isValidBookShelf} - and every other mod that counts bookshelves - reads;</li>
 *     <li>a full ring of it around a real table makes a real {@code EnchantmentMenu} offer a level-30 enchant
 *     in its third slot;</li>
 *     <li>that still holds when the gap between table and shelves is filled with a replaceable block
 *     ({@code minecraft:light}), which vanilla lets through via {@code #minecraft:enchantment_power_transmitter}.
 *     Only a block in the vanilla provider tag gets that rule; WorldWeaver's own magic-source fallback
 *     demanded literal air.</li>
 * </ul>
 * The vanilla bookshelf runs through the identical harness as a control.
 */
public class EnchantingBookshelfGameTest {
    private static final BlockPos TABLE = new BlockPos(2, 1, 2);
    private static final BlockPos PLAYER = new BlockPos(6, 1, 6);

    @GameTest
    public void everyBookshelfIsAnEnchantmentPowerProvider(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (Block shelf : bookshelves()) {
            if (!shelf.defaultBlockState().is(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                failures.add(BuiltInRegistries.BLOCK.getKey(shelf) + " is not in #minecraft:enchantment_power_provider");
            }
        }
        failIfAny(helper, "Bookshelf enchanting tag regression (#216)", failures);
        helper.succeed();
    }

    @GameTest
    public void bookshelvesAloneReachLevel30(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final List<String> failures = new ArrayList<>();

        check(helper, player, Blocks.BOOKSHELF, Blocks.AIR, failures);
        for (Block shelf : bookshelves()) {
            check(helper, player, shelf, Blocks.AIR, failures);
        }

        failIfAny(helper, "Bookshelf enchanting power regression (#216)", failures);
        helper.succeed();
    }

    @GameTest
    public void bookshelvesPowerThroughReplaceableBlocks(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final List<String> failures = new ArrayList<>();

        check(helper, player, Blocks.BOOKSHELF, Blocks.LIGHT, failures);
        for (Block shelf : bookshelves()) {
            check(helper, player, shelf, Blocks.LIGHT, failures);
        }

        failIfAny(helper, "Bookshelf enchanting transmitter regression (#216)", failures);
        helper.succeed();
    }

    private static List<Block> bookshelves() {
        final List<Block> result = new ArrayList<>();
        for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
            if (BetterNether.C.namespace.equals(id.getNamespace())
                    && id.getPath().endsWith("_bookshelf")
                    && !id.getPath().contains("chiseled")) {
                result.add(BuiltInRegistries.BLOCK.getValue(id));
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("no BetterNether bookshelves registered - the sweep would be vacuous");
        }
        return result;
    }

    /**
     * Builds the full 32-block ring of {@code shelf} around a fresh table, fills the gap ring with {@code gap},
     * and asks a real {@code EnchantmentMenu} what it offers for a diamond pickaxe.
     */
    private static void check(GameTestHelper helper, ServerPlayer player, Block shelf, Block gap, List<String> failures) {
        helper.setBlock(TABLE, Blocks.ENCHANTING_TABLE);
        int providers = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE.offset(offset), shelf);
            helper.setBlock(TABLE.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2), gap);
        }
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantingTableBlock.isValidBookShelf(helper.getLevel(), helper.absolutePos(TABLE), offset)) {
                providers++;
            }
        }

        final EnchantmentMenu menu = new EnchantmentMenu(
                0,
                player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(TABLE))
        );
        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_PICKAXE));
        final int topCost = menu.costs[2];
        menu.getSlot(0).set(ItemStack.EMPTY);

        if (topCost != 30) {
            failures.add(BuiltInRegistries.BLOCK.getKey(shelf) + " behind " + BuiltInRegistries.BLOCK.getKey(gap)
                    + ": top enchant costs " + topCost + " (expected 30), " + providers + "/"
                    + EnchantingTableBlock.BOOKSHELF_OFFSETS.size() + " ring positions count as bookshelves");
        }

        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE.offset(offset), Blocks.AIR);
            helper.setBlock(TABLE.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2), Blocks.AIR);
        }
    }

    private static void failIfAny(GameTestHelper helper, String title, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(title + ":\n - " + String.join("\n - ", failures)));
        }
    }
}
