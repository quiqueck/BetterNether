package org.betterx.betternether.mixin.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MapItemSavedData.class)
public abstract class MapStateMixin extends SavedData {
    public MapStateMixin(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey<Level> registryKey) {
        super();
    }

    @Shadow
    public boolean unlimitedTracking;
    @Shadow
    public byte scale;
    @Shadow
    @Final
    public int centerX;
    @Shadow
    @Final
    public int centerZ;
    @Shadow
    @Final
    private Map<String, MapDecoration> decorations;

    @Shadow
    protected abstract void removeDecoration(String string);
    @Shadow
    protected abstract void setDecorationsDirty();

    @Shadow
    private int trackedDecorationCount;

    @Inject(method = "addDecoration", at = @At(value = "HEAD"), cancellable = true)
    private void bn_addDecoration(
            MapDecoration.Type type,
            LevelAccessor level,
            String key,
            double x,
            double y,
            double rotation,
            Component text,
            CallbackInfo info
    ) {
        if (level != null && level.dimensionType().hasCeiling()) {
            //TODO: Check for new Version
            //Code derived and adapted from Vanilla Minecraft Code in net.minecraft.world.item.MapItemSaveData.addDecoration
            int scale = 1 << this.scale;
            float px = (float) (x - (double) this.centerX) / (float) scale;
            float pz = (float) (y - (double) this.centerZ) / (float) scale;
            byte mapX = (byte) ((int) ((double) (px * 2.0F) + 0.5));
            byte mapZ = (byte) ((int) ((double) (pz * 2.0F) + 0.5));

            byte displayRotation;
            if (px >= -63.0F && pz >= -63.0F && px <= 63.0F && pz <= 63.0F) {
                rotation += rotation < 0.0 ? -8.0 : 8.0;
                displayRotation = (byte) ((int) (rotation * 16.0 / 360.0));
                //We do want the actual rotation of the player here
//                if (this.dimension == Level.NETHER && levelAccessor != null) {
//                    int l = (int)(levelAccessor.getLevelData().getDayTime() / 10L);
//                    k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
//                }
            } else {
                if (type != MapDecoration.Type.PLAYER) {
                    this.removeDecoration(key);
                    return;
                }

                if (Math.abs(px) < 320.0F && Math.abs(pz) < 320.0F) {
                    type = MapDecoration.Type.PLAYER_OFF_MAP;
                } else {
                    if (!this.unlimitedTracking) {
                        this.removeDecoration(key);
                        return;
                    }

                    type = MapDecoration.Type.PLAYER_OFF_LIMITS;
                }

                displayRotation = 0;
                if (px <= -63.0F) {
                    mapX = -128;
                }

                if (pz <= -63.0F) {
                    mapZ = -128;
                }

                if (px >= 63.0F) {
                    mapX = 127;
                }

                if (pz >= 63.0F) {
                    mapZ = 127;
                }
            }

            MapDecoration mapDecoration = new MapDecoration(type, mapX, mapZ, displayRotation, text);
            MapDecoration mapDecoration2 = this.decorations.put(key, mapDecoration);
            if (!mapDecoration.equals(mapDecoration2)) {
                if (mapDecoration2 != null && mapDecoration2.type().shouldTrackCount()) {
                    --this.trackedDecorationCount;
                }

                if (type.shouldTrackCount()) {
                    ++this.trackedDecorationCount;
                }

                this.setDecorationsDirty();
            }


            info.cancel();
        }
    }
}
