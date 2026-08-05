package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.Stem;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.sets.api.blocks.types.Planks;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.betterx.betternether.blocks.NetherModels;
import de.ambertation.wover.sets.api.blocks.types.Button;
import de.ambertation.wover.sets.api.blocks.types.PressurePlate;

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
                    // The mushroom fence's post/side models are pure texture-swap children of nether_reed's
                    // bespoke fence templates - generate them (and the multipart/inventory) from those templates
                    // instead of hand-authoring. The nether_reed fence templates stay hand-authored.
                    .replace(new NetherWoodSlots.FenceTemplate(
                            org.betterx.betternether.BetterNether.C.mk("block/nether_mushroom_fence_side"),
                            org.betterx.betternether.BetterNether.C.mk("block/nether_mushroom_fence_top"),
                            org.betterx.betternether.BetterNether.C.mk("block/nether_mushroom_planks")))
                    // A stem without the default "4 stems -> log" recipe (this set has no log), whose
                    // blockstate/model/item model are hand-authored rather than the generic pillar.
                    .add(new Stem() {
                        @Override
                        protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return null;
                        }

                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.externalModel();
                        }
                    })
                    // Plate and button use dedicated textures rather than the planks'.
                    .replace(new PressurePlate() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return NetherModels.pressurePlate("block/nether_mushroom_plate");
                        }
                    })
                    .replace(new Button() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return NetherModels.button("block/nether_mushroom_button");
                        }
                    })
                    // Planks are crafted from 4 stems instead of a log.
                    .replace(new Planks() {
                        @Override
                        protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup traitLookup) {
                            return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                                    .crafting(key.identifier(), block)
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
