package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.behaviours.interfaces.BehaviourClimableVine;
import org.betterx.bclib.blocks.BaseSimpleVineBlock;

import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.material.MapColor;


public class BlockBlackVine extends BaseSimpleVineBlock implements BonemealableBlock, BehaviourClimableVine {
    public BlockBlackVine(Properties settings) {
        super(Materials.staticVine(settings, MapColor.COLOR_BLACK).instabreak(), 27, 1);
    }
}
