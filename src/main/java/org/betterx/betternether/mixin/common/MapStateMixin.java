package org.betterx.betternether.mixin.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(MapItemSavedData.class)
public abstract class MapStateMixin extends SavedData {
    public MapStateMixin(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey<Level> registryKey) {
        super();
    }

    @Final
    @Shadow
    private boolean unlimitedTracking;
    @Final
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
    Map<String, MapDecoration> decorations;

    @Shadow
    protected abstract void removeDecoration(String string);
    @Shadow
    protected abstract void setDecorationsDirty();

    @Shadow
    private int trackedDecorationCount;

    @WrapOperation(method = "addDecoration", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;NETHER:Lnet/minecraft/resources/ResourceKey;"))
    ResourceKey<Level> bn_netherWithCompas(Operation<ResourceKey<Level>> original) {
        //this will make the game think, we are never in the nether
        return null;
    }


//    private void addDecoration(
//            Holder<MapDecorationType> type,
//            @Nullable LevelAccessor level,
//            String string,
//            double x,
//            double y,
//            double rotation,
//            @Nullable Component text
//    ) {
//        MapDecoration mapDecoration2;
//        MapDecoration mapDecoration;
//        byte displayRotation;
//        int scale = 1 << this.scale;
//        float px = (float) (x - (double) this.centerX) / (float) scale;
//        float py = (float) (y - (double) this.centerZ) / (float) scale;
//        byte mapX = (byte) ((double) (px * 2.0f) + 0.5);
//        byte mapZ = (byte) ((double) (py * 2.0f) + 0.5);
//        if (px >= -63.0f && py >= -63.0f && px <= 63.0f && py <= 63.0f) {
//            displayRotation = (byte) ((rotation + (rotation < 0.0 ? -8.0 : 8.0)) * 16.0 / 360.0);
//            //We do want the actual rotation of the player here
////            if (this.dimension == Level.NETHER && level != null) {
////                l = (int)(level.getLevelData().getDayTime() / 10L);
////                k = (byte)(l * l * 34187121 + l * 121 >> 15 & 0xF);
////            }
//        } else if (type.is(MapDecorationTypes.PLAYER)) {
//            l = 320;
//            if (Math.abs(px) < 320.0f && Math.abs(py) < 320.0f) {
//                type = MapDecorationTypes.PLAYER_OFF_MAP;
//            } else if (this.unlimitedTracking) {
//                type = MapDecorationTypes.PLAYER_OFF_LIMITS;
//            } else {
//                this.removeDecoration(string);
//                return;
//            }
//            displayRotation = 0;
//            if (px <= -63.0f) {
//                mapX = -128;
//            }
//            if (py <= -63.0f) {
//                mapZ = -128;
//            }
//            if (px >= 63.0f) {
//                mapX = 127;
//            }
//            if (py >= 63.0f) {
//                mapZ = 127;
//            }
//        } else {
//            this.removeDecoration(string);
//            return;
//        }
//        if (!(mapDecoration = new MapDecoration(type, mapX, mapZ, displayRotation, Optional.ofNullable(text))).equals((Object) (mapDecoration2 = this.decorations.put(string, mapDecoration)))) {
//            if (mapDecoration2 != null && ((MapDecorationType) mapDecoration2.type().value()).trackCount()) {
//                --this.trackedDecorationCount;
//            }
//            if (((MapDecorationType) type.value()).trackCount()) {
//                ++this.trackedDecorationCount;
//            }
//            this.setDecorationsDirty();
//        }
//    }
//
//    @Inject(method = "addDecoration", at = @At(value = "HEAD"), cancellable = true)
//    private void bn_addDecoration(
//            Holder<MapDecorationType> type,
//            LevelAccessor level,
//            String key,
//            double x,
//            double y,
//            double rotation,
//            Component text,
//            CallbackInfo info
//    ) {
//        if (level != null && level.dimensionType().hasCeiling()) {
//            //TODO: Check for new Version
//            //Code derived and adapted from Vanilla Minecraft Code in net.minecraft.world.item.MapItemSaveData.addDecoration
//            int scale = 1 << this.scale;
//            float px = (float) (x - (double) this.centerX) / (float) scale;
//            float pz = (float) (y - (double) this.centerZ) / (float) scale;
//            byte mapX = (byte) ((int) ((double) (px * 2.0F) + 0.5));
//            byte mapZ = (byte) ((int) ((double) (pz * 2.0F) + 0.5));
//
//            byte displayRotation;
//            if (px >= -63.0F && pz >= -63.0F && px <= 63.0F && pz <= 63.0F) {
//                rotation += rotation < 0.0 ? -8.0 : 8.0;
//                displayRotation = (byte) ((int) (rotation * 16.0 / 360.0));
//                //We do want the actual rotation of the player here
////                if (this.dimension == Level.NETHER && levelAccessor != null) {
////                    int l = (int)(levelAccessor.getLevelData().getDayTime() / 10L);
////                    k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
////                }
//            } else {
//                if (type != MapDecoration.Type.PLAYER) {
//                    this.removeDecoration(key);
//                    return;
//                }
//
//                if (Math.abs(px) < 320.0F && Math.abs(pz) < 320.0F) {
//                    type = MapDecoration.Type.PLAYER_OFF_MAP;
//                } else {
//                    if (!this.unlimitedTracking) {
//                        this.removeDecoration(key);
//                        return;
//                    }
//
//                    type = MapDecorationTypes.PLAYER_OFF_LIMITS;
//                }
//
//                displayRotation = 0;
//                if (px <= -63.0F) {
//                    mapX = -128;
//                }
//
//                if (pz <= -63.0F) {
//                    mapZ = -128;
//                }
//
//                if (px >= 63.0F) {
//                    mapX = 127;
//                }
//
//                if (pz >= 63.0F) {
//                    mapZ = 127;
//                }
//            }
//
//            MapDecoration mapDecoration = new MapDecoration(type, mapX, mapZ, displayRotation, text);
//            MapDecoration mapDecoration2 = this.decorations.put(key, mapDecoration);
//            if (!mapDecoration.equals(mapDecoration2)) {
//                if (mapDecoration2 != null && mapDecoration2.type().shouldTrackCount()) {
//                    --this.trackedDecorationCount;
//                }
//
//                if (type.shouldTrackCount()) {
//                    ++this.trackedDecorationCount;
//                }
//
//                this.setDecorationsDirty();
//            }
//
//
//            info.cancel();
//        }
//    }
}
