package org.betterx.betternether.world.tree.build;

import net.minecraft.world.level.block.state.BlockState;

/**
 * The whole tree, in tree-local integer coordinates, before any of it touches the level.
 *
 * <h2>Why a buffer at all</h2>
 * Leaf decay is a property of the finished tree - whether a given leaf is close enough to <em>some</em>
 * log depends on every log the tree ended up having. It therefore cannot be decided while the tree is
 * being drawn, and fixing it needs the freedom to change the geometry, which is gone the moment blocks
 * are in the world. Building here first, solving, and only then committing is what lets
 * {@link org.betterx.betternether.world.tree.decay.LeafDecay} do its job. It also means the world is
 * read exactly once per position at commit time, and never at all during shape evaluation.
 *
 * <p>Backed by flat arrays over a fixed box rather than a map: a large tree is on the order of 50k
 * cells, which is a few hundred kilobytes and far cheaper to sweep than the hashing a map would cost on
 * every one of the solver's breadth-first passes.
 */
public final class TreeVoxels {
    public static final byte EMPTY = 0;
    public static final byte LOG = 1;
    public static final byte LEAF = 2;

    private final int minX;
    private final int minY;
    private final int minZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    private final byte[] kind;
    private final BlockState[] state;

    public TreeVoxels(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.sizeX = Math.max(1, sizeX);
        this.sizeY = Math.max(1, sizeY);
        this.sizeZ = Math.max(1, sizeZ);
        final int cells = this.sizeX * this.sizeY * this.sizeZ;
        this.kind = new byte[cells];
        this.state = new BlockState[cells];
    }

    public int minX() {
        return minX;
    }

    public int minY() {
        return minY;
    }

    public int minZ() {
        return minZ;
    }

    public int sizeX() {
        return sizeX;
    }

    public int sizeY() {
        return sizeY;
    }

    public int sizeZ() {
        return sizeZ;
    }

    public int cellCount() {
        return kind.length;
    }

    public boolean inBounds(int x, int y, int z) {
        return x >= minX && x < minX + sizeX
                && y >= minY && y < minY + sizeY
                && z >= minZ && z < minZ + sizeZ;
    }

    /**
     * Flat index of a local position, or {@code -1} when it is outside the buffer.
     */
    public int index(int x, int y, int z) {
        if (!inBounds(x, y, z)) return -1;
        return ((x - minX) * sizeY + (y - minY)) * sizeZ + (z - minZ);
    }

    public int localX(int index) {
        return index / (sizeY * sizeZ) + minX;
    }

    public int localY(int index) {
        return (index / sizeZ) % sizeY + minY;
    }

    public int localZ(int index) {
        return index % sizeZ + minZ;
    }

    public byte kindAt(int index) {
        return index < 0 ? EMPTY : kind[index];
    }

    public byte kindAt(int x, int y, int z) {
        return kindAt(index(x, y, z));
    }

    public BlockState stateAt(int index) {
        return index < 0 ? null : state[index];
    }

    public void set(int index, byte what, BlockState blockState) {
        if (index < 0) return;
        kind[index] = what;
        state[index] = blockState;
    }

    /**
     * Writes a log, overwriting whatever was there. Logs win over leaves - a branch passing through
     * foliage is wood, not leaves.
     */
    public void setLog(int x, int y, int z, BlockState blockState) {
        final int i = index(x, y, z);
        if (i < 0 || blockState == null) return;
        kind[i] = LOG;
        state[i] = blockState;
    }

    /**
     * Writes a leaf only into an empty cell, so foliage never eats the trunk it hangs on.
     */
    public void setLeafIfEmpty(int x, int y, int z, BlockState blockState) {
        final int i = index(x, y, z);
        if (i < 0 || blockState == null || kind[i] != EMPTY) return;
        kind[i] = LEAF;
        state[i] = blockState;
    }

    public void clear(int index) {
        if (index < 0) return;
        kind[index] = EMPTY;
        state[index] = null;
    }

    /**
     * {@code true} when every one of the six neighbours is occupied - i.e. this cell cannot be seen
     * from outside. Cells on the buffer boundary count as exposed, since what lies beyond is unknown.
     */
    public boolean isBuried(int index) {
        if (index < 0) return false;
        final int x = localX(index);
        final int y = localY(index);
        final int z = localZ(index);
        for (int face = 0; face < 6; face++) {
            final int n = index(x + FACE_X[face], y + FACE_Y[face], z + FACE_Z[face]);
            if (n < 0 || kind[n] == EMPTY) return false;
        }
        return true;
    }

    /**
     * Wood that can be seen from outside while sitting in foliage - a log breaking the canopy
     * surface. Trunk and branches out in the open are not counted: they are meant to be visible, and
     * they have no leaves around them.
     *
     * @param minLeafNeighbours how much foliage must surround a cell (of its 26 neighbours) before it
     *                          counts as "inside the canopy" rather than out in the open
     */
    public int countWoodBreakingFoliage(int minLeafNeighbours) {
        int n = 0;
        for (int i = 0; i < kind.length; i++) {
            if (kind[i] != LOG || isBuried(i)) continue;
            final int x = localX(i);
            final int y = localY(i);
            final int z = localZ(i);
            int leaves = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        if (kindAt(x + dx, y + dy, z + dz) == LEAF) leaves++;
                    }
                }
            }
            if (leaves >= minLeafNeighbours) n++;
        }
        return n;
    }

    private static final int[] FACE_X = {-1, 1, 0, 0, 0, 0};
    private static final int[] FACE_Y = {0, 0, -1, 1, 0, 0};
    private static final int[] FACE_Z = {0, 0, 0, 0, -1, 1};

    public int count(byte what) {
        int n = 0;
        for (byte b : kind) {
            if (b == what) n++;
        }
        return n;
    }

    /**
     * Visits every non-empty cell.
     */
    public void forEach(CellVisitor visitor) {
        for (int i = 0; i < kind.length; i++) {
            if (kind[i] != EMPTY) {
                visitor.visit(localX(i), localY(i), localZ(i), kind[i], state[i]);
            }
        }
    }

    @FunctionalInterface
    public interface CellVisitor {
        void visit(int x, int y, int z, byte kind, BlockState state);
    }
}
