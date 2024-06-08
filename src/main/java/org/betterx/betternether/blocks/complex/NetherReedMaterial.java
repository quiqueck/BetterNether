package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.blocks.BaseBookshelfBlock;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.client.models.PatternsHelper;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.Bookshelf;
import org.betterx.bclib.complexmaterials.set.wood.Planks;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.items.boat.BoatTypeOverride;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BlockReedsBlock;
import org.betterx.betternether.client.block.Patterns;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReedBookshelfBlock extends BaseBookshelfBlock.Wood {

    public ReedBookshelfBlock(Block source) {
        super(source);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable BlockModel getBlockModel(ResourceLocation blockId, BlockState blockState) {
        Optional<String> pattern = PatternsHelper.createJson(Patterns.REED_BLOCK_BOOKSHELF, replacePath(blockId));
        return ModelsHelper.fromPattern(pattern);
    }
}

public class NetherReedMaterial extends RoofMaterial<NetherReedMaterial> {
    public NetherReedMaterial() {
        super("nether_reed", MapColor.COLOR_CYAN, MapColor.COLOR_CYAN);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .remove(WoodSlots.LOG)
                    .remove(WoodSlots.BARK)
                    .remove(WoodSlots.STRIPPED_LOG)
                    .remove(WoodSlots.STRIPPED_BARK)

                    .replace(new Planks() {
                        @Override
                        protected @NotNull Block createBlock(
                                WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
                        ) {
                            return new BlockReedsBlock();
                        }

                        @Override
                        protected @Nullable void makeRecipe(ComplexMaterial parentMaterial, ResourceLocation id) {
                            BCLRecipeBuilder.crafting(id, parentMaterial.getBlock(WoodSlots.PLANKS))
                                            .setOutputCount(1)
                                            .setShape("##", "##")
                                            .addMaterial('#', getStem())
                                            .setGroup("planks")
                                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                                            .build();
                        }
                    })
                    .replace(new Bookshelf() {
                        @Override
                        protected @NotNull Block createBlock(
                                WoodenComplexMaterial parentMaterial, BlockBehaviour.Properties settings
                        ) {
                            return new ReedBookshelfBlock(parentMaterial.getBlock(WoodSlots.PLANKS));
                        }
                    });
    }

    @Override
    public @Nullable <M extends ComplexMaterial> Block getBlock(MaterialSlot<M> key) {
        if (key.suffix.equals(WoodSlots.STRIPPED_LOG.suffix))
            return getStem();
        return super.getBlock(key);
    }

    public Block getStem() {
        return NetherBlocks.NETHER_REED_STEM;
    }

    @Override
    public BoatTypeOverride supplyBoatType() {
        return BoatTypeOverride.create(
                C,
                getBaseName(),
                getBlock(WoodSlots.PLANKS),
                true
        );
    }
}
