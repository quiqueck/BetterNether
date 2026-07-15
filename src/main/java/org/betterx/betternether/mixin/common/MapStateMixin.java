package org.betterx.betternether.mixin.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Historically this made map decorations behave normally in the Nether by neutralizing a
 * {@code this.dimension == Level.NETHER} branch inside {@code MapItemSavedData.addDecoration}. That branch was
 * removed from vanilla (addDecoration no longer references {@code Level.NETHER}), so the special-casing is now
 * unnecessary — map compass/player markers already behave correctly in the Nether. Kept as an (empty) mixin
 * placeholder.
 * <p>
 * TODO(1.21.7): remove this mixin entirely, or re-add nether-specific decoration handling if a regression appears.
 */
@Mixin(MapItemSavedData.class)
public abstract class MapStateMixin extends SavedData {
    public MapStateMixin(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey<Level> registryKey) {
        super();
    }
}
