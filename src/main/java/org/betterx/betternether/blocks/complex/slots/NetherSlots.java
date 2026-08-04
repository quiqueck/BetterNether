package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.sets.api.blocks.SlotType;

/**
 * BetterNether-specific {@link SlotType}s used by the wover-sets-api wooden material sets
 * (see {@link org.betterx.betternether.blocks.complex.NetherWoodenMaterial} and subclasses).
 * <p>
 * The standard log/bark/plank/... slots come from
 * {@link de.ambertation.wover.sets.api.blocks.slots.WoodSlots}; only the Nether-only extras live here.
 */
public class NetherSlots {
    public static final SlotType STEM = new SlotType("stem");
    public static final SlotType TRUNK = new SlotType("trunk");
    public static final SlotType ROOF = new SlotType("roof");
    public static final SlotType ROOF_STAIRS = new SlotType("roof_stairs");
    public static final SlotType ROOF_SLAB = new SlotType("roof_slab");
    public static final SlotType CONE = new SlotType("cone");
    public static final SlotType BOWL = new SlotType("bowl");
    public static final SlotType ROOTS = new SlotType("roots");
    public static final SlotType TORCH = new SlotType("torch");
    public static final SlotType BRANCH = new SlotType("branch");
    public static final SlotType SEED = new SlotType("seed");
    public static final SlotType SAPLING = new SlotType("sapling");
}
