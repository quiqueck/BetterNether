package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockReedsBlock;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.wover.sets.api.blocks.types.Planks;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherReedMaterial extends RoofMaterial<NetherReedMaterial> {
    public NetherReedMaterial() {
        super("nether_reed", MapColor.COLOR_CYAN, MapColor.COLOR_CYAN);
        this.setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .remove(WoodSlots.LOG)
                    .remove(WoodSlots.BARK)
                    .remove(WoodSlots.STRIPPED_LOG)
                    .remove(WoodSlots.STRIPPED_BARK)
                    // Reed "planks" are a pillar block crafted from 4 reed stems.
                    .replace(new Planks() {
                        @Override
                        protected BlockDefinition<?, ?> startBlockDefinition(
                                @NotNull BlockRegistry registry,
                                @NotNull BlockSet<?> set,
                                @NotNull String name
                        ) {
                            return registry.defineDefaultBlockWithProps(name, BlockReedsBlock::new);
                        }

                        @Override
                        protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup traitLookup) {
                            return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                                    .crafting(key.location(), block)
                                    .outputCount(1)
                                    .shape("##", "##")
                                    .addMaterial('#', getStem())
                                    .group("planks")
                                    .category(RecipeCategory.BUILDING_BLOCKS)
                                    .build(context));
                        }

                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildWoodModel(WoodenBlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.pillar();
                        }
                    });
    }

    @Override
    public @Nullable Block getBlock(@NotNull SlotType type) {
        if (SlotType.STRIPPED_LOG.equals(type)) {
            return getStem();
        }
        return super.getBlock(type);
    }

    public Block getStem() {
        return NetherBlocks.NETHER_REED_STEM;
    }
}
