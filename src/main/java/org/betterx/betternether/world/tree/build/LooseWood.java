package org.betterx.betternether.world.tree.build;

/**
 * Deletes wood that is not connected to the tree's base.
 *
 * <h2>Why this is needed</h2>
 * A bark displacement is added to a signed distance, so where it is locally larger than the trunk's
 * radius it does not roughen the surface - it removes it, cutting the run into pieces. A thin branch
 * tip, or a trunk pinched between two segments, can come apart this way. The pieces that are still
 * attached read as bark; the pieces that are not read as logs hanging in mid-air, which is the single
 * most recognisable artefact of a badly built tree.
 *
 * <p>Connectivity is tested over all 26 neighbours rather than 6. Two cells meeting only at an edge or a
 * corner still look joined, and a diagonal step is exactly how a thin, slanted branch rasterises - a
 * face-only test would amputate branches that are visually fine.
 */
final class LooseWood {
    private LooseWood() {
    }

    /**
     * Removes every log cell with no 26-connected path back to the tree's base column.
     *
     * @return how many cells were removed
     */
    static int prune(TreeVoxels voxels) {
        final boolean[] attached = new boolean[voxels.cellCount()];
        final int[] queue = new int[voxels.cellCount()];
        int head = 0;
        int tail = 0;

        // Seed from the lowest layer that has any wood in it - the tree's contact with the ground.
        // Seeding from the origin column alone would be wrong for a trunk wide enough that (0, y, 0)
        // is not itself a log cell, and seeding from every log in that column would happily anchor a
        // fragment that is floating directly above the stump.
        final int groundLayer = lowestWoodLayer(voxels);
        if (groundLayer == Integer.MIN_VALUE) return 0;
        for (int x = voxels.minX(); x < voxels.minX() + voxels.sizeX(); x++) {
            for (int z = voxels.minZ(); z < voxels.minZ() + voxels.sizeZ(); z++) {
                final int seed = voxels.index(x, groundLayer, z);
                if (seed >= 0 && voxels.kindAt(seed) == TreeVoxels.LOG && !attached[seed]) {
                    attached[seed] = true;
                    queue[tail++] = seed;
                }
            }
        }

        while (head < tail) {
            final int current = queue[head++];
            final int x = voxels.localX(current);
            final int y = voxels.localY(current);
            final int z = voxels.localZ(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        final int neighbour = voxels.index(x + dx, y + dy, z + dz);
                        if (neighbour < 0 || attached[neighbour]) continue;
                        if (voxels.kindAt(neighbour) != TreeVoxels.LOG) continue;
                        attached[neighbour] = true;
                        queue[tail++] = neighbour;
                    }
                }
            }
        }

        return sweepUnattached(voxels, attached);
    }

    /**
     * The lowest tree-local Y that contains any wood, or {@link Integer#MIN_VALUE} when there is none.
     */
    private static int lowestWoodLayer(TreeVoxels voxels) {
        for (int y = voxels.minY(); y < voxels.minY() + voxels.sizeY(); y++) {
            for (int x = voxels.minX(); x < voxels.minX() + voxels.sizeX(); x++) {
                for (int z = voxels.minZ(); z < voxels.minZ() + voxels.sizeZ(); z++) {
                    if (voxels.kindAt(x, y, z) == TreeVoxels.LOG) return y;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private static int sweepUnattached(TreeVoxels voxels, boolean[] attached) {
        int removed = 0;
        for (int i = 0; i < attached.length; i++) {
            if (voxels.kindAt(i) == TreeVoxels.LOG && !attached[i]) {
                voxels.clear(i);
                removed++;
            }
        }
        return removed;
    }
}
