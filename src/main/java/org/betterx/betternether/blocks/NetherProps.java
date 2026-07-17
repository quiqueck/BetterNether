package org.betterx.betternether.blocks;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Consumer;

/**
 * A single block property expressed as a {@link BlockTrait}, so it can be chained AFTER a material trait
 * through the {@code List<BlockTrait>} the {@code NetherBlocks.register*} helpers accept.
 * <p>
 * Property setters apply in call order (WorldWeaver 5dcd992) and {@link NetherTraits#and} appends these
 * after the material trait, so the override wins over the material default (e.g. a block that should keep
 * its own strength while still being classified as {@code STONE_BLOCK}). {@code keepLatestOnly()} is left
 * at its {@code false} default, so an override coexists with (and runs after) the material trait rather
 * than replacing it.
 */
public final class NetherProps {
    // Shared, interned key. keepLatestOnly() is false, so multiple instances all run in call order.
    private static final BlockTraitKey KEY = BlockTraitKey.of(BetterNether.C, "property_override");

    private NetherProps() {
    }

    private static GenericBlockTrait of(Consumer<BlockDefinition<Block, ? extends BlockDefinition<Block, ?>>> setter) {
        return new BlockTraitImpl.Generic() {
            @Override
            public BlockTraitKey key() {
                return KEY;
            }

            @Override
            public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
                setter.accept(definition);
            }
        };
    }

    /** Chains {@code strength(destroyTime, explosionResistance)} after any preceding material trait. */
    public static BlockTrait<?, ?> strength(float destroyTime, float explosionResistance) {
        return of(def -> def.strength(destroyTime, explosionResistance));
    }

    /** Chains {@code sound(soundType)} after any preceding material trait. */
    public static BlockTrait<?, ?> sound(SoundType soundType) {
        return of(def -> def.sound(soundType));
    }
}
