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
                                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties makeNetherGrass(MapColor color) {
        return BehaviourBuilders.createGrass(color)
                                .isValidSpawn((state, world, pos, type) -> true);
    }

    // Props-threaded variants: apply the material defaults on top of an already-configured
    // (id-bearing) Properties instance instead of building a fresh (id-less) one. Used by the
    // block constructors that receive their properties from the block-registry definition.

    public static BlockBehaviour.Properties makeNetherWood(BlockBehaviour.Properties props, MapColor color) {
        return BehaviourBuilders.createWood(props, color, false)
                                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties makeNetherGrass(BlockBehaviour.Properties props, MapColor color) {
        return BehaviourBuilders.createGrass(props, color)
                                .isValidSpawn((state, world, pos, type) -> true);
    }

    public static BlockBehaviour.Properties netherPlant(BlockBehaviour.Properties props) {
        return BehaviourBuilders.createPlant(props, MapColor.PLANT)
                                .sound(SoundType.CROP)
                                .noOcclusion()
                                .noCollission();
    }

    public static BlockBehaviour.Properties netherSapling(BlockBehaviour.Properties props) {
        return BehaviourBuilders.createPlant(props, MapColor.PLANT)
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .noCollission()
                                .noOcclusion();
    }
}
