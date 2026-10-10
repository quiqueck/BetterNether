package org.betterx.betternether.mixin.common;

import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.entity.animal.golem.CopperGolemAi;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

/**
 * Lets copper golems deliver items to modded wooden chests.
 * <p>
 * Vanilla hardcodes the golem's destination as {@code block.is(Blocks.CHEST) || block.is(Blocks.TRAPPED_CHEST)}
 * (see {@code CopperGolemAi.TRANSPORT_ITEM_DESTINATION_BLOCK}), so no tag can make our chests qualify. The
 * conventional {@code c:chests/wooden} tag contains exactly those two vanilla blocks plus every wooden chest
 * that mods declare, so widening the predicate to that tag keeps vanilla behaviour and adds ours (and any other
 * mod's) without listing blocks here. The golem only ever scans {@code ChestBlockEntity}s, so a non-chest that a
 * mod might put into the tag can never become a target.
 */
@Mixin(CopperGolemAi.class)
public class CopperGolemAiMixin {
    @ModifyArg(
            method = "initIdleActivity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;<init>(FLjava/util/function/Predicate;Ljava/util/function/Predicate;IILjava/util/Map;Ljava/util/function/Consumer;Ljava/util/function/Predicate;)V"
            ),
            index = 2
    )
    private static Predicate<BlockState> bn_acceptWoodenChests(Predicate<BlockState> destination) {
        return destination.or(state -> state.is(CommonBlockTags.WOODEN_CHEST));
    }
}
