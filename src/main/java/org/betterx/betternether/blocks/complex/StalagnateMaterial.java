package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.betternether.blocks.BlockStalagnate;
import org.betterx.betternether.blocks.BlockStalagnateBowl;
import org.betterx.betternether.blocks.NetherLoot;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.blocks.complex.slots.Stem;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.types.Log;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.betterx.betternether.BetterNether;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class StalagnateMaterial extends RoofMaterial<StalagnateMaterial> {
    public StalagnateMaterial() {
        super("stalagnate", MapColor.TERRACOTTA_LIGHT_GREEN, MapColor.TERRACOTTA_LIGHT_GREEN);
        setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    // The stem delegates its item model to the first of its randomised stem variants;
                    // its blockstate and block models are hand-authored.
                    .add(new Stem() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.externalModelDelegatedItem(
                                    () -> BetterNether.C.mk("block/stalagnate_stem_1")
                            );
                        }
                    })
                    // The trunk's loot was the hand-authored loot_table/blocks/stalagnate_trunk.json (a
                    // stem plus a 25%-chance seed); block loot is trait-only here.
                    .add(TrunkSlot.createClimbable(
                            BlockStalagnate::new,
                            TraitLists.and(NetherRender.cutout(), NetherLoot.stalagnateTrunk())
                    ))
                    .add(AbstractSeed.create(
                            BlockStalagnateSeed::new,
                            NetherSurvival.netherrack(),
                            () -> WeightedCrossModelTrait.booleanDispatch(
                                    BlockStalagnateSeed.TOP,
                                    java.util.List.of(WeightedCrossModelTrait.cross(
                                            BetterNether.C.mk("block/stalagnate_seed"))),
                                    java.util.List.of(WeightedCrossModelTrait.cross(
                                            BetterNether.C.mk("block/stalagnate_seed")).rotated(180, 0)),
                                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/stalagnate_seed"))
                            )
                    ))
                    // BlockStalagnateBowl (WP6.12): noOcclusion() moved out of its constructor (R1) to this
                    // slot's extraProperties - there is no BlockDefinition at this call site to chain a
                    // setter onto directly (SimpleBlockSlot builds it later), so this is the visible
                    // equivalent of the former NetherProps.noOcclusion() trait.
                    .add(SimpleBlockSlot.blockOnly(
                            NetherSlots.BOWL,
                            (set, props) -> new BlockStalagnateBowl(props),
                            NetherRender.cutout(),
                            def -> def.noOcclusion().addTrait(NetherLoot.stalagnateBowl())
                    ))
                    .replace(new NetherWoodSlots.Log(true) {
                        @Override
                        protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                                    .crafting(key.identifier(), block)
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
