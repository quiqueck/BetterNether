package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.data_components.DataComponentManager;
import org.betterx.wover.enchantment.api.EnchantmentKey;
import org.betterx.wover.enchantment.api.EnchantmentManager;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import org.jetbrains.annotations.ApiStatus;

public class NetherEnchantments {
    public static EnchantmentKey OBSIDIAN_BREAKER = EnchantmentManager.createKey(BetterNether.C.id("obsidian_breaker"));
    ;
    public static EnchantmentKey RUBY_FIRE = EnchantmentManager.createKey(BetterNether.C.id("ruby_fire"));

    public static final Holder<Attribute> OBSIDIAN_BLOCK_BREAK_SPEED = DataComponentManager.registerAttribute(
            BetterNether.C.mk("player.obsidian_block_break_speed"),
            new RangedAttribute("attribute.name.player.bn_obsidian_block_break_speed", 1.0, 1.0f, 100.0f).setSyncable(true)
    );

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        //NO-OP
    }
}
