package org.betterx.betternether.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;

/**
 * Rubeus leaves decay like vanilla leaves (#97), so - like the gloomwood leaves and unlike the rest of
 * the {@link BNLeaves} family, which switch decay off entirely - this is a plain vanilla leaves block.
 * That is only safe because {@code RubeusTreeFeature} writes every leaf's true path distance to the wood
 * and prunes the ones out of reach, so a freshly generated crown has nothing for vanilla to correct.
 * Leaves placed by a player, and every crown generated before this change, are {@code persistent} and
 * unaffected.
 */
public class BlockRubeusLeaves extends TintedParticleLeavesBlock {
    public BlockRubeusLeaves(Block sapling, Properties settings) {
        // sapling no longer tracked by the vanilla leaves block in 1.21.6
        super(0.01F, settings);
    }
}
