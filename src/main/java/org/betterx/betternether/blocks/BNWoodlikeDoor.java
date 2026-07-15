package org.betterx.betternether.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

// TODO(1.21.7): bclib BaseDoorBlock.Wood was removed; extend vanilla DoorBlock directly.
public class BNWoodlikeDoor extends DoorBlock {

    public BNWoodlikeDoor(Block source, WoodType type) {
        super(type.setType(), BlockBehaviour.Properties.ofFullCopy(source));
    }

    public BNWoodlikeDoor(Properties properties, WoodType type) {
        super(type.setType(), properties);
    }
}
