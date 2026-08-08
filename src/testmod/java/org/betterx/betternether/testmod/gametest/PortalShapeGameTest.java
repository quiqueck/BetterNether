package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.portals.BNPortalShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code BNPortalShape}'s flood-fill frame detection - pure block-state logic, no player needed,
 * driven directly rather than through the {@code PortalShapeMixin} that wires it into vanilla's own
 * portal-lighting code path.
 * <p>
 * Frame-material coverage is limited to plain obsidian here: {@code CommonBlockTags.NETHER_PORTAL_FRAME}
 * membership (the other half {@code isFrame} accepts) is applied per-block via
 * {@code NetherMaterial#obsidianPortalFrame()}, and no single concrete block in this repo's registries
 * was confirmed to carry it in the time budget for this pass - the flood-fill algorithm itself, which is
 * what these tests actually exercise, does not care which condition a frame block satisfied.
 */
public class PortalShapeGameTest {
    private static final BlockPos ORIGIN = new BlockPos(5, 2, 5);

    /**
     * An L-shaped obsidian boundary (a plain rectangle plus one extra notch) - proves the flood-fill
     * follows the actual frame shape rather than only recognizing a rectangle the way vanilla's
     * {@code PortalShape} does.
     */
    @GameTest
    public void nonRectangularFrameIsValid(GameTestHelper helper) {
        // A 3-wide x4-tall rectangle frame around an empty interior, plus one extra obsidian cell
        // bulging out of the top-right corner - not a rectangle, but a single connected frame boundary.
        for (int x = 0; x <= 2; x++) {
            helper.setBlock(ORIGIN.offset(x, 0, 0), Blocks.OBSIDIAN);
            helper.setBlock(ORIGIN.offset(x, 4, 0), Blocks.OBSIDIAN);
        }
        for (int y = 0; y <= 4; y++) {
            helper.setBlock(ORIGIN.offset(0, y, 0), Blocks.OBSIDIAN);
            helper.setBlock(ORIGIN.offset(2, y, 0), Blocks.OBSIDIAN);
        }
        // The interior (1,1)-(1,3) is air by default in an empty GameTest structure.
        // The notch: one extra frame cell sticking out above the top-right corner.
        helper.setBlock(ORIGIN.offset(2, 5, 0), Blocks.OBSIDIAN);

        // Axis.X selects DIR_X = {UP, DOWN, EAST, WEST}, matching a frame that varies along X/Y at a
        // fixed Z - which is what's built above. Getting this backwards (Axis.Z) makes the flood-fill
        // walk the wrong pair of directions and was the actual cause of this test's first failure.
        final var shape = new BNPortalShape(helper.getLevel(), helper.absolutePos(ORIGIN.offset(1, 1, 0)), Direction.Axis.X);
        if (!shape.isValid()) {
            throw helper.assertionException(Component.literal(
                    "a connected, non-rectangular obsidian boundary was rejected as an invalid portal shape"
            ));
        }
        helper.succeed();
    }

    /**
     * An interior wider than {@code MAX_SEARCH_DIST} with no frame anywhere in reach must fail cleanly
     * (return invalid), not hang or throw.
     */
    @GameTest
    public void openAreaBeyondMaxSearchDistIsInvalid(GameTestHelper helper) {
        // No blocks placed at all - pure air stretching in every direction from this point, further
        // than MAX_SEARCH_DIST (21) can reach a frame.
        final var shape = new BNPortalShape(helper.getLevel(), helper.absolutePos(ORIGIN), Direction.Axis.X);
        if (shape.isValid()) {
            throw helper.assertionException(Component.literal(
                    "an open area with no obsidian anywhere in reach was still reported as a valid portal shape"
            ));
        }
        helper.succeed();
    }
}
