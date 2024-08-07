package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseSimpleVineBlock;

import net.minecraft.world.level.material.MapColor;

public class BlockGoldenVine extends BaseSimpleVineBlock {
    public BlockGoldenVine() {
        super(
                BehaviourBuilders
                        .createStaticVine(MapColor.COLOR_YELLOW)
                        .lightLevel((bs) -> 15)
                        .instabreak(),
                29,
                0
        );
    }
}
