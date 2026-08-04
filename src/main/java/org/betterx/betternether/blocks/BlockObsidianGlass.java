package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherObsidianBlocks;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockBehaviour;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone; noOcclusion()/isSuffocating()/isViewBlocking() move to the registration sites
// (NetherObsidianBlocks.OBSIDIAN_GLASS, BLUE_OBSIDIAN_GLASS). Both always dropped themselves unconditionally via
// BlockBase's inherited getDrops() override (no loot table json was generated for either), reproduced
// explicitly as NetherLoot.dropSelfNoExplosion().
public class BlockObsidianGlass extends Block {
    public BlockObsidianGlass(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState neighbor, Direction facing) {
        return neighbor.getBlock() == this || super.skipRendering(state, neighbor, facing);
    }
}