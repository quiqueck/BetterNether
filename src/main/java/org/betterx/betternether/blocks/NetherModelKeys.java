package org.betterx.betternether.blocks;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.block.api.model.ModelKey;
import de.ambertation.wover.block.api.trait.BlockTraitKey;

/**
 * Creates the {@link ModelKey}s for BetterNether's model shapes. Each shape declares its key next to the common factory
 * that binds it; the client generator behind the key is registered by
 * {@link org.betterx.betternether.client.NetherClientModels} through the {@code wover.client.traits} entrypoint.
 */
public final class NetherModelKeys {
    private NetherModelKeys() {
    }

    public static <P> ModelKey<P> key(String path) {
        return new ModelKey<>(BlockTraitKey.ofUnique(BetterNether.C, "model/" + path));
    }
}
