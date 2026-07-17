package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase {
    public BlockCincinnasite(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        // Adopt the metal material default strength (5/6, set by Materials.metal) rather than the old
        // 3/10, so cincinnasite is a consistent METAL_BLOCK across its base cubes and every copy-based
        // decorative variant (stairs/slabs/walls/pillars/...), which the METAL_BLOCK trait drives to 5/6.
        super(Materials.metal(settings, MapColor.COLOR_YELLOW)
                               .requiresCorrectToolForDrops()
        );
    }
}
