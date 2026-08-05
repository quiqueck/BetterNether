package org.betterx.betternether.registry.item;

import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherJukeboxSongs;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;

/**
 * The music discs for the three gloomwood tracks. Each one plays a single {@link JukeboxSong} from
 * {@link NetherJukeboxSongs}; the biome keeps playing all three from its own pooled sound event.
 */
public class NetherMusicDiscItems {
    public static final Item MUSIC_DISC_GLOOM_WOODS = registerDisc(
            "music_disc_gloom_woods",
            NetherJukeboxSongs.GLOOM_WOODS
    );
    public static final Item MUSIC_DISC_GLOOM_WISPS = registerDisc(
            "music_disc_gloom_wisps",
            NetherJukeboxSongs.GLOOM_WISPS
    );
    public static final Item MUSIC_DISC_GLOOMSCULK = registerDisc(
            "music_disc_gloomsculk",
            NetherJukeboxSongs.GLOOMSCULK
    );

    private NetherMusicDiscItems() {
    }

    private static Item registerDisc(String name, ResourceKey<JukeboxSong> song) {
        return NetherItems
                .getItemRegistry()
                .defineDefaultItem(name)
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .jukeboxPlayable(song)
                .buildAndRegister();
    }

    public static void ensureLoaded() {
    }
}
