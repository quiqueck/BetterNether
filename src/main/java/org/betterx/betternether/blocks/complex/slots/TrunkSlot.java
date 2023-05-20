package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrunkSlot extends MaterialSlot<WoodenComplexMaterial> {
    protected static final String TRUNK_SUFFIX = "trunk";
    private final BlockEntry trunkEntry;


    protected TrunkSlot(BiFunction<ComplexMaterial, BlockBehaviour.Properties, Block> maker, boolean climable) {
        super(TRUNK_SUFFIX);
        trunkEntry = new BlockEntry(suffix, false, maker);
        if (climable) {
            trunkEntry.setBlockTags(BlockTags.CLIMBABLE);
        }
    }


    @Override
    public void addBlockEntry(WoodenComplexMaterial parentMaterial, Consumer<BlockEntry> adder) {
        adder.accept(trunkEntry);
    }

    @Override
    public void addRecipeEntry(WoodenComplexMaterial parentMaterial, Consumer<RecipeEntry> adder) {

    }

    public static TrunkSlot create(BiFunction<ComplexMaterial, BlockBehaviour.Properties, Block> maker) {
        return new TrunkSlot(maker, false);
    }

    public static TrunkSlot create(Supplier<Block> maker) {
        return new TrunkSlot((s, p) -> maker.get(), false);
    }

    public static TrunkSlot createClimbable(Supplier<Block> maker) {
        return new TrunkSlot((s, p) -> maker.get(), true);
    }
}
