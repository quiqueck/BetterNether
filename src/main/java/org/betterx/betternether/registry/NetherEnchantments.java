package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.enchantments.ObsidianBreaker;
import org.betterx.betternether.enchantments.RubyFire;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class NetherEnchantments {
    public static ResourceKey<Enchantment> OBSIDIAN_BREAKER_KEY = ResourceKey.create(Registries.ENCHANTMENT, BetterNether.C.id("obsidian_breaker"));
    public static ObsidianBreaker OBSIDIAN_BREAKER = register(OBSIDIAN_BREAKER_KEY, new ObsidianBreaker());

    public static ResourceKey<Enchantment> RUBY_FIRE_KEY = ResourceKey.create(Registries.ENCHANTMENT, BetterNether.C.id("ruby_fire"));
    public static RubyFire RUBY_FIRE = register(RUBY_FIRE_KEY, new RubyFire());

    private static <T extends Enchantment> T register(ResourceKey<Enchantment> key, T enchantment) {
        return Registry.register(Registries.ENCHANTMENT, key.location(), enchantment);
    }
}
