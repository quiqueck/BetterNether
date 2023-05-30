package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSeed extends SimpleMaterialSlot<WoodenComplexMaterial> {
    protected static final String SEED_SUFFIX = "seed";

    protected AbstractSeed() {
        super(SEED_SUFFIX);
    }


    @Override
    protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {

    }

    public static AbstractSeed create(BiFunction<ComplexMaterial, BlockBehaviour.Properties, Block> maker) {
        return new AbstractSeed() {
            @Override
            protected @NotNull Block createBlock(
                    WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
            ) {
                return maker.apply(parentMaterial, settings);
            }
        };
    }

    public static AbstractSeed create(Supplier<Block> maker) {
        return new AbstractSeed() {
            @Override
            protected @NotNull Block createBlock(
                    WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
            ) {
                return maker.get();
            }
        };
    }
}
