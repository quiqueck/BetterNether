package org.betterx.betternether.registry;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.poi.api.PoiManager;
import de.ambertation.wover.poi.api.WoverPoiType;

public class NetherPoiTypes {
    public static final WoverPoiType PIG_STATUE = PoiManager.register(
            BetterNether.C.id("pig_statue"),
            WoverPoiType.getBlockStates(NetherFunctionalBlocks.PIG_STATUE_RESPAWNER),
            1, 1
    );

    public static void register() {

    }
}
