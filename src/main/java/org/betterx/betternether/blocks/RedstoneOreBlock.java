package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherOreBlocks;

import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.ToIntFunction;

public class RedstoneOreBlock extends RedStoneOreBlock {
    private final int minCount;
    private final int maxCount;

    public RedstoneOreBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(settings);

        this.minCount = 1;
        this.maxCount = 3;
    }

    /**
     * The former Materials.stone(...).lightLevel(...) ctor argument, exposed so
     * NetherOreBlocks.NETHER_REDSTONE_ORE can chain it as a .lightLevel(...) setter at the registration
     * site instead (WP3.6).
     */
    public static ToIntFunction<BlockState> litBlockEmission(int i) {
        return (blockState) -> {
            return (Boolean) blockState.getValue(BlockStateProperties.LIT) ? i : 0;
        };
    }
}
