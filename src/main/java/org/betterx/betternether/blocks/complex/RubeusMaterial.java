package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockRubeusCone;
import org.betterx.betternether.blocks.BlockRubeusSapling;
import org.betterx.betternether.blocks.RubeusBark;
import org.betterx.betternether.blocks.RubeusLog;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.types.Bark;
import org.betterx.wover.sets.api.blocks.types.Log;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;

public class RubeusMaterial extends NetherWoodenMaterial<RubeusMaterial> {
    public RubeusMaterial() {
        super("rubeus", MapColor.COLOR_MAGENTA, MapColor.COLOR_MAGENTA);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Sapling.create(BlockRubeusSapling::new))
                    .add(SimpleBlockSlot.withItem(NetherSlots.CONE, (set, props) -> new BlockRubeusCone(props)))
                    .replace(new Log(true) {
                        @Override
                        protected BlockDefinition<?, ?> startBlockDefinition(
                                @NotNull BlockRegistry registry,
                                @NotNull BlockSet<?> set,
                                @NotNull String name
                        ) {
                            return registry.defineDefaultBlockWithProps(name, RubeusLog::new);
                        }
                    })
                    .replace(new Bark(true) {
                        @Override
                        protected BlockDefinition<?, ?> startBlockDefinition(
                                @NotNull BlockRegistry registry,
                                @NotNull BlockSet<?> set,
                                @NotNull String name
                        ) {
                            return registry.defineDefaultBlockWithProps(name, RubeusBark::new);
                        }
                    });
    }

    public Block getCone() {
        return getBlock(NetherSlots.CONE);
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }
}
