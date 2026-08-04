package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.betternether.blocks.BlockWillowBranch;
import org.betterx.betternether.blocks.NetherLoot;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.NetherTraits;
import org.betterx.betternether.blocks.BlockWillowSapling;
import org.betterx.betternether.blocks.BlockWillowTorch;
import org.betterx.betternether.blocks.BlockWillowTrunk;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.WeightedBark;
import org.betterx.bclib.trait.block.WeightedLog;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import de.ambertation.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class WillowMaterial extends RoofMaterial<WillowMaterial> {
    public WillowMaterial() {
        super("willow", MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_RED);
        setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    // willow's trapdoor is exactly nether_sakura's shared #side trapdoor mesh with only the
                    // #side texture swapped (willow_planks) - generate it from the shared template.
                    .replace(new org.betterx.betternether.blocks.complex.slots.NetherWoodSlots.TrapdoorSideTemplate(
                            BetterNether.C.mk("block/willow_planks")))
                    // Restore the randomized-log look: generate the weighted willow_log/willow_bark blockstates
                    // (base + _2) instead of hand-authoring them. willow's log uses the willow_bark texture on its
                    // sides (there is no willow_log_side), so the base model textures are supplied explicitly.
                    .replace(new WeightedLog(
                            true, new int[]{1, 1},
                            BetterNether.C.mk("block/willow_bark"),
                            BetterNether.C.mk("block/willow_log_top")
                    ))
                    .replace(new WeightedBark(
                            true, new int[]{1, 1},
                            BetterNether.C.mk("block/willow_bark"),
                            null
                    ))
                    .add(TrunkSlot.create(BlockWillowTrunk::new)
                                  .withModelTrait(WillowMaterial::willowTrunkModelTrait))
                    .add(Sapling.create(BlockWillowSapling::new, NetherSurvival.netherGround()))
                    .add(SimpleBlockSlot.blockOnly(
                            NetherSlots.BRANCH,
                            (set, props) -> new BlockWillowBranch(props),
                            TraitLists.and(NetherRender.cutout(), NetherLoot.willowBranch())
                    ))
                    // BlockWillowTorch (WP6.12): always dropped itself unconditionally via BlockBase's
                    // inherited getDrops() override (no loot table json was generated for it), reproduced
                    // explicitly as NetherLoot.dropSelfNoExplosion().
                    .add(SimpleBlockSlot.withItem(
                            NetherSlots.TORCH,
                            (set, props) -> new BlockWillowTorch(props),
                            // Replaces BlockWillowTorch's BehaviourCompostable marker, which only tagged the
                            // item and never registered a composter entry.
                            TraitLists.and(NetherTraits.compostable(NetherRender.cutout()), NetherLoot.dropSelfNoExplosion())
                    ));
    }

    /**
     * The willow-trunk blockstate is dispatched over its {@code shape} (bottom/middle/top) property. The {@code
     * middle} and {@code top} states each hold a two-entry weighted list whose {@code _2} variant is the same mesh
     * with the {@code willow_bark} texture+particle swapped to {@code willow_bark_mossy}; those two children are
     * generated. The hand-authored {@code willow_trunk_bottom}/{@code _middle}/{@code _top} meshes stay the kept
     * templates and are referenced directly. Trunk has no item ({@code noBlockItem}).
     */
    @org.jetbrains.annotations.Nullable
    private static de.ambertation.wover.block.api.client.trait.BlockModelTrait willowTrunkModelTrait() {
        final var mossy = BetterNether.C.mk("block/willow_bark_mossy");
        final var swap = java.util.Map.of("particle", mossy, "texture", mossy);
        final var bottom = BetterNether.C.mk("block/willow_trunk_bottom");
        final var middle = BetterNether.C.mk("block/willow_trunk_middle");
        final var top = BetterNether.C.mk("block/willow_trunk_top");
        return WeightedTemplateModelTrait.propertyDispatch(
                BlockWillowTrunk.SHAPE,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(
                                de.ambertation.wover.block.api.BlockProperties.TripleShape.BOTTOM,
                                java.util.List.of(WeightedTemplateModelTrait.model(bottom))),
                        WeightedTemplateModelTrait.Case.of(
                                de.ambertation.wover.block.api.BlockProperties.TripleShape.MIDDLE,
                                java.util.List.of(
                                        WeightedTemplateModelTrait.model(middle),
                                        WeightedTemplateModelTrait.child(middle, swap))),
                        WeightedTemplateModelTrait.Case.of(
                                de.ambertation.wover.block.api.BlockProperties.TripleShape.TOP,
                                java.util.List.of(
                                        WeightedTemplateModelTrait.model(top),
                                        WeightedTemplateModelTrait.child(top, swap)))
                ),
                WeightedTemplateModelTrait.Item.none());
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }

    public Block getBranch() {
        return getBlock(NetherSlots.BRANCH);
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public Block getTorch() {
        return getBlock(NetherSlots.TORCH);
    }
}
