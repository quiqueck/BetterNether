package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockRubeusCone;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockRubeusSapling;
import org.betterx.betternether.blocks.RubeusBark;
import org.betterx.betternether.blocks.RubeusLog;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.block.TripleShapePillarModelTrait;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.types.Bark;
import org.betterx.wover.sets.api.blocks.types.Log;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class RubeusMaterial extends NetherWoodenMaterial<RubeusMaterial> {
    public RubeusMaterial() {
        super("rubeus", MapColor.COLOR_MAGENTA, MapColor.COLOR_MAGENTA);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Sapling.create(BlockRubeusSapling::new, NetherSurvival.netherGround()))
                    .add(SimpleBlockSlot.withItem(NetherSlots.CONE, (set, props) -> new BlockRubeusCone(props)))
                    .replace(new NetherWoodSlots.Log(true) {
                        @Override
                        protected BlockDefinition<?, ?> startBlockDefinition(
                                @NotNull BlockRegistry registry,
                                @NotNull BlockSet<?> set,
                                @NotNull String name
                        ) {
                            return registry.defineDefaultBlockWithProps(name, RubeusLog::new);
                        }

                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            // shape=bottom/middle/top blend pillar: all three shape models are plain vanilla-template
                            // children (cube_column / cube_bottom_top), so the whole blockstate is generated.
                            return TripleShapePillarModelTrait.log(
                                    RubeusLog.SHAPE,
                                    BetterNether.C.mk("block/rubeus_log_side"),
                                    BetterNether.C.mk("block/rubeus_log_top"),
                                    BetterNether.C.mk("block/rubeus_log_side_blend"),
                                    BetterNether.C.mk("block/rubeus_stripped_log_top"),
                                    BetterNether.C.mk("block/rubeus_log_top"),
                                    BetterNether.C.mk("block/rubeus_stripped_log_side"),
                                    BetterNether.C.mk("block/rubeus_stripped_log_top")
                            );
                        }
                    })
                    .replace(new NetherWoodSlots.Bark(true) {
                        @Override
                        protected BlockDefinition<?, ?> startBlockDefinition(
                                @NotNull BlockRegistry registry,
                                @NotNull BlockSet<?> set,
                                @NotNull String name
                        ) {
                            return registry.defineDefaultBlockWithProps(name, RubeusBark::new);
                        }

                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return TripleShapePillarModelTrait.bark(
                                    RubeusLog.SHAPE,
                                    BetterNether.C.mk("block/rubeus_log_side"),
                                    BetterNether.C.mk("block/rubeus_log_side_blend"),
                                    BetterNether.C.mk("block/rubeus_stripped_log_side"),
                                    BetterNether.C.mk("block/rubeus_log_side"),
                                    BetterNether.C.mk("block/rubeus_stripped_log_side")
                            );
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
