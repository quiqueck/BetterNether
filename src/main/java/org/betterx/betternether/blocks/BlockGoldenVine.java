package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseSimpleVineBlock;

import net.minecraft.world.level.material.MapColor;

public class BlockGoldenVine extends BaseSimpleVineBlock {
    public BlockGoldenVine(Properties settings) {
        super(
                org.betterx.bclib.behaviours.BehaviourBuilders
                        .createStaticVine(settings, MapColor.COLOR_YELLOW)
                        .lightLevel((bs) -> 15)
                        .instabreak(),
                29,
                0
        );
    }
}
