package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleBlockOnlyMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.Log;
import org.betterx.betternether.blocks.BlockStalagnate;
import org.betterx.betternether.blocks.BlockStalagnateBowl;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.Nullable;

public class StalagnateMaterial extends RoofMaterial<StalagnateMaterial> {
    public StalagnateMaterial() {
        super("stalagnate", MapColor.TERRACOTTA_LIGHT_GREEN, MapColor.TERRACOTTA_LIGHT_GREEN);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
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
                        protected @Nullable void makeRecipe(
                                RecipeOutput context, ComplexMaterial material, ResourceLocation id
                        ) {
                            CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder
                                    .crafting(id, material.getBlock(suffix));
                            CraftingRecipeBuilder craftingRecipeBuilder2 = craftingRecipeBuilder1.outputCount(1);
                            CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder2.shape("##", "##")
                                                                                                .addMaterial('#', material.getBlock(NetherSlots.STEM));
                            BaseRecipeBuilder<CraftingRecipeBuilder> craftingRecipeBuilderBaseRecipeBuilder = craftingRecipeBuilder.group("logs");
                            craftingRecipeBuilderBaseRecipeBuilder.category(RecipeCategory.BUILDING_BLOCKS)
                                                                  .build(context);
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
