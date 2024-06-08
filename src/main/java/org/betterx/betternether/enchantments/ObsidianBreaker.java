package org.betterx.betternether.enchantments;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherTags;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianBreaker {
    public static float modifyObsidianBreakerSpeed(BlockState blockState, float speed, LivingEntity entity) {
        if (blockState.is(NetherTags.OBSIDIAN_BREAKER_MINEABLE)) {
            speed *= (float) entity.getAttributeValue(NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED);
        }
        return speed;
    }
}
