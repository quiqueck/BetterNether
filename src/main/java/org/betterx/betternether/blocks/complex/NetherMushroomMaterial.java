package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.Stem;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.betterx.betternether.blocks.NetherModels;
import org.betterx.wover.sets.api.blocks.types.Button;
import org.betterx.wover.sets.api.blocks.types.PressurePlate;

public class NetherMushroomMaterial extends NetherWoodenMaterial<NetherMushroomMaterial> {
    public NetherMushroomMaterial() {
        super("nether_mushroom", MapColor.TERRACOTTA_WHITE, MapColor.COLOR_LIGHT_GRAY);
        setFurnitureCloth(Blocks.RED_WOOL);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .remove(WoodSlots.LOG)
                    .remove(WoodSlots.BARK)
                    .remove(WoodSlots.STRIPPED_LOG)
                    .remove(WoodSlots.STRIPPED_BARK)
                    // The mushroom fence is a bespoke shape (custom top/lower bars on a dedicated
                    // fence texture), not the vanilla plank fence - keep its hand-authored assets.
                    .replace(new NetherWoodSlots.Fence())
                    // A stem without the default "4 stems -> log" recipe (this set has no log), whose
                    // blockstate/model/item model are hand-authored rather than the generic pillar.
                    .add(new Stem() {
                        @Override
                        protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return null;
                        }

                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.externalModel();
                        }
                    })
                    // Plate and button use dedicated textures rather than the planks'.
                    .replace(new PressurePlate() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return NetherModels.pressurePlate("block/nether_mushroom_plate");
                        }
                    })
                    .replace(new Button() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return NetherModels.button("block/nether_mushroom_button");
                        }
                    })
                    // Planks are crafted from 4 stems instead of a log.
                    .replace(new Planks() {
                        @Override
                        protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup traitLookup) {
                            return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                                    .crafting(key.location(), block)
                                    .outputCount(4)
                                    .shapeless()
                                    .addMaterial('#', set.recipeMaterial(NetherSlots.STEM))
                                    .group("planks")
                                    .category(RecipeCategory.BUILDING_BLOCKS)
                                    .build(context));
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
        return getBlock(NetherSlots.STEM);
    }
}
