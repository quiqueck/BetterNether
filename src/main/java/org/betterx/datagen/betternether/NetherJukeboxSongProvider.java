package org.betterx.datagen.betternether;

import org.betterx.betternether.registry.NetherJukeboxSongs;
import org.betterx.betternether.registry.SoundsRegistry;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.item.JukeboxSong;

/**
 * Writes {@code data/betternether/jukebox_song/*.json} for the three gloomwood tracks that BetterNether
 * ships as music discs.
 *
 * <p>The lengths must match the .ogg files in {@code assets/betternether/sounds/music/} - the jukebox uses
 * them to decide when a disc has finished, and a value that is too short cuts the track off while it is
 * still audible. The comparator outputs are the mod's own 1..3, independent of vanilla's 1..15 scale.
 */
public class NetherJukeboxSongProvider extends WoverRegistryContentProvider<JukeboxSong> {
    public NetherJukeboxSongProvider(ModCore modCore) {
        super(modCore, "Nether Jukebox Songs", Registries.JUKEBOX_SONG);
    }

    @Override
    protected void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, NetherJukeboxSongs.GLOOM_WOODS, SoundsRegistry.MUSIC_DISC_GLOOM_WOODS, 125, 1);
        register(context, NetherJukeboxSongs.GLOOM_WISPS, SoundsRegistry.MUSIC_DISC_GLOOM_WISPS, 200, 2);
        register(context, NetherJukeboxSongs.GLOOMSCULK, SoundsRegistry.MUSIC_DISC_GLOOMSCULK, 154, 3);
    }

    private static void register(
            BootstrapContext<JukeboxSong> context,
            ResourceKey<JukeboxSong> key,
            Holder<SoundEvent> soundEvent,
            float lengthInSeconds,
            int comparatorOutput
    ) {
        context.register(
                key,
                new JukeboxSong(
                        soundEvent,
                        Component.translatable(Util.makeDescriptionId("jukebox_song", key.identifier())),
                        lengthInSeconds,
                        comparatorOutput
                )
        );
    }
}
