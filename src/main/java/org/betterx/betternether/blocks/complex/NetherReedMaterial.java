package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockReedsBlock;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
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
import org.betterx.betternether.blocks.NetherModels;
import org.betterx.wover.sets.api.blocks.types.Stairs;

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
                    // nether_reed's own ladder model IS the shared ladder template (NetherWoodSlots.LADDER_TEMPLATE),
                    // so it stays hand-authored - it cannot be generated as a child of itself.
                    .replace(new NetherWoodSlots.LadderExternal())
                    // nether_reed's trapdoor is exactly stalagnate's shared (no-side) trapdoor mesh -
                    // generate its child model/blockstate/item from the template.
                    .replace(new NetherWoodSlots.TrapdoorTemplate())
                    // The reed slab reuses the reed planks' dedicated top texture (nether_reed_planks_top)
                    // on its top/bottom faces, and the reed fence is a bespoke bar shape - neither matches
                    // the vanilla plank slab/fence generator, so keep their hand-authored assets.
                    .replace(new NetherWoodSlots.Slab())
                    .replace(new NetherWoodSlots.Fence())
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

                        // The hand-authored blockstate/model stay: the generic planks model is an axis-less
                        // cube_all, whose blockstate has no variant for axis=x/z at all. Only the item model
                        // is wired, to the same block model the hand-authored blockstate uses.
                        // NOTE: Planks overrides buildModel() itself, so it never routes through
                        // WoodenSlotFromDefinition's buildWoodModel() hook - this has to override buildModel.
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.externalModelDelegatedItem();
                        }
                    })
                    // Reed stairs need the axis-aware plank textures, not the slot's single-texture default.
                    .replace(new Stairs() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return NetherModels.reedStairs();
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
