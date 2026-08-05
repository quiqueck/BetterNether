package org.betterx.betternether.world.tree.decay;

/**
 * What to do about leaves that are too far from any log for vanilla leaf decay to keep them alive.
 */
public enum DecayRepair {
    /**
     * Delete them.
     * <p>
     * Always correct and never surprising: the tree that appears is exactly the tree that will still be
     * standing a few random ticks later. The cost is that an over-ambitious canopy silently loses its
     * outer shell, so a shape tuned under this policy is a shape that was already decay-clean.
     */
    PRUNE,

    /**
     * Grow real branches until they are close enough, then prune whatever is still out of reach.
     * <p>
     * The branch is carved along the path the leaves themselves already form, starting from a cell
     * adjacent to existing wood, so it is connected to the trunk by construction. This is the difference
     * between this policy and the "hide a few logs inside the canopy" trick: there is no step at which a
     * log is placed anywhere that is not contiguous with the rest of the tree.
     */
    CARVE_BRANCH,

    /**
     * Mark them {@code persistent} so vanilla never checks them.
     * <p>
     * The escape hatch, not a solution: persistent leaves do not decay when the tree is cut down either,
     * which is exactly the behaviour players read as a bug. Present for shapes that genuinely have no
     * woody structure to hang off - free-floating foliage, vine curtains - and for parity when porting
     * an existing tree that already behaved this way.
     */
    PERSIST
}
