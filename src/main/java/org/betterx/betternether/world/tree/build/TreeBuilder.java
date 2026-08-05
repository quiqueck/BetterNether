package org.betterx.betternether.world.tree.build;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.canopy.CanopyShape;
import org.betterx.betternether.world.tree.decay.DecayRepair;
import org.betterx.betternether.world.tree.decay.LeafDecay;
import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.palette.TreePalette;
import org.betterx.betternether.world.tree.skeleton.Anchor;
import org.betterx.betternether.world.tree.skeleton.Segment;
import org.betterx.betternether.world.tree.skeleton.Skeleton;
import org.betterx.betternether.world.tree.skeleton.TrunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Assembles a {@link TrunkShape}, a {@link CanopyShape} and a {@link TreePalette} into a tree.
 *
 * <h2>Two phases, on purpose</h2>
 * {@link #build(TreeSpace, RandomSource)} does all the geometry, the palette and the leaf-decay solve,
 * and touches no level at all - it is pure, deterministic given its {@link RandomSource}, and therefore
 * unit-testable without a running server. {@link #commit(LevelAccessor, TreeSpace, TreeVoxels)} is the
 * only part that reads or writes the world, and it does so exactly once per cell.
 */
public final class TreeBuilder {
    private final TrunkShape trunk;
    private final CanopyShape canopy;
    private final TreePalette palette;
    private final Volume.Displacement bark;
    private final DecayRepair repair;
    private final int decayBudget;
    private final int maxCarves;
    private final Predicate<BlockState> replaceable;
    private final boolean pruneLooseWood;
    private final int writeFlags;

    private TreeBuilder(Builder b) {
        this.trunk = b.trunk;
        this.canopy = b.canopy;
        this.palette = b.palette;
        this.bark = b.bark;
        this.repair = b.repair;
        this.decayBudget = b.decayBudget;
        this.maxCarves = b.maxCarves;
        this.replaceable = b.replaceable;
        this.pruneLooseWood = b.pruneLooseWood;
        this.writeFlags = b.writeFlags;
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     * Which tree-local cells the tree is allowed to occupy.
     *
     * <h2>Why the shape has to know</h2>
     * Terrain the tree cannot overwrite is not merely "a block that does not get placed" - it
     * silently invalidates the leaf-decay solve. A trunk running into a cliff loses its logs at
     * commit time, and the leaves that were relying on those logs are then orphaned, decay-eligible,
     * and nothing is left to notice. So the mask has to be applied <em>before</em> the solve, not as
     * a filter on the way out.
     * <p>
     * Keeping it an interface rather than a level lookup is what lets
     * {@link TreeBuilder#build(TreeSpace, RandomSource, CellFilter)} stay pure and unit-testable:
     * tests pass {@link #ANY}, real placement passes one backed by the level.
     */
    @FunctionalInterface
    public interface CellFilter {
        CellFilter ANY = (x, y, z) -> true;

        boolean canOccupy(int x, int y, int z);
    }

    /**
     * Everything but the world writes, on the assumption that nothing is in the way. Deterministic
     * given its {@link RandomSource}, so this is the entry point for tests.
     */
    public TreeVoxels build(TreeSpace space, RandomSource random) {
        return build(space, random, CellFilter.ANY);
    }

    /**
     * Shape, blocks and a decay-clean result, restricted to the cells {@code filter} allows.
     */
    public TreeVoxels build(TreeSpace space, RandomSource random, CellFilter filter) {
        final Skeleton skeleton = trunk.build(space, random);
        final TreeVoxels voxels = allocate(skeleton);

        rasterizeWood(voxels, skeleton, space, random, filter);
        final int woodRaw = voxels.count(TreeVoxels.LOG);
        if (pruneLooseWood) {
            LooseWood.prune(voxels);
        }
        final int woodKept = voxels.count(TreeVoxels.LOG);
        rasterizeFoliage(voxels, skeleton, space, random, filter);
        final int leavesRaw = voxels.count(TreeVoxels.LEAF);
        // Measured before the solve, so it attributes surfacing wood to the trunk shape rather than to
        // the carve. The two need different fixes and are easy to confuse.
        final int breakingBefore = DEBUG ? voxels.countWoodBreakingFoliage(8) : 0;

        final LeafDecay.Result decay = LeafDecay.solve(
                voxels, repair, decayBudget, maxCarves,
                // Axis Y for carved branches: the carve follows the leaf path one cell at a time and
                // the factory is handed a position, not a direction, so there is no run to take an
                // axis from. A support branch is a handful of blocks deep inside foliage; `carved` is
                // in the query so a palette that cares can pick a bark or twig block that has no axis
                // at all.
                (x, y, z) -> palette.log(new TreePalette.LogQuery(
                        world(space, x, y, z),
                        new Vector3f(x, y, z),
                        y,
                        Direction.Axis.Y,
                        true,
                        random
                ))
        );
        settleColumnTops(voxels);
        if (DEBUG) {
            System.out.println(String.format(
                    "[tree] segments=%d anchors=%d | wood raw=%d kept=%d | leaves raw=%d kept=%d "
                            + "pruned=%d branch=%d persist=%d | wood breaking foliage: %d before solve,"
                            + " %d after",
                    skeleton.segments().size(), skeleton.anchors().size(),
                    woodRaw, woodKept, leavesRaw,
                    decay.leavesKept(), decay.leavesPruned(), decay.branchCells(), decay.leavesPersist(),
                    breakingBefore, voxels.countWoodBreakingFoliage(8)));
        }
        return voxels;
    }

    /**
     * Gives the palette the last word on every log that ended up with nothing on top of it.
     * <p>
     * Runs after the decay solve, which is the first moment the wood is final: the prune deletes logs
     * and {@link org.betterx.betternether.world.tree.decay.DecayRepair#CARVE_BRANCH} adds them, so a
     * cell that had a log above it while the trunk was being drawn may not have one by the end, and
     * the other way round. See {@link TreePalette.LogSource#topOfColumn(BlockState)}.
     */
    private void settleColumnTops(TreeVoxels voxels) {
        for (int i = 0; i < voxels.cellCount(); i++) {
            if (voxels.kindAt(i) != TreeVoxels.LOG) continue;
            final int x = voxels.localX(i);
            final int y = voxels.localY(i);
            final int z = voxels.localZ(i);
            if (voxels.kindAt(x, y + 1, z) == TreeVoxels.LOG) continue;

            final BlockState state = voxels.stateAt(i);
            final BlockState settled = palette.topOfColumn(state);
            if (settled != null && settled != state) voxels.set(i, TreeVoxels.LOG, settled);
        }
    }

    /**
     * Set {@code -Dbetternether.tree.debug=true} to print a per-tree build summary. Cheap enough to
     * leave in: the counts are the only window into a pipeline whose intermediate stages are all
     * discarded before anything reaches the world.
     */
    private static final boolean DEBUG = Boolean.getBoolean("betternether.tree.debug");

    private static BlockPos world(TreeSpace space, int x, int y, int z) {
        return new BlockPos(
                space.origin().getX() + x,
                space.origin().getY() + y,
                space.origin().getZ() + z
        );
    }

    /**
     * Writes a built tree into the level, skipping anything outside the write zone or blocked by
     * terrain.
     *
     * @return how many blocks were actually placed
     */
    public int commit(LevelAccessor level, TreeSpace space, TreeVoxels voxels) {
        final BlockPos origin = space.origin();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final int[] placed = {0};

        voxels.forEach((x, y, z, kind, state) -> {
            // The same test the CellFilter already applied, repeated only as a guard: by the time a
            // buffer built with the matching filter reaches here nothing should be rejected, and
            // anything that is has bypassed the decay solve.
            if (!space.canWrite(x, z)) return;
            pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
            if (level.isOutsideBuildHeight(pos)) return;
            if (!replaceable.test(level.getBlockState(pos))) return;
            level.setBlock(pos, state, writeFlags);
            placed[0]++;
        });
        return placed[0];
    }

    /**
     * The cell filter for a real placement: inside the write zone, inside the world, and standing on
     * something this tree is allowed to overwrite.
     */
    public CellFilter filterFor(LevelAccessor level, TreeSpace space) {
        final BlockPos origin = space.origin();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        return (x, y, z) -> {
            if (!space.canWrite(x, z)) return false;
            pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
            if (level.isOutsideBuildHeight(pos)) return false;
            return replaceable.test(level.getBlockState(pos));
        };
    }

    /**
     * Build and commit in one call - the normal entry point, and the one that gets the terrain mask
     * right.
     */
    public int grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        final TreeSpace space = TreeSpace.of(level, origin);
        return commit(level, space, build(space, random, filterFor(level, space)));
    }

    // ---------------------------------------------------------------- rasterising

    private TreeVoxels allocate(Skeleton skeleton) {
        final Bounds bounds = new Bounds();
        for (Segment s : skeleton.segments()) {
            // The bark displacement pushes the surface outwards as well as inwards, so the box has to
            // allow for it or the crackle gets shaved off flat at the edges.
            final float r = s.maxRadius() + BARK_MARGIN;
            bounds.include(s.from(), r);
            bounds.include(s.to(), r);
        }
        for (Anchor a : skeleton.anchors()) {
            bounds.include(a.position(), canopy.reach() * Math.max(1, a.scale()));
        }
        // A tree with no segments at all would give an inverted box; one cell keeps everything downstream
        // total.
        if (bounds.empty) bounds.include(new Vector3f(0, 0, 0), 1);
        // The base block itself is always in range, whatever the shape decided.
        bounds.include(new Vector3f(0, 0, 0), 1);

        return new TreeVoxels(
                bounds.minX, bounds.minY, bounds.minZ,
                bounds.maxX - bounds.minX + 1,
                bounds.maxY - bounds.minY + 1,
                bounds.maxZ - bounds.minZ + 1
        );
    }

    private void rasterizeWood(
            TreeVoxels voxels, Skeleton skeleton, TreeSpace space, RandomSource random, CellFilter filter
    ) {
        for (Segment segment : skeleton.segments()) {
            Volume volume = segment.toVolume();
            if (bark != null) volume = volume.displace(bark);

            final Bounds bounds = new Bounds();
            final float r = segment.maxRadius() + BARK_MARGIN;
            bounds.include(segment.from(), r);
            bounds.include(segment.to(), r);

            for (int x = bounds.minX; x <= bounds.maxX; x++) {
                for (int y = bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = bounds.minZ; z <= bounds.maxZ; z++) {
                        if (!volume.contains(x, y, z)) continue;
                        if (!filter.canOccupy(x, y, z)) continue;
                        final BlockState state = palette.log(new TreePalette.LogQuery(
                                world(space, x, y, z), new Vector3f(x, y, z),
                                y, segment.axis(), false, random
                        ));
                        voxels.setLog(x, y, z, state);
                    }
                }
            }
        }
    }

    private void rasterizeFoliage(
            TreeVoxels voxels, Skeleton skeleton, TreeSpace space, RandomSource random, CellFilter filter
    ) {
        final Anchor primary = primaryAnchor(skeleton);
        // Primary lobe first. Foliage only fills cells that are still empty, so where two lobes
        // overlap the one rasterised first owns the contested cells - and if that is not the primary
        // lobe, any mark the palette wanted to put on the tree's defining canopy silently lands on
        // cells that are already spoken for and vanishes. Ordering is the whole fix; nothing else
        // about the shape changes, because the union of the lobes is the same either way.
        for (Anchor anchor : orderedAnchors(skeleton, primary)) {
            final Volume volume = canopy.volumeAt(anchor, space, random);
            final float reach = canopy.reach() * Math.max(1, anchor.scale()) + 1;
            final boolean isPrimary = anchor == primary;

            final Bounds bounds = new Bounds();
            bounds.include(anchor.position(), reach);

            for (int x = bounds.minX; x <= bounds.maxX; x++) {
                for (int y = bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = bounds.minZ; z <= bounds.maxZ; z++) {
                        if (!voxels.inBounds(x, y, z)) continue;
                        if (voxels.kindAt(x, y, z) != TreeVoxels.EMPTY) continue;
                        if (!volume.contains(x, y, z)) continue;
                        if (!filter.canOccupy(x, y, z)) continue;

                        final Vector3f local = new Vector3f(x, y, z);
                        final BlockState state = palette.leaf(new TreePalette.LeafQuery(
                                world(space, x, y, z), local, anchor.position(),
                                local.distance(anchor.position()), isPrimary, random
                        ));
                        voxels.setLeafIfEmpty(x, y, z, state);
                    }
                }
            }
        }
    }

    /**
     * The skeleton's anchors with {@code primary} moved to the front.
     */
    private static List<Anchor> orderedAnchors(Skeleton skeleton, Anchor primary) {
        if (primary == null) return skeleton.anchors();
        final List<Anchor> ordered = new ArrayList<>(skeleton.anchors());
        // By reference, not equals: Anchor is a record, and two lobes that happen to share a position
        // and scale would otherwise make remove() drop the wrong one.
        ordered.removeIf(a -> a == primary);
        ordered.add(0, primary);
        return ordered;
    }

    /**
     * The anchor that dominates the tree's silhouette: the highest one, breaking ties on scale.
     * <p>
     * Deliberately a property of the finished skeleton rather than something the trunk shape
     * declares. A shape does not know which of its arms won the height roll, and a palette that
     * wants to mark the tree exactly once (a face, a crown) needs whichever one actually did.
     */
    private static Anchor primaryAnchor(Skeleton skeleton) {
        Anchor best = null;
        for (Anchor a : skeleton.anchors()) {
            if (best == null
                    || a.position().y > best.position().y
                    || (a.position().y == best.position().y && a.scale() > best.scale())) {
                best = a;
            }
        }
        return best;
    }

    /**
     * How far past its nominal radius a displaced surface may bulge. Kept as a constant rather than
     * derived from the displacement, because a {@link Volume.Displacement} is an opaque function with no
     * amplitude to ask for.
     */
    private static final float BARK_MARGIN = 2.0F;

    /**
     * A growable integer box over tree-local space.
     */
    private static final class Bounds {
        private boolean empty = true;
        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;

        void include(Vector3f point, float radius) {
            final int lowX = (int) Math.floor(point.x - radius);
            final int lowY = (int) Math.floor(point.y - radius);
            final int lowZ = (int) Math.floor(point.z - radius);
            final int highX = (int) Math.ceil(point.x + radius);
            final int highY = (int) Math.ceil(point.y + radius);
            final int highZ = (int) Math.ceil(point.z + radius);
            if (empty) {
                minX = lowX;
                minY = lowY;
                minZ = lowZ;
                maxX = highX;
                maxY = highY;
                maxZ = highZ;
                empty = false;
                return;
            }
            minX = Math.min(minX, lowX);
            minY = Math.min(minY, lowY);
            minZ = Math.min(minZ, lowZ);
            maxX = Math.max(maxX, highX);
            maxY = Math.max(maxY, highY);
            maxZ = Math.max(maxZ, highZ);
        }
    }

    public static final class Builder {
        private TrunkShape trunk;
        private CanopyShape canopy;
        private TreePalette palette;
        private Volume.Displacement bark;
        private DecayRepair repair = DecayRepair.CARVE_BRANCH;
        private int decayBudget = LeafDecay.VANILLA_BUDGET;
        private int maxCarves = 32;
        private boolean pruneLooseWood = true;
        private Predicate<BlockState> replaceable = state -> state.isAir() || state.canBeReplaced();
        private int writeFlags = Block.UPDATE_CLIENTS;

        public Builder trunk(TrunkShape shape) {
            this.trunk = shape;
            return this;
        }

        public Builder canopy(CanopyShape shape) {
            this.canopy = shape;
            return this;
        }

        public Builder palette(TreePalette palette) {
            this.palette = palette;
            return this;
        }

        /**
         * Perturbs the wood surface - the "crackly bark" pass. Keep the amplitude below half the
         * smallest trunk radius; see {@link org.betterx.betternether.world.tree.math.Crackle}.
         */
        public Builder bark(Volume.Displacement displacement) {
            this.bark = displacement;
            return this;
        }

        public Builder repair(DecayRepair repair) {
            this.repair = repair;
            return this;
        }

        /**
         * The largest leaf distance that survives. Leave at {@link LeafDecay#VANILLA_BUDGET} unless the
         * leaf block widens the vanilla {@code distance} property.
         */
        public Builder decayBudget(int budget) {
            this.decayBudget = budget;
            return this;
        }

        public Builder maxCarves(int carves) {
            this.maxCarves = carves;
            return this;
        }

        /**
         * Whether to delete wood that ends up not connected to the base. On by default: a heavy bark
         * displacement can bite a thin branch in two, and half a branch hanging in the air is precisely
         * the artefact this library refuses to ship.
         */
        public Builder pruneLooseWood(boolean prune) {
            this.pruneLooseWood = prune;
            return this;
        }

        /**
         * Which existing blocks the tree may overwrite.
         */
        public Builder replaceable(Predicate<BlockState> predicate) {
            this.replaceable = predicate;
            return this;
        }

        /**
         * Block-update flags for the commit. Defaults to {@link Block#UPDATE_CLIENTS} - no neighbour
         * updates, because the tree is already internally consistent and world generation does not want
         * to pay for the cascade.
         */
        public Builder writeFlags(int flags) {
            this.writeFlags = flags;
            return this;
        }

        public TreeBuilder build() {
            if (trunk == null) throw new IllegalStateException("a tree needs a trunk shape");
            if (canopy == null) throw new IllegalStateException("a tree needs a canopy shape");
            if (palette == null) throw new IllegalStateException("a tree needs a palette");
            return new TreeBuilder(this);
        }
    }
}
