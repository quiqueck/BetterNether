package org.betterx.betternether.blocks.complex.slots;

import org.betterx.betternether.blocks.complex.NetherWoodenMaterial;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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

    /**
     * WP8.3 (REVIEWED) scope note: the vanilla-alignment review's nether-wood sound split
     * ({@link NetherWoodenMaterial#soundOverride}/{@link NetherWoodenMaterial#setTypeSound}) and
     * furnace-fuel policy ({@link NetherWoodenMaterial#fuelTrait}) cover this mod's own 9 nether-wood species
     * only. Both {@link VanillaWood} (overworld-wood-flavored furniture) and {@link VanillaNetherWood}
     * (crimson/warped furniture built on vanilla nether wood) sit on this shared base class but were not part
     * of that review, so all three hooks are neutralized here rather than left to silently inherit the
     * 9-species behaviour. (In practice neither subclass registers a door/trapdoor/button/plate/gate/sign
     * slot, so {@code setTypeSound} would be inert either way - null here for defensive clarity.)
     */
    @Override
    protected SoundType soundOverride(SlotType slot) {
        return null;
    }

    /** @see #soundOverride(SlotType) */
    @Override
    protected SoundType setTypeSound() {
        return null;
    }

    /** @see #soundOverride(SlotType) */
    @Override
    protected BlockTrait<?, ?> fuelTrait(SlotType slot) {
        return null;
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
        // BLOCK is a defaulted registry: getValue returns AIR for a missing key. Return null instead so
        // callers (e.g. BlockSet#recipeMaterial) fall through to the item slot for item-only slots like BOAT.
        if (!BuiltInRegistries.BLOCK.containsKey(vanillaID)) return null;
        return BuiltInRegistries.BLOCK.getValue(vanillaID);
    }
}
