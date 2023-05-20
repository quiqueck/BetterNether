package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.AbstractSaplingSlot;
import org.betterx.bclib.complexmaterials.set.wood.Bark;
import org.betterx.bclib.complexmaterials.set.wood.Log;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockRubeusCone;
import org.betterx.betternether.blocks.BlockRubeusSapling;
import org.betterx.betternether.blocks.RubeusBark;
import org.betterx.betternether.blocks.RubeusLog;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RubeusMaterial extends NetherWoodenMaterial<RubeusMaterial> {
    public RubeusMaterial() {
        super("rubeus", MaterialColor.COLOR_MAGENTA, MaterialColor.COLOR_MAGENTA);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(AbstractSaplingSlot.create(BlockRubeusSapling::new))
                    .add(new SimpleMaterialSlot<>(NetherSlots.CONE) {
                        @Override
                        protected @NotNull Block createBlock(
                                WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
                        ) {
                            return new BlockRubeusCone();
                        }

                        @Override
                        protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {

                        }
                    })
                    .replace(new Log() {
                        @Override
                        protected @NotNull Block createBlock(
                                WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
                        ) {
                            return new RubeusLog(woodColor, getStrippedLog());
                        }
                    })
                    .replace(new Bark() {
                        @Override
                        protected @NotNull Block createBlock(
                                WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
                        ) {
                            return new RubeusBark(woodColor, getStrippedBark());
                        }
                    });
    }


    public Block getCone() {
        return getBlock(NetherSlots.CONE);
    }

    public Block getSapling() {
        return getBlock(WoodSlots.SAPLING);
    }
}
