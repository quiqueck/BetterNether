package org.betterx.betternether.blocks;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BNWoodlikeDoor extends DoorBlock {
    public BNWoodlikeDoor(Properties properties, WoodType type) {
        super(type.setType(), properties);
    }
}
