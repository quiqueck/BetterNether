package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherOreBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers tool-tier gating and silk touch for {@code cincinnasite_ore}, mirroring BetterEnd's
 * {@code OreDropGameTest}.
 * <p>
 * {@code Block.dropResources} does not enforce tool-tier gating at all (confirmed by reading its
 * bytecode while writing BetterEnd's version of this test) - the tier check ({@code
 * requiresCorrectToolForDrops()}/{@code isCorrectToolForDrops()}) lives one level up, in the real
 * player-mining code path. {@link #stoneToolIsNotAValidToolForCincinnasiteOre} asserts that gate
 * directly; {@link #ironToolDropsSomething} and {@link #silkTouchDropsTheOreBlockItself} use
 * {@code dropResources} for the drop-content checks, which it computes correctly.
 */
public class OreDropGameTest {
    private static final BlockPos ORE_POS = new BlockPos(1, 2, 1);

    @GameTest
    public void stoneToolIsNotAValidToolForCincinnasiteOre(GameTestHelper helper) {
        helper.setBlock(ORE_POS, NetherOreBlocks.CINCINNASITE_ORE);
        final BlockState state = helper.getBlockState(ORE_POS);
        final ItemStack stone = new ItemStack(Items.STONE_PICKAXE);

        final List<String> failures = new ArrayList<>();
        if (!state.requiresCorrectToolForDrops()) {
            failures.add("cincinnasite_ore does not require a correct tool for drops at all");
        }
        if (stone.isCorrectToolForDrops(state)) {
            failures.add("a stone pickaxe is considered a correct tool for cincinnasite_ore");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "cincinnasite_ore tool-tier gating regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void ironToolDropsSomething(GameTestHelper helper) {
        final List<ItemEntity> drops = breakAndCollectDrops(helper, new ItemStack(Items.IRON_PICKAXE));
        if (drops.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "an iron pickaxe (meets cincinnasite_ore's tool requirement) dropped nothing"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void silkTouchDropsTheOreBlockItself(GameTestHelper helper) {
        final ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        final Holder<Enchantment> silkTouch = helper.getLevel()
                                                     .registryAccess()
                                                     .lookupOrThrow(Registries.ENCHANTMENT)
                                                     .getOrThrow(Enchantments.SILK_TOUCH);
        pickaxe.enchant(silkTouch, 1);

        final List<ItemEntity> drops = breakAndCollectDrops(helper, pickaxe);
        final List<String> failures = new ArrayList<>();
        if (drops.isEmpty()) {
            failures.add("a silk-touch pickaxe dropped nothing");
        } else if (drops.stream().noneMatch(e -> e.getItem().is(NetherOreBlocks.CINCINNASITE_ORE.asItem()))) {
            failures.add("a silk-touch pickaxe did not drop the ore block itself, got "
                    + drops.stream().map(e -> e.getItem().toString()).toList());
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Silk touch ore-drop regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static List<ItemEntity> breakAndCollectDrops(GameTestHelper helper, ItemStack tool) {
        helper.setBlock(ORE_POS, NetherOreBlocks.CINCINNASITE_ORE);
        final BlockPos abs = helper.absolutePos(ORE_POS);
        final BlockState state = helper.getLevel().getBlockState(abs);
        final ServerPlayer player = MockPlayers.survival(helper, ORE_POS.above());

        Block.dropResources(state, helper.getLevel(), abs, null, player, tool);

        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0));
    }
}
