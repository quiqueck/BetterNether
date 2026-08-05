package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

/**
 * The keys of the {@link JukeboxSong}s BetterNether's music discs play. {@code jukebox_song} is a datapack
 * registry, so these are only keys here - the entries themselves are written by
 * {@code org.betterx.datagen.betternether.NetherJukeboxSongProvider} and resolved when the level loads.
 */
public class NetherJukeboxSongs {
    public static final ResourceKey<JukeboxSong> GLOOM_WOODS = create("gloom_woods");
    public static final ResourceKey<JukeboxSong> GLOOM_WISPS = create("gloom_wisps");
    public static final ResourceKey<JukeboxSong> GLOOMSCULK = create("gloomsculk");

    private NetherJukeboxSongs() {
    }

    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, BetterNether.C.id(name));
    }
}
