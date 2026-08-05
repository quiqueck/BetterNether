package org.betterx.betternether.blocks;

import de.ambertation.wover.enchantment.api.EnchantmentUtils;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

/**
 * A gloomsculk geode: a sculk shell holding lava behind a frosted cap.
 * <p>
 * Break it carelessly and the lava is let out. Silk touch takes the whole geode intact instead, which is
 * the {@code silkTouchSelf} loot table on the registration; this class only handles what is left behind.
 */
public class BlockGloomsculkGeode extends Block {
    /** One random tick in this many buds a crystal, if there is a free face to grow it on. */
    private static final int BUD_CHANCE = 6;

    public BlockGloomsculkGeode(Properties settings) {
        super(settings);
    }

    /**
     * Grows a crystal on a free face, the way budding amethyst does.
     * <p>
     * This is what makes a silk-touched geode worth carrying home rather than a souvenir: it is a farm.
     * Faces are tried in a random order and the first empty one wins, so a geode in the open fills in
     * gradually instead of always budding on the same side.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(BUD_CHANCE) != 0) return;

        final Direction[] faces = Direction.values().clone();
        for (int i = faces.length - 1; i > 0; i--) {
            final int j = random.nextInt(i + 1);
            final Direction t = faces[i];
            faces[i] = faces[j];
            faces[j] = t;
        }
        for (Direction face : faces) {
            final BlockPos target = pos.relative(face);
            if (!level.getBlockState(target).isAir()) continue;
            final BlockState bud = NetherTerrainBlocks.GLOOMSCULK_GEODE_CRYSTAL
                    .defaultBlockState()
                    .setValue(BlockGloomsculkCrystal.FACING, face);
            if (!bud.canSurvive(level, target)) continue;
            level.setBlockAndUpdate(target, bud);
            return;
        }
    }

    /**
     * Places the lava after the break rather than before it.
     * <p>
     * {@code playerWillDestroy} runs first and would be the obvious hook, but anything written there is
     * immediately overwritten by the removal that follows. This runs once the block is already gone and
     * its drops have been handled, so the position is free.
     */
    @Override
    public void playerDestroy(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            BlockEntity blockEntity,
            ItemStack tool
    ) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (level.isClientSide()) return;
        if (EnchantmentUtils.getItemEnchantmentLevel(level, Enchantments.SILK_TOUCH, tool) > 0) return;
        // only fill air: a geode broken under water or inside another block should not blow a hole in it
        if (!level.getBlockState(pos).isAir()) return;
        level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
    }
}
