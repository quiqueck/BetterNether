package org.betterx.betternether.client;

import org.betterx.betternether.blocks.NetherModels;
import org.betterx.betternether.blocks.complex.slots.AnchorTreeSaplingSlot;
import de.ambertation.wover.client.api.WoverClientTraitEntrypoint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Registers the client generators behind BetterNether's model keys. Runs through the {@code wover.client.traits}
 * entrypoint, so the generators are present in both the client and the datagen launch.
 */
@Environment(EnvType.CLIENT)
public class NetherClientModels implements WoverClientTraitEntrypoint {
    @Override
    public void registerClientTraits() {
        NetherModels.registerClientModels();
        AnchorTreeSaplingSlot.registerClientModels();
    }
}
