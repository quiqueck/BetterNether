package org.betterx.betternether.world.tree.decay;

import org.betterx.betternether.world.tree.build.TreeVoxels;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Makes a rasterised tree survive vanilla leaf decay.
 *
 * <h2>The rule being satisfied</h2>
 * {@code LeavesBlock} stores a {@code distance} in 1..7 and destroys any non-persistent leaf whose
 * distance reaches 7. The value relaxes from the six neighbours - a block in
 * {@code #minecraft:prevents_nearby_leaf_decay} (i.e. any log) counts as 0 - so the real rule is
 * <em>every leaf must be within six six-connected steps, through other leaves, of some log</em>. Note
 * that this is a path length through the foliage, not a straight-line distance: a leaf three blocks from
 * a branch as the crow flies can still be nine steps away around a hollow.
 *
 * <h2>What this class does about it</h2>
 * A breadth-first sweep from every log through the leaves gives each leaf its true distance. Leaves
 * inside the budget get that exact value written into their {@code distance} property, so the tree
 * starts consistent and vanilla's relaxation has nothing to correct. Leaves outside it are handled
 * according to the {@link DecayRepair} policy - and under {@link DecayRepair#CARVE_BRANCH} the fix is to
 * turn part of the leaf path itself into wood.
 *
 * <h2>Why carving cannot produce a floating log</h2>
 * The carve walks the breadth-first gradient from a stranded leaf back towards the wood, and converts
 * the prefix of that path running from distance 1 (a cell touching existing wood) up to distance
 * {@code d - budget}. Every converted cell is therefore contiguous with the cell before it, and the
 * first one is contiguous with wood that was already there. There is no step at which an isolated log is
 * placed inside the canopy and hoped for the best.
 * <p>
 * Leaves with no leaf-path to any log at all - a speck knocked loose by surface noise - cannot be
 * reached by a carve and are pruned. That is a shape defect, and deleting it is the honest repair.
 */
public final class LeafDecay {
    /**
     * The largest {@code distance} a leaf may carry and still survive. One below
     * {@code LeavesBlock.DECAY_DISTANCE}, because that value is the one that decays.
     */
    public static final int VANILLA_BUDGET = LeavesBlock.DECAY_DISTANCE - 1;

    private static final int UNREACHED = Integer.MAX_VALUE;

    private LeafDecay() {
    }

    /**
     * Produces a {@link BlockState} for a log carved as a support branch at a tree-local position.
     */
    @FunctionalInterface
    public interface BranchFactory {
        BlockState at(int x, int y, int z);
    }

    /**
     * @param leavesKept    leaves left standing, all of them decay-safe
     * @param leavesPruned  leaves deleted because nothing could reach them
     * @param branchCells   leaf cells converted to wood to support the rest
     * @param leavesPersist leaves marked persistent under {@link DecayRepair#PERSIST}
     */
    public record Result(int leavesKept, int leavesPruned, int branchCells, int leavesPersist) {
        public boolean isClean() {
            return leavesPruned == 0 && branchCells == 0 && leavesPersist == 0;
        }
    }

    public static Result solve(TreeVoxels voxels, DecayRepair repair) {
        return solve(voxels, repair, VANILLA_BUDGET, 32, null);
    }

    /**
     * @param budget    the largest surviving distance; {@link #VANILLA_BUDGET} unless the leaf block
     *                  extends the vanilla property range
     * @param maxCarves an upper bound on carve iterations, so a pathological shape degrades to pruning
     *                  instead of looping
     * @param branches  supplies the wood for carved branches; required for
     *                  {@link DecayRepair#CARVE_BRANCH}
     */
    public static Result solve(
            TreeVoxels voxels,
            DecayRepair repair,
            int budget,
            int maxCarves,
            BranchFactory branches
    ) {
        int[] distance = sweep(voxels);
        int branchCells = 0;

        if (repair == DecayRepair.CARVE_BRANCH && branches != null) {
            // Leaves whose path out could not be turned into a buried branch. They are set aside
            // rather than ending the whole carve: one un-buriable path says nothing about the next
            // leaf, which may well sit over a route that is fully enclosed. Aborting on the first
            // failure (which an earlier version did) loses every remaining branch and hands the
            // entire hem to the pruner.
            final boolean[] giveUp = new boolean[voxels.cellCount()];
            for (int carve = 0; carve < maxCarves; carve++) {
                final int worst = worstReachableLeaf(voxels, distance, budget, giveUp);
                if (worst < 0) break;
                final int converted = carveTowardsWood(voxels, distance, worst, budget, branches);
                if (converted == 0) {
                    giveUp[worst] = true;
                    continue;
                }
                branchCells += converted;
                distance = sweep(voxels);
            }
        }

        return finish(voxels, distance, repair, budget, branchCells);
    }

    /**
     * Breadth-first distance from the nearest log, travelling only through leaves.
     * <p>
     * Logs are sources at 0 but are not themselves traversed onward - which mirrors vanilla, where a log
     * hands 0 to its neighbours but a chain of logs does not carry leaf distance along it.
     */
    private static int[] sweep(TreeVoxels voxels) {
        final int[] distance = new int[voxels.cellCount()];
        final int[] queue = new int[voxels.cellCount()];
        int head = 0;
        int tail = 0;

        for (int i = 0; i < distance.length; i++) {
            if (voxels.kindAt(i) == TreeVoxels.LOG) {
                distance[i] = 0;
                queue[tail++] = i;
            } else {
                distance[i] = UNREACHED;
            }
        }

        while (head < tail) {
            final int current = queue[head++];
            final int next = distance[current] + 1;
            final int x = voxels.localX(current);
            final int y = voxels.localY(current);
            final int z = voxels.localZ(current);

            for (int face = 0; face < 6; face++) {
                final int nx = x + FACE_X[face];
                final int ny = y + FACE_Y[face];
                final int nz = z + FACE_Z[face];
                final int neighbour = voxels.index(nx, ny, nz);
                if (neighbour < 0) continue;
                if (voxels.kindAt(neighbour) != TreeVoxels.LEAF) continue;
                if (distance[neighbour] <= next) continue;
                distance[neighbour] = next;
                queue[tail++] = neighbour;
            }
        }
        return distance;
    }

    /**
     * The reachable leaf furthest over budget, or {@code -1} when none is. Unreachable leaves are
     * deliberately skipped: a carve follows the gradient, and they have no gradient to follow.
     */
    private static int worstReachableLeaf(TreeVoxels voxels, int[] distance, int budget, boolean[] giveUp) {
        int worst = -1;
        int worstDistance = budget;
        for (int i = 0; i < distance.length; i++) {
            if (voxels.kindAt(i) != TreeVoxels.LEAF) continue;
            if (distance[i] == UNREACHED || giveUp[i]) continue;
            if (distance[i] > worstDistance) {
                worstDistance = distance[i];
                worst = i;
            }
        }
        return worst;
    }

    /**
     * Converts the near end of the stranded leaf's path to wood, and returns how many cells changed.
     */
    private static int carveTowardsWood(
            TreeVoxels voxels,
            int[] distance,
            int leaf,
            int budget,
            BranchFactory branches
    ) {
        // Everything at distance <= this becomes wood, which leaves the stranded leaf exactly `budget`
        // steps from the new branch tip.
        final int convertUpTo = distance[leaf] - budget;
        if (convertUpTo < 1) return 0;

        // Collect the whole gradient path first. The walk runs from the stranded leaf inwards, i.e.
        // in *decreasing* distance, but the branch has to be grown in the other direction - see below.
        final List<Integer> path = new ArrayList<>();
        int current = leaf;
        while (distance[current] > 0) {
            final int previous = stepTowardsWood(voxels, distance, current);
            if (previous < 0) break;
            if (distance[previous] >= 1 && distance[previous] <= convertUpTo) {
                path.add(previous);
            }
            current = previous;
        }
        Collections.reverse(path);   // now ordered outwards from the wood: distance 1, 2, 3, ...

        int converted = 0;
        for (int cell : path) {
            // Stop at the first cell that is not fully enclosed by other tree blocks. A support branch
            // exists to feed the foliage from inside it; the moment it reaches a cell that can be seen
            // from outside it stops being structure and becomes a log sticking out through the leaves,
            // which is far uglier than the thin hem the alternative costs. Everything past this point
            // is left to the pruning pass.
            //
            // Growing outwards (rather than converting as the walk runs inwards) is what makes that
            // check meaningful *and* keeps the branch contiguous: the converted cells are always the
            // prefix 1..k of the path, each touching the one before it and the first touching wood
            // that was already there.
            if (!voxels.isBuried(cell)) break;
            final BlockState wood = branches.at(voxels.localX(cell), voxels.localY(cell), voxels.localZ(cell));
            if (wood == null) break;
            voxels.set(cell, TreeVoxels.LOG, wood);
            converted++;
        }
        return converted;
    }

    /**
     * A neighbour one step closer to the wood.
     */
    private static int stepTowardsWood(TreeVoxels voxels, int[] distance, int from) {
        final int target = distance[from] - 1;
        final int x = voxels.localX(from);
        final int y = voxels.localY(from);
        final int z = voxels.localZ(from);
        for (int face = 0; face < 6; face++) {
            final int neighbour = voxels.index(x + FACE_X[face], y + FACE_Y[face], z + FACE_Z[face]);
            if (neighbour >= 0 && distance[neighbour] == target) return neighbour;
        }
        return -1;
    }

    /**
     * Applies the distances to the surviving leaves and disposes of the ones still out of budget.
     */
    private static Result finish(
            TreeVoxels voxels,
            int[] distance,
            DecayRepair repair,
            int budget,
            int branchCells
    ) {
        int kept = 0;
        int pruned = 0;
        int persisted = 0;

        for (int i = 0; i < distance.length; i++) {
            if (voxels.kindAt(i) != TreeVoxels.LEAF) continue;
            final BlockState leaf = voxels.stateAt(i);
            final int d = distance[i];

            if (d > budget) {
                // CARVE_BRANCH falls through to pruning: anything still stranded after the carve budget
                // is either unreachable or not worth another branch.
                if (repair == DecayRepair.PERSIST && leaf.hasProperty(BlockStateProperties.PERSISTENT)) {
                    voxels.set(i, TreeVoxels.LEAF, leaf.setValue(BlockStateProperties.PERSISTENT, true));
                    persisted++;
                } else {
                    voxels.clear(i);
                    pruned++;
                }
                continue;
            }

            BlockState settled = leaf;
            if (settled.hasProperty(BlockStateProperties.DISTANCE)) {
                settled = settled.setValue(BlockStateProperties.DISTANCE, Math.max(1, d));
            }
            if (settled.hasProperty(BlockStateProperties.PERSISTENT)) {
                settled = settled.setValue(BlockStateProperties.PERSISTENT, false);
            }
            voxels.set(i, TreeVoxels.LEAF, settled);
            kept++;
        }

        return new Result(kept, pruned, branchCells, persisted);
    }

    private static final int[] FACE_X = {-1, 1, 0, 0, 0, 0};
    private static final int[] FACE_Y = {0, 0, -1, 1, 0, 0};
    private static final int[] FACE_Z = {0, 0, 0, 0, -1, 1};
}
