package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.furniture.slots.BarStool;
import org.betterx.bclib.furniture.slots.Chair;
import org.betterx.bclib.furniture.slots.Taburet;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

/**
 * Base wooden material for BetterNether, on top of the wover-sets-api {@link WoodenBlockSet}.
 * <p>
 * Differs from the vanilla-wood default in three ways: Nether wood does not burn (no
 * {@link BlockTraits#FLAMMABLE} trait), and the set also carries a {@code wall} slot as well as the wooden
 * furniture slots (see {@link #addFurniture}).
 */
public class NetherWoodenMaterial<T extends NetherWoodenMaterial<T>> extends WoodenBlockSet<T> {
    protected final MapColor plankColor;
    protected Block furnitureCloth = Blocks.RED_WOOL;

    public NetherWoodenMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(BetterNether.C, name, woodColor);
        setPlanksColor(planksColor);
        this.plankColor = planksColor;
    }

    /**
     * Builds and registers every slot in this set. Kept named {@code init()} for parity with the
     * previous complex-material API so the {@code NetherBlocks} call sites read the same.
     */
    public T init() {
        return this.buildAndRegister();
    }

    /**
     * Sets the block whose texture the upholstery of this set's furniture (chair/bar-stool) uses. Must be
     * called before {@link #init()}, since that is when the slots are built.
     */
    public T setFurnitureCloth(Block clothMaterial) {
        this.furnitureCloth = clothMaterial;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Adds the three wooden furniture slots (taburet/chair/bar-stool) to {@code slots}. The slot classes live in
     * bclib (BetterEnd shares them); wover-sets-api has no furniture of its own. Each is built from this set's
     * slab and textured with its planks, matching what {@code WoodSlots.TABURET}/{@code CHAIR}/{@code BAR_STOOL}
     * used to do before the wover migration.
     *
     * @param slots the map to add to
     * @return {@code slots}, for chaining
     */
    protected SlotMap addFurniture(SlotMap slots) {
        return slots
                .add(new Taburet())
                // read the cloth lazily: subclasses may set it after this map is built
                .add(new Chair(() -> furnitureCloth))
                .add(new BarStool(() -> furnitureCloth));
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // Deliberately NOT calling super: the base adds BlockTraits.FLAMMABLE, but nothing burns in the Nether.
        blockDefinition.addTrait(BlockTraits.WOOD_BLOCK.withDefault());
        if (slot == SlotType.PLANKS || isFurniture(slot)) {
            // The furniture is made of (and pre-migration copied its properties from) the planks/slab, so it
            // takes the plank color rather than the log's.
            blockDefinition.mapColor(plankColor);
        } else {
            blockDefinition.mapColor(woodColor);
        }
    }

    private static boolean isFurniture(SlotType slot) {
        return slot == SlotType.TABURET || slot == SlotType.CHAIR || slot == SlotType.BAR_STOOL;
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        // The replaced slots keep their default block/recipe/tags and only opt out of model generation:
        // BetterNether hand-authors their blockstates and models (see NetherWoodSlots).
        return addFurniture(super.createDefaultDefinitions()
                                 .add(WoodSlots.WALL)
                                 .replace(new NetherWoodSlots.Ladder())
                                 .replace(new NetherWoodSlots.Trapdoor())
                                 .replace(new NetherWoodSlots.Gate())
                                 .replace(new NetherWoodSlots.Fence())
                                 .replace(new NetherWoodSlots.Slab())
                                 .replace(new NetherWoodSlots.Log(true))
                                 .replace(new NetherWoodSlots.Log(false))
                                 .replace(new NetherWoodSlots.Bark(true))
                                 .replace(new NetherWoodSlots.Bark(false)));
    }

    public Block getPlanks() {
        return getBlock(SlotType.PLANKS);
    }

    public Block getSlab() {
        return getBlock(SlotType.SLAB);
    }

    public Block getLog() {
        return getBlock(SlotType.LOG);
    }

    public Block getBark() {
        return getBlock(SlotType.BARK);
    }

    public Block getStrippedLog() {
        return getBlock(SlotType.STRIPPED_LOG);
    }

    public Block getStrippedBark() {
        return getBlock(SlotType.STRIPPED_BARK);
    }
}
