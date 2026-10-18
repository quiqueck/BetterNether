package org.betterx.betternether.blocks.materials;

import org.betterx.bclib.behaviours.BehaviourBuilders;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Materials {
    /**
     * The plant/sapling presets are factories, not shared static instances: {@link BlockBehaviour.Properties} is
     * mutable and block constructors chain their own settings ({@code noLootTable()}, {@code randomTicks()},
     * {@code lightLevel(...)}, {@code offsetType(...)}) onto it, so a shared instance leaked those settings into
     * every block built from it afterwards.
     */
    public static BlockBehaviour.Properties netherSapling() {
        return BehaviourBuilders.createPlant()
                                .randomTicks()
                                .sound(SoundType.CROP)
                                .noCollission()
                                .noOcclusion();
    }

    public static BlockBehaviour.Properties netherPlant() {
        return BehaviourBuilders
                .createPlant()
                .sound(SoundType.CROP)
                .noOcclusion()
                .noCollission();
    }

    public static BlockBehaviour.Properties makeNetherWood(MapColor color) {
        return BehaviourBuilders.createWood(color, false)
                                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties makeNetherGrass(MapColor color) {
        return BehaviourBuilders.createGrass(color)
                                .isValidSpawn((state, world, pos, type) -> true);
    }
}
