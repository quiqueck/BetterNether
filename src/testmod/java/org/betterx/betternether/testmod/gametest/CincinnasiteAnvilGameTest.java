package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * {@code cincinnasite_anvil} is a near-passthrough of vanilla {@code AnvilBlock} - the actual custom
 * surface area is {@code NetherLoot.cincinnasiteAnvil()}'s loot trait, which only drops the block when
 * mined with a pickaxe (an ingredient-tag condition embedded in the loot table itself, not the separate
 * block-level {@code requiresCorrectToolForDrops} flag {@code Block.dropResources} does not enforce -
 * so, unlike ore tool-tier gating, {@code dropResources} alone is sufficient to test this).
 */
public class CincinnasiteAnvilGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void onlyAPickaxeDropsTheAnvil(GameTestHelper helper) {
        if (!dropsSomething(helper, new ItemStack(Items.IRON_PICKAXE))) {
            throw helper.assertionException(Component.literal("a pickaxe dropped nothing from cincinnasite_anvil"));
        }
        if (dropsSomething(helper, new ItemStack(Items.IRON_AXE))) {
            throw helper.assertionException(Component.literal("an axe still dropped something from cincinnasite_anvil"));
        }
        helper.succeed();
    }

    private static boolean dropsSomething(GameTestHelper helper, ItemStack tool) {
        helper.setBlock(POS, NetherFunctionalBlocks.CINCINNASITE_ANVIL);
        final BlockPos abs = helper.absolutePos(POS);
        final ServerPlayer player = MockPlayers.survival(helper, POS.above());

        Block.dropResources(helper.getLevel().getBlockState(abs), helper.getLevel(), abs, null, player, tool);

        final List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0));
        final boolean dropped = !drops.isEmpty();
        drops.forEach(ItemEntity::discard);
        player.discard();
        return dropped;
    }
}
