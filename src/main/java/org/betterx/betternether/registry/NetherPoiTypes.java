package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.poi.api.PoiManager;
import org.betterx.wover.poi.api.WoverPoiType;

public class NetherPoiTypes {
    public static final WoverPoiType PIG_STATUE = PoiManager.register(
            BetterNether.C.id("pig_statue"),
            WoverPoiType.getBlockStates(NetherBlocks.PIG_STATUE_RESPAWNER),
            1, 1
    );

    public static void register() {

    }
}
