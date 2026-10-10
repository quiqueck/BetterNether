package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.trait.block.DescriptionBlockTrait;
import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockNetherSakuraSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.TraitLists;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class NetherSakuraMaterial extends NetherWoodenMaterial<NetherSakuraMaterial> {
    public NetherSakuraMaterial() {
        super("nether_sakura", MapColor.COLOR_PINK, MapColor.COLOR_BROWN);
        setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Sapling.create(
                            BlockNetherSakuraSapling::new,
                            TraitLists.and(
                                    NetherSurvival.netherrack(),
                                    PottablePlantBlockTrait.withSoils(CommonBlockTags.NETHERRACK),
                                    // It grows downwards, and NetherSakuraFeature declines unless the air
                                    // column below holds trunk + crown - 10, i.e. 15 to 29 blocks.
                                    DescriptionBlockTrait.of(
                                            "tooltip.betternether.sapling.hanging",
                                            "tooltip.betternether.nether_sakura_sapling.space"
                                    )
                            )
                    ));
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
