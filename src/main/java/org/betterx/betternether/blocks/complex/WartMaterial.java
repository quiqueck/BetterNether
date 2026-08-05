package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.betternether.blocks.BlockWartRoots;
import org.betterx.betternether.blocks.NetherLoot;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.betternether.blocks.BlockWartSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class WartMaterial extends RoofMaterial<WartMaterial> {
    public WartMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(name, woodColor, planksColor);
        setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    // wart's trapdoor is exactly nether_sakura's shared #side trapdoor mesh with only the
                    // #side texture swapped (wart_planks) - generate it from the shared template.
                    .replace(new org.betterx.betternether.blocks.complex.slots.NetherWoodSlots.TrapdoorSideTemplate(
                            org.betterx.betternether.BetterNether.C.mk("block/wart_planks")))
                    .add(AbstractSeed.create(BlockWartSeed::new, NetherSurvival.soulSand()))
                    // The roots' loot was the hand-authored loot_table/blocks/wart_roots.json (drops a
                    // wart log); block loot is trait-only here.
                    .add(SimpleBlockSlot.blockOnly(
                            NetherSlots.ROOTS,
                            (set, props) -> new BlockWartRoots(props),
                            TraitLists.of(NetherLoot.dropOther(this::getLog))
                    ));
    }

    public Block getRoot() {
        return getBlock(NetherSlots.ROOTS);
    }

    public Block getSeed() {
        return getBlock(NetherSlots.SEED);
    }
}
