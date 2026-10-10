package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherWoodBlocks;

import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Issue #311: a copper golem must deliver items into our wooden chests, not only into vanilla ones.
 * <p>
 * Vanilla hardcodes the golem's destination as {@code chest || trapped_chest}, which {@code CopperGolemAiMixin}
 * widens to {@code c:chests/wooden}. Both tests are driven end to end by a real golem with AI: it spawns
 * already holding an item, which puts it straight into "find a destination" mode (no copper chest needed),
 * and the only chest in range is the one under test. The vanilla chest is the control proving the harness
 * itself lets a golem deliver at all.
 */
public class CopperGolemChestGameTest {
    private static final BlockPos CHEST = new BlockPos(1, 1, 1);
    private static final BlockPos GOLEM = new BlockPos(3, 1, 1);
    private static final int COUNT = 5;

    @GameTest(maxTicks = 600)
    public void aCopperGolemDeliversIntoABetterNetherChest(GameTestHelper helper) {
        deliversInto(helper, NetherWoodBlocks.MAT_GLOOMWOOD.getBlock(SlotType.CHEST));
    }

    @GameTest(maxTicks = 600)
    public void aCopperGolemDeliversIntoAVanillaChest(GameTestHelper helper) {
        deliversInto(helper, Blocks.CHEST);
    }

    private static void deliversInto(GameTestHelper helper, Block chestBlock) {
        helper.setBlock(CHEST, chestBlock);
        if (!(helper.getLevel().getBlockEntity(helper.absolutePos(CHEST)) instanceof ChestBlockEntity chest)) {
            throw helper.assertionException(Component.literal(
                    BuiltInRegistries.BLOCK.getKey(chestBlock) + " did not create a ChestBlockEntity"
            ));
        }

        final CopperGolem golem = helper.spawn(EntityTypes.COPPER_GOLEM, GOLEM);
        golem.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE, COUNT));

        helper.succeedWhen(() -> {
            final ItemStack inside = chest.getItem(0);
            if (!inside.is(Items.COBBLESTONE) || inside.getCount() != COUNT) {
                throw helper.assertionException(Component.literal(
                        "after " + helper.getTick() + " ticks the copper golem has not put its " + COUNT
                                + " cobblestone into " + BuiltInRegistries.BLOCK.getKey(chestBlock)
                                + " (golem holds " + golem.getMainHandItem() + ", chest slot 0 has " + inside + ")"
                ));
            }
        });
    }
}
