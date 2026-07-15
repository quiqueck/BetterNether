package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockStalagnate;
import org.betterx.betternether.blocks.BlockStalagnateBowl;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.blocks.complex.slots.Stem;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.types.Log;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class StalagnateMaterial extends RoofMaterial<StalagnateMaterial> {
    public StalagnateMaterial() {
        super("stalagnate", MapColor.TERRACOTTA_LIGHT_GREEN, MapColor.TERRACOTTA_LIGHT_GREEN);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Stem.SLOT)
                    .add(TrunkSlot.createClimbable(BlockStalagnate::new))
                    .add(AbstractSeed.create(BlockStalagnateSeed::new))
                    .add(SimpleBlockSlot.blockOnly(
                            NetherSlots.BOWL,
                            (set, props) -> new BlockStalagnateBowl(props)
                    ))
                    .replace(new Log(true) {
                        @Override
                        protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                                    .crafting(key.location(), block)
                                    .outputCount(1)
                                    .shape("##", "##")
                                    .addMaterial('#', set.recipeMaterial(NetherSlots.STEM))
                                    .group("logs")
                                    .category(RecipeCategory.BUILDING_BLOCKS)
                                    .build(context));
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
