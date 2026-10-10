package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockEyeSeed;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * #153, #159, #179: the eye plant (an {@code eye_vine} stalk hanging from the ceiling with an
 * {@code eyeball}/{@code eyeball_small} on its end) must yield eye seeds and slime balls however it comes
 * down - not only when the eyeball itself is mined, which {@link LootAuditGameTest} already covers.
 * <p>
 * The other ways the reports describe are the ones checked here: cutting the stalk (the eyeball loses its
 * support and pops through {@code updateShape} returning air) and a piston ({@code PushReaction.DESTROY}).
 * The stalk itself drops nothing on purpose - it has no item, and never had a loot table on any branch
 * (it was {@code dropsNothing()}/{@code noLootTable()} back to 1.17); the seeds come from the eyeball.
 * <p>
 * The whole plant must come down either way: the stalk also breaks once nothing hangs below it, as it
 * did before eye_vine became a {@code BaseVineBlock} (the reports' "the vine disappears"). The bonemeal
 * test guards the other side of that rule - the seed grows its stalk top-down through real block updates,
 * and every segment must survive the moment before the one below it is placed.
 */
public class EyePlantLootGameTest {
    /** Leaves the arena's barrier ceiling (y=7) alone - see gametest-arena-is-8-blocks-tall. */
    private static final BlockPos CEILING = new BlockPos(3, 6, 3);
    private static final BlockPos VINE_TOP = CEILING.below();
    private static final BlockPos VINE_BOTTOM = CEILING.below(2);
    private static final BlockPos EYE = CEILING.below(3);

    private static final int SETTLE = 20;

    /**
     * #217: on 1.20.x the eyeball shared the plant preset's random XZ offset, so the full-cube eyeball
     * drifted off its stalk and off the block grid (z-fighting/culling against neighbours). The stalk and
     * both eyeballs must stay grid-aligned, so they always line up with each other.
     */
    @GameTest
    public void eyePlantBlocksAreNotOffset(GameTestHelper helper) {
        for (Block block : List.of(NetherVineBlocks.EYE_VINE, NetherPlantBlocks.EYEBALL, NetherPlantBlocks.EYEBALL_SMALL)) {
            if (block.defaultBlockState().hasOffsetFunction()) {
                helper.fail(Component.literal(block + " has a random model offset"));
            }
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void cuttingTheStalkDropsTheEyeballLoot(GameTestHelper helper) {
        plant(helper, NetherPlantBlocks.EYEBALL);
        final ServerPlayer player = MockPlayers.survival(helper, VINE_TOP.east(2));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.gameMode.destroyBlock(helper.absolutePos(VINE_TOP));

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> check(helper, "cutting the stalk", 1, 2))
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void cuttingTheStalkDropsTheSmallEyeballLoot(GameTestHelper helper) {
        plant(helper, NetherPlantBlocks.EYEBALL_SMALL);
        final ServerPlayer player = MockPlayers.survival(helper, VINE_TOP.east(2));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SHEARS));
        player.gameMode.destroyBlock(helper.absolutePos(VINE_TOP));

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> check(helper, "cutting the stalk (small eyeball, shears)", 1, 1))
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void aPistonIntoTheEyeballDropsItsLoot(GameTestHelper helper) {
        plant(helper, NetherPlantBlocks.EYEBALL);
        piston(helper, EYE);

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> check(helper, "a piston into the eyeball", 1, 2))
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void aPistonIntoTheStalkDropsTheEyeballLoot(GameTestHelper helper) {
        plant(helper, NetherPlantBlocks.EYEBALL);
        piston(helper, VINE_BOTTOM);

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> check(helper, "a piston into the stalk", 1, 2))
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void miningTheEyeballTakesTheStalkDownToo(GameTestHelper helper) {
        plant(helper, NetherPlantBlocks.EYEBALL);
        final ServerPlayer player = MockPlayers.survival(helper, EYE.east(2));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
        player.gameMode.destroyBlock(helper.absolutePos(EYE));

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> check(helper, "mining the eyeball", 1, 2))
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void bonemealedEyeSeedGrowsACompletePlant(GameTestHelper helper) {
        helper.setBlock(CEILING, Blocks.NETHERRACK);
        helper.setBlock(VINE_TOP, NetherPlantBlocks.EYE_SEED);
        final BlockPos abs = helper.absolutePos(VINE_TOP);
        ((BlockEyeSeed) NetherPlantBlocks.EYE_SEED).performBonemeal(
                helper.getLevel(), helper.getLevel().getRandom(), abs, helper.getLevel().getBlockState(abs)
        );

        helper.startSequence()
              .thenIdle(2)
              .thenExecute(() -> {
                  BlockPos p = VINE_TOP;
                  int vines = 0;
                  while (helper.getBlockState(p).is(NetherVineBlocks.EYE_VINE)) {
                      vines++;
                      p = p.below();
                  }
                  final var tip = helper.getBlockState(p);
                  if (vines == 0 || !(tip.is(NetherPlantBlocks.EYEBALL) || tip.is(NetherPlantBlocks.EYEBALL_SMALL))) {
                      throw helper.assertionException(Component.literal(
                              "bonemealed eye seed grew " + vines + " stalk segments ending in " + tip
                                      + ", expected a stalk with an eyeball on its end"
                      ));
                  }
              })
              .thenSucceed();
    }

    private static void plant(GameTestHelper helper, Block eyeball) {
        helper.setBlock(CEILING, Blocks.NETHERRACK);
        helper.setBlock(VINE_TOP, NetherVineBlocks.EYE_VINE);
        helper.setBlock(VINE_BOTTOM, NetherVineBlocks.EYE_VINE);
        helper.setBlock(EYE, eyeball);
    }

    private static void piston(GameTestHelper helper, BlockPos target) {
        helper.setBlock(target.west(), Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.EAST));
        helper.setBlock(target.west(2), Blocks.REDSTONE_BLOCK);
    }

    /** Nothing of the plant may be left standing, and the eyeball's seeds and slime must be on the floor. */
    private static void check(GameTestHelper helper, String how, int minSeeds, int minSlime) {
        final List<String> failures = new ArrayList<>();
        for (BlockPos p : List.of(VINE_TOP, VINE_BOTTOM, EYE)) {
            final var state = helper.getBlockState(p);
            if (state.is(NetherVineBlocks.EYE_VINE) || state.is(NetherPlantBlocks.EYEBALL) || state.is(NetherPlantBlocks.EYEBALL_SMALL)) {
                failures.add(p + " is still " + state + " - the plant did not come down");
            }
        }
        final List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class,
                new AABB(helper.absolutePos(EYE)).inflate(8.0)
        );
        final int seeds = count(drops, NetherPlantBlocks.EYE_SEED.asItem());
        final int slime = count(drops, Items.SLIME_BALL);
        if (seeds < minSeeds) failures.add("dropped " + seeds + " eye seeds, expected at least " + minSeeds);
        if (slime < minSlime) failures.add("dropped " + slime + " slime balls, expected at least " + minSlime);
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Eye plant loot regression (" + how + "):\n - " + String.join("\n - ", failures)
            ));
        }
    }

    private static int count(List<ItemEntity> drops, Item item) {
        return drops.stream().filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
    }
}
