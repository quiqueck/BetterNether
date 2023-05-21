package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleBlockOnlyMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.Log;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BlockStalagnate;
import org.betterx.betternether.blocks.BlockStalagnateBowl;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.Nullable;

public class StalagnateMaterial extends RoofMaterial<StalagnateMaterial> {
    public StalagnateMaterial() {
        super("stalagnate", MapColor.TERRACOTTA_LIGHT_GREEN, MapColor.TERRACOTTA_LIGHT_GREEN);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(NetherSlots.STEM)
                    .add(TrunkSlot.createClimbable(BlockStalagnate::new))
                    .add(AbstractSeed.create(BlockStalagnateSeed::new))
                    .add(SimpleBlockOnlyMaterialSlot.createBlockOnly(
                            NetherSlots.BOWL,
                            (c, p) -> new BlockStalagnateBowl(c.getBlock(NetherSlots.TRUNK))
                    ))
                    .replace(new Log() {
                        @Override
                        protected @Nullable void makeRecipe(ComplexMaterial material, ResourceLocation id) {
                            BCLRecipeBuilder
                                    .crafting(id, material.getBlock(suffix))
                                    .setOutputCount(1)
                                    .setShape("##", "##")
                                    .addMaterial('#', material.getBlock(NetherSlots.STEM))
                                    .setGroup("logs")
                                    .setCategory(RecipeCategory.BUILDING_BLOCKS)
                                    .build();
                        }
                    });
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }

    public Block getStem() {
        return getBlock(NetherSlots.STEM);
    }

    public Block getBowl() {
        return getBlock(NetherSlots.BOWL);
    }

    public Block getSeed() {
        return getBlock(NetherSlots.SEED);
    }
}
