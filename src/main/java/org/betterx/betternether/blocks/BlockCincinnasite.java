package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase {
    public BlockCincinnasite(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        // Cincinnasite is a distinct material: weaker to mine than default metal (destroyTime 3 < 5) but
        // more blast-resistant (10 > 6). METAL_BLOCK classifies it (tags/sound/instrument/pickaxe), and this
        // ctor strength wins over the trait (the block factory runs last in build()); copy-based decorative
        // variants that DON'T reach this ctor take the same 3/10 from NetherMaterial.cincinnasite().
        super(Materials.metal(settings, MapColor.COLOR_YELLOW)
                               .requiresCorrectToolForDrops()
                               .strength(3.0F, 10.0F)
        );
    }
}
