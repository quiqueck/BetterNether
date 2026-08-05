package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundsRegistry {
    public static final Holder<SoundEvent> AMBIENT_MUSHROOM_FOREST = registerHolder(
            "betternether.ambient.mushroom_forest");
    public static final Holder<SoundEvent> AMBIENT_GRAVEL_DESERT = registerHolder("betternether.ambient.gravel_desert");
    public static final Holder<SoundEvent> AMBIENT_NETHER_JUNGLE = registerHolder("betternether.ambient.nether_jungle");
    public static final Holder<SoundEvent> AMBIENT_SWAMPLAND = registerHolder("betternether.ambient.swampland");

    /**
     * A {@link net.minecraft.sounds.Music} names a single sound event, so all three gloomwood tracks
     * hang off this one event and the sound engine picks between them each time the music starts.
     */
    public static final Holder<SoundEvent> MUSIC_GLOOMWOOD = registerHolder("betternether.music.gloomwood");

    /**
     * A {@link net.minecraft.world.item.JukeboxSong} also names a single sound event, but a disc has to play
     * the one track printed on its label - so the three gloomwood tracks get an event each here, next to the
     * pooled {@link #MUSIC_GLOOMWOOD} the biome uses.
     */
    public static final Holder<SoundEvent> MUSIC_DISC_GLOOM_WOODS = registerHolder("betternether.music_disc.gloom_woods");
    public static final Holder<SoundEvent> MUSIC_DISC_GLOOM_WISPS = registerHolder("betternether.music_disc.gloom_wisps");
    public static final Holder<SoundEvent> MUSIC_DISC_GLOOMSCULK = registerHolder("betternether.music_disc.gloomsculk");

    public static final Holder<SoundEvent> BLOCK_GLOOMWISP_CHIME = registerHolder("betternether.block.gloomwisp_chime");

    /**
     * The short laugh a wisp gives off when it actually pays out, in place of the single
     * {@link #BLOCK_GLOOMWISP_CHIME} note it rings the rest of the time - so the two are told apart by ear
     * rather than only by the orb appearing.
     * <p>
     * A voice rather than another struck note on purpose: the chime is the sound of brushing the plant, and
     * anything in the same family would have read as a louder version of it. A laugh says something in there
     * is pleased with you, which is the whole of what the payout means.
     */
    public static final Holder<SoundEvent> BLOCK_GLOOMWISP_BOON = registerHolder("betternether.block.gloomwisp_boon");

    public static final Holder<SoundEvent> MOB_FIREFLY_FLY = registerHolder("betternether.mob.firefly.fly");
    public static final Holder<SoundEvent> MOB_JELLYFISH = registerHolder("betternether.mob.jellyfish");
    public static final Holder<SoundEvent> MOB_NAGA_IDLE = registerHolder("betternether.mob.naga_idle");
    public static final Holder<SoundEvent> MOB_NAGA_ATTACK = registerHolder("betternether.mob.naga_attack");
    public static final Holder<SoundEvent> MOB_SKULL_FLIGHT = registerHolder("betternether.mob.skull_flight");

    public static SoundEvent register(String id) {
        Identifier loc = BetterNether.C.mk(id);
        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                loc,
                SoundEvent.createVariableRangeEvent(loc)
        );
    }

    public static Holder<SoundEvent> registerHolder(String id) {
        Identifier loc = BetterNether.C.mk(id);
        return Registry.registerForHolder(
                BuiltInRegistries.SOUND_EVENT,
                loc,
                SoundEvent.createVariableRangeEvent(loc)
        );
    }

    public static void ensureStaticallyLoaded() {
    }
}
