package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BNBlockProperties.PottedPlantShape;
import org.betterx.betternether.blocks.BlockPottedPlant;
import org.betterx.betternether.registry.block.NetherDecorBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * #85: a BetterNether pot (8px tall) keeps its plant in the cell <em>above</em> it, so a filled pot is two
 * cells tall and the plant cell's top is the only surface left to build on. The plant had a plant-sized
 * outline and no collision, so nothing could rest on it. It now behaves like leaves: a full-cube outline
 * and collision (blocks placed against it sit flush on the plant cell's top, mobs stand on it), but an
 * empty support shape (no torches, buttons or pressure plates on top, exactly like leaves).
 * <p>
 * Every placement goes through a real raycast from above, the way a player's crosshair would pick the face.
 */
public class PottedPlantBuildingGameTest {
    private static final BlockPos POT = new BlockPos(3, 1, 3);
    private static final BlockPos PLANT = POT.above();
    private static final BlockPos ABOVE = PLANT.above();
    private static final BlockPos PLAYER = new BlockPos(1, 1, 1);

    @GameTest(maxTicks = 40)
    public void blocksCanBePlacedOnTopOfAPottedPlant(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        for (Block pot : List.of(NetherDecorBlocks.CINCINNASITE_POT, NetherDecorBlocks.BRICK_POT)) {
            for (PottedPlantShape shape : List.of(PottedPlantShape.AGAVE, PottedPlantShape.NETHER_GRASS, PottedPlantShape.REEDS)) {
                final String what = shape + " in " + pot.getDescriptionId() + ": ";
                potWithPlant(helper, pot, shape);

                final BlockHitResult hit = lookDownAt(helper, PLANT);
                if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(helper.absolutePos(PLANT))) {
                    failures.add(what + "looking down hits " + hit.getBlockPos() + " (" + hit.getType() + "), expected the plant cell");
                    continue;
                }
                if (hit.getDirection() != Direction.UP || Math.abs(hit.getLocation().y - helper.absolutePos(ABOVE).getY()) > 1.0e-6) {
                    failures.add(what + "hit face " + hit.getDirection() + " at y=" + hit.getLocation().y + ", expected the plant cell's top");
                }

                use(helper, player, new ItemStack(Items.STONE), hit);
                if (!helper.getBlockState(ABOVE).is(Blocks.STONE)) {
                    failures.add(what + "stone placed on top ended up as " + helper.getBlockState(ABOVE));
                }
                if (!helper.getBlockState(PLANT).is(NetherPlantBlocks.POTTED_PLANT) || !helper.getBlockState(POT).is(pot)) {
                    failures.add(what + "placing on top disturbed the pot/plant: " + helper.getBlockState(POT) + " / " + helper.getBlockState(PLANT));
                }
            }
        }
        fail(helper, "building on a potted plant", failures);
        helper.succeed();
    }

    /** Leaves parity: carpets rest on top, but torches need a sturdy face and leaves (and the plant) have none. */
    @GameTest(maxTicks = 40)
    public void topBehavesLikeLeaves(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);

        potWithPlant(helper, NetherDecorBlocks.CINCINNASITE_POT, PottedPlantShape.AGAVE);
        use(helper, player, new ItemStack(Items.TORCH), lookDownAt(helper, PLANT));
        if (!helper.getBlockState(ABOVE).isAir()) {
            failures.add("a torch could be placed on the plant (" + helper.getBlockState(ABOVE) + "), leaves refuse it");
        }
        use(helper, player, new ItemStack(Items.MOSS_CARPET), lookDownAt(helper, PLANT));
        if (!helper.getBlockState(ABOVE).is(Blocks.MOSS_CARPET)) {
            failures.add("a carpet could not be placed on the plant (" + helper.getBlockState(ABOVE) + ")");
        }
        fail(helper, "leaves parity", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 60)
    public void mobsStandOnAPottedPlant(GameTestHelper helper) {
        potWithPlant(helper, NetherDecorBlocks.CINCINNASITE_POT, PottedPlantShape.AGAVE);
        final Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, Vec3.atBottomCenterOf(ABOVE).add(0, 0.5, 0));
        final double top = helper.absolutePos(ABOVE).getY();
        helper.runAfterDelay(30, () -> {
            if (!zombie.onGround() || Math.abs(zombie.getY() - top) > 1.0e-3) {
                throw helper.assertionException(Component.literal(
                        "zombie dropped onto a potted plant is at y=" + zombie.getY() + " (onGround=" + zombie.onGround()
                                + "), expected it to stand on the plant cell's top at y=" + top));
            }
            helper.succeed();
        });
    }

    /** The pot's own behaviour must survive the new shapes: planting, breaking the plant, breaking the pot. */
    @GameTest(maxTicks = 40)
    public void potInteractionsStillWork(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final ServerLevel level = helper.getLevel();
        final Item agave = PottedPlantShape.AGAVE.getItem();

        // planting: right-click the pot's top with a pottable plant
        helper.setBlock(POT, NetherDecorBlocks.CINCINNASITE_POT);
        helper.setBlock(PLANT, Blocks.AIR);
        final ItemStack seeds = new ItemStack(agave, 2);
        use(helper, player, seeds, lookDownAt(helper, POT));
        if (!helper.getBlockState(PLANT).is(NetherPlantBlocks.POTTED_PLANT)
                || helper.getBlockState(PLANT).getValue(BlockPottedPlant.PLANT) != PottedPlantShape.AGAVE) {
            failures.add("planting agave into the pot gave " + helper.getBlockState(PLANT));
        }
        if (seeds.getCount() != 1) failures.add("planting consumed " + (2 - seeds.getCount()) + " agave, expected 1");

        // a second plant can't go in while one is there, and the click must not replace the plant
        use(helper, player, new ItemStack(agave), lookDownAt(helper, POT));
        if (!helper.getBlockState(PLANT).is(NetherPlantBlocks.POTTED_PLANT)) {
            failures.add("re-planting into a filled pot replaced the plant with " + helper.getBlockState(PLANT));
        }

        // breaking the plant leaves the pot and drops the plant
        clearItems(helper);
        player.gameMode.destroyBlock(helper.absolutePos(PLANT));
        if (!helper.getBlockState(PLANT).isAir()) failures.add("breaking the plant left " + helper.getBlockState(PLANT));
        if (!helper.getBlockState(POT).is(NetherDecorBlocks.CINCINNASITE_POT)) failures.add("breaking the plant took the pot along");
        if (count(level, helper, agave) != 1) failures.add("breaking the plant dropped " + count(level, helper, agave) + " agave, expected 1");

        // breaking the pot pops the plant (and drops it)
        potWithPlant(helper, NetherDecorBlocks.CINCINNASITE_POT, PottedPlantShape.AGAVE);
        clearItems(helper);
        player.gameMode.destroyBlock(helper.absolutePos(POT));
        if (!helper.getBlockState(PLANT).isAir()) failures.add("breaking the pot left the plant floating: " + helper.getBlockState(PLANT));
        if (count(level, helper, agave) != 1) failures.add("breaking the pot dropped " + count(level, helper, agave) + " agave, expected 1");

        // the plant is solid now, so it must not be planted into a mob standing on the pot
        helper.setBlock(POT, NetherDecorBlocks.CINCINNASITE_POT);
        helper.setBlock(PLANT, Blocks.AIR);
        final Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, Vec3.atBottomCenterOf(POT).add(0, 0.5, 0));
        use(helper, player, new ItemStack(agave), lookDownAt(helper, POT));
        if (!helper.getBlockState(PLANT).isAir()) failures.add("a plant was planted into a zombie standing on the pot");
        zombie.discard();

        fail(helper, "pot interactions", failures);
        helper.succeed();
    }

    /**
     * Mining a filled pot with the tool it needs must drop the plant as well as the pot, exactly once -
     * the same as when the pot is washed away. Breaking the plant itself must not drop it twice either.
     */
    @GameTest(maxTicks = 40)
    public void miningAFilledPotDropsThePlantOnce(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final ServerLevel level = helper.getLevel();
        // The Fireruby pickaxe carries ruby_fire once setupItemStack has run, so its drop hook is exercised too.
        final ItemStack rubyPickaxe = new ItemStack(NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT));
        ItemStackHelper.callItemStackSetupIfPossible(rubyPickaxe, level.registryAccess());
        final List<ItemStack> tools = List.of(
                ItemStack.EMPTY,
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.NETHERITE_PICKAXE),
                rubyPickaxe
        );
        for (Block pot : List.of(NetherDecorBlocks.CINCINNASITE_POT, NetherDecorBlocks.BRICK_POT)) {
            for (PottedPlantShape shape : PottedPlantShape.values()) {
                final Item plant = shape.getItem();
                for (ItemStack tool : tools) {
                    final String what = shape + " in " + pot.getDescriptionId() + " with " + tool + ": ";

                    potWithPlant(helper, pot, shape);
                    clearItems(helper);
                    player.setItemInHand(InteractionHand.MAIN_HAND, tool.copy());
                    player.gameMode.destroyBlock(helper.absolutePos(POT));
                    if (!helper.getBlockState(POT).isAir() || !helper.getBlockState(PLANT).isAir()) {
                        failures.add(what + "mining the pot left " + helper.getBlockState(POT) + " / " + helper.getBlockState(PLANT));
                    }
                    if (count(level, helper, plant) != 1) {
                        failures.add(what + "mining the pot dropped " + count(level, helper, plant) + " " + plant + ", expected 1");
                    }

                    potWithPlant(helper, pot, shape);
                    clearItems(helper);
                    player.setItemInHand(InteractionHand.MAIN_HAND, tool.copy());
                    player.gameMode.destroyBlock(helper.absolutePos(PLANT));
                    if (count(level, helper, plant) != 1) {
                        failures.add(what + "mining the plant dropped " + count(level, helper, plant) + " " + plant + ", expected 1");
                    }
                }
            }
        }
        clearItems(helper);
        fail(helper, "mining a filled pot", failures);
        helper.succeed();
    }

    private static void potWithPlant(GameTestHelper helper, Block pot, PottedPlantShape shape) {
        helper.setBlock(ABOVE, Blocks.AIR);
        helper.setBlock(POT, pot);
        helper.setBlock(PLANT, NetherPlantBlocks.POTTED_PLANT.defaultBlockState().setValue(BlockPottedPlant.PLANT, shape));
    }

    /** What a crosshair 2 blocks above the cell's centre, looking straight down, would pick. */
    private static BlockHitResult lookDownAt(GameTestHelper helper, BlockPos rel) {
        final Vec3 centre = Vec3.atCenterOf(helper.absolutePos(rel));
        return helper.getLevel().clip(new ClipContext(
                centre.add(0, 2.5, 0), centre.add(0, -0.5, 0),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()
        ));
    }

    private static void use(GameTestHelper helper, ServerPlayer player, ItemStack stack, BlockHitResult hit) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.gameMode.useItemOn(player, helper.getLevel(), stack, InteractionHand.MAIN_HAND, hit);
    }

    private static AABB box(GameTestHelper helper) {
        return new AABB(helper.absolutePos(POT)).inflate(3.0);
    }

    private static void clearItems(GameTestHelper helper) {
        helper.getLevel().getEntitiesOfClass(ItemEntity.class, box(helper)).forEach(ItemEntity::discard);
    }

    private static int count(ServerLevel level, GameTestHelper helper, Item item) {
        return level.getEntitiesOfClass(ItemEntity.class, box(helper), e -> e.getItem().is(item))
                    .stream().mapToInt(e -> e.getItem().getCount()).sum();
    }

    private static void fail(GameTestHelper helper, String what, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(what + " regression:\n - " + String.join("\n - ", failures)));
        }
    }
}
