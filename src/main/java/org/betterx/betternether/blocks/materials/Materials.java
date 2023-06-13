package org.betterx.betternether.blocks.materials;

import org.betterx.bclib.behaviours.BehaviourBuilders;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Materials {
    public static final BlockBehaviour.Properties NETHER_GRASS = BehaviourBuilders.createGrass(MapColor.GRASS);
    public static final BlockBehaviour.Properties NETHER_SAPLING = BehaviourBuilders.createPlant().randomTicks()
                                                                                    .sound(SoundType.CROP)
                                                                                    .noCollission()
                                                                                    .noOcclusion();

    public static final BlockBehaviour.Properties NETHER_PLANT = BehaviourBuilders
            .createPlant()
            .sound(SoundType.CROP)
            .noOcclusion()
            .noCollission();

    public static BlockBehaviour.Properties makeNetherWood(MapColor color) {
        return BehaviourBuilders.createWood(color, false)
                                .requiresCorrectToolForDrops()
                                .strength(1);
    }

    public static BlockBehaviour.Properties makeNetherGrass(MapColor color) {
        return BehaviourBuilders.createGrass(color)
                                .isValidSpawn((state, world, pos, type) -> true);
    }
}
