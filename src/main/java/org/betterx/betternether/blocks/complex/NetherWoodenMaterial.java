package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * Base wooden material for BetterNether, on top of the wover-sets-api {@link WoodenBlockSet}.
 * <p>
 * Differs from the vanilla-wood default in two ways: Nether wood does not burn (no
 * {@link BlockTraits#FLAMMABLE} trait), and the set also carries a {@code wall} slot.
 */
public class NetherWoodenMaterial<T extends NetherWoodenMaterial<T>> extends WoodenBlockSet<T> {
    protected final MapColor plankColor;

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
     * Furniture (taburet/chair/bar-stool) slots were removed from wover-sets-api; this is a no-op kept so
     * the existing subclass constructors keep compiling. Remove the calls when convenient.
     */
    public T setFurnitureCloth(Block clothMaterial) {
        //noinspection unchecked
        return (T) this;
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // Deliberately NOT calling super: the base adds BlockTraits.FLAMMABLE, but nothing burns in the Nether.
        blockDefinition.addTrait(BlockTraits.WOOD_BLOCK.withDefault());
        if (slot == SlotType.PLANKS) {
            blockDefinition.mapColor(plankColor);
        } else {
            blockDefinition.mapColor(woodColor);
        }
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions().add(WoodSlots.WALL);
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
