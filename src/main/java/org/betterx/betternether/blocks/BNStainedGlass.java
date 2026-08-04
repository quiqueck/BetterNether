package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseGlassBlock;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BNStainedGlass extends BaseGlassBlock {
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    // The template-block and resistance-carrying BaseGlassBlock constructors are gone (R1): properties are
    // configured by GlassBlockTrait at the definition site, so a block class only ever threads Properties
    // through untouched.
    public BNStainedGlass(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(COLOR);
    }
}