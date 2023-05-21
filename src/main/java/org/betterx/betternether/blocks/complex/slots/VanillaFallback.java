package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;
import org.betterx.betternether.blocks.complex.NetherWoodenMaterial;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.Nullable;

public class VanillaFallback<T extends NetherWoodenMaterial<T>> extends NetherWoodenMaterial<T> {
    public VanillaFallback(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }


    @Override
    public @Nullable <M extends ComplexMaterial> Block getBlock(MaterialSlot<M> key) {
        return this.getBlock(key.suffix);
    }

    @Override
    public @Nullable Block getBlock(String key) {
        final Block bl = super.getBlock(key);
        //look for a vanilla Block
        if (bl == null) {
            return getVanillaBlock(this.baseName, key);
        }
        return bl;
    }

    public static Block getVanillaBlock(String baseName, String key) {
        var vanillaID = new ResourceLocation(baseName + "_" + key);
        return BuiltInRegistries.BLOCK.get(vanillaID);
    }
}
