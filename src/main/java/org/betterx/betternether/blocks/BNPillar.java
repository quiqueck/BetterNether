package org.betterx.betternether.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

// The nested Wood/Stone/Metal shims (WP6.14 sweep) are gone: they added nothing beyond BNPillar itself
// (pure passthrough constructors, needed only because this class used to be abstract). Every direct
// BNPillar.Wood/.Stone/.Metal registration/subclass site is now plain BNPillar (material is carried by the
// registration's traits, not the class).
public class BNPillar extends RotatedPillarBlock {
    public BNPillar(Properties settings) {
        super(settings);
    }

    public BNPillar(Block block) {
        super(BlockBehaviour.Properties.ofFullCopy(block));
    }

    // The (MapColor)-only constructors below (this class + the three former nested subclasses) built a
    // fresh, id-less BlockBehaviour.Properties.of() via Materials.makeNetherWood(color) - dead code, since
    // nothing in src ever called `new BNPillar(MapColor...)`/`new BNPillar.Wood(MapColor...)`/etc.; every
    // live registration site uses the (Properties) constructor. Removed rather than ported (WP3.8).
}
