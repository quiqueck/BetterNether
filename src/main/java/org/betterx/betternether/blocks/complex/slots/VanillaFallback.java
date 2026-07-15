package org.betterx.betternether.blocks.complex.slots;

import org.betterx.betternether.blocks.complex.NetherWoodenMaterial;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wooden set whose {@link #getBlock(SlotType)} falls back to the matching vanilla block
 * ({@code minecraft:<baseName>_<slot>}) when this set does not register one itself.
 */
public class VanillaFallback<T extends NetherWoodenMaterial<T>> extends NetherWoodenMaterial<T> {
    public VanillaFallback(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    public @Nullable Block getBlock(@NotNull SlotType type) {
        final Block bl = super.getBlock(type);
        if (bl == null) {
            return getVanillaBlock(this.baseName, type.suffix());
        }
        return bl;
    }

    public static Block getVanillaBlock(String baseName, String key) {
        var vanillaID = ResourceLocation.withDefaultNamespace(baseName + "_" + key);
        return BuiltInRegistries.BLOCK.getValue(vanillaID);
    }
}
