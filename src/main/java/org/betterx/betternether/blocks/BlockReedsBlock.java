package org.betterx.betternether.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

// Reparented off BNPillar.Wood (WP6.14 sweep): the nested Wood/Stone/Metal shims added nothing beyond
// BNPillar itself (pure passthrough constructors) and are dissolved once their last live use (this class,
// and NetherBlocks' CINCINNASITE_BRICKS_PILLAR registration) stops referencing them.
public class BlockReedsBlock extends BNPillar {
    // The no-arg overload (building a fresh, id-less BlockBehaviour.Properties.of() via
    // Materials.makeNetherWood(...)) was dead code - nothing in src called it, only the (Properties)
    // overload below is ever used at registration (see complex/NetherReedMaterial.java's
    // registry.defineDefaultBlockWithProps(name, BlockReedsBlock::new)). Removed rather than ported (WP3.8).
    public BlockReedsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
