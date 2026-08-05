package org.betterx.betternether.mixin.common;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reaches the offset function on block properties.
 * <p>
 * {@link BlockBehaviour.Properties#offsetType} only takes the {@link BlockBehaviour.OffsetType} enum, which builds a
 * fixed function from it - there is no public way to install a custom one. The function itself is handed the
 * {@link net.minecraft.world.level.block.state.BlockState}, though, so a block that wants its offset to depend on
 * its own state only needs to get at the field.
 * <p>
 * Reading the existing function as well as replacing it is deliberate: a block can then wrap vanilla's own XZ
 * function rather than restating its hashing, which would otherwise be a copy silently drifting from the original.
 */
@Mixin(BlockBehaviour.Properties.class)
public interface BlockBehaviourPropertiesAccessor {
    @Accessor("offsetFunction")
    BlockBehaviour.OffsetFunction betternether$getOffsetFunction();

    @Accessor("offsetFunction")
    void betternether$setOffsetFunction(BlockBehaviour.OffsetFunction offsetFunction);
}
