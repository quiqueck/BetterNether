package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundsRegistry {
    public static final Holder<SoundEvent> AMBIENT_MUSHROOM_FOREST = registerHolder(
            "betternether.ambient.mushroom_forest");
    public static final Holder<SoundEvent> AMBIENT_GRAVEL_DESERT = registerHolder("betternether.ambient.gravel_desert");
    public static final Holder<SoundEvent> AMBIENT_NETHER_JUNGLE = registerHolder("betternether.ambient.nether_jungle");
    public static final Holder<SoundEvent> AMBIENT_SWAMPLAND = registerHolder("betternether.ambient.swampland");

    public static final Holder<SoundEvent> MOB_FIREFLY_FLY = registerHolder("betternether.mob.firefly.fly");
    public static final Holder<SoundEvent> MOB_JELLYFISH = registerHolder("betternether.mob.jellyfish");
    public static final Holder<SoundEvent> MOB_NAGA_IDLE = registerHolder("betternether.mob.naga_idle");
    public static final Holder<SoundEvent> MOB_NAGA_ATTACK = registerHolder("betternether.mob.naga_attack");
    public static final Holder<SoundEvent> MOB_SKULL_FLIGHT = registerHolder("betternether.mob.skull_flight");

    public static SoundEvent register(String id) {
        ResourceLocation loc = new ResourceLocation(BetterNether.MOD_ID, id);
        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                loc,
                SoundEvent.createVariableRangeEvent(loc)
        );
    }

    public static Holder<SoundEvent> registerHolder(String id) {
        ResourceLocation loc = new ResourceLocation(BetterNether.MOD_ID, id);
        return Registry.registerForHolder(
                BuiltInRegistries.SOUND_EVENT,
                loc,
                SoundEvent.createVariableRangeEvent(loc)
        );
    }

    public static void ensureStaticallyLoaded() {
    }
}
