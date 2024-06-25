package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.entry.MaterialSlot;

public class NetherSlots {
    public static final MaterialSlot STRIPPED_LOG = new NetherStrippedLog();
    public static final MaterialSlot STRIPPED_BARK = new NetherStrippedBark();
    public static final MaterialSlot LOG = new NetherLog();
    public static final MaterialSlot BARK = new NetherBark();
    public static final MaterialSlot STEM = new Stem();
    public static final MaterialSlot ROOF = new Roof();
    public static final MaterialSlot ROOF_STAIRS = new RoofStairs();
    public static final MaterialSlot ROOF_SLAB = new RoofSlab();

    public static final String TRUNK = TrunkSlot.TRUNK_SUFFIX;
    public final static String CONE = "cone";
    public final static String BOWL = "bowl";
    public final static String ROOTS = "roots";
    public final static String TORCH = "torch";
    public final static String BRANCH = "branch";
    public final static String SEED = AbstractSeed.SEED_SUFFIX;
}
