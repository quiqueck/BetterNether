package org.betterx.betternether.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor.Brightness;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public abstract class MapMixin extends ComplexItem {
    public MapMixin(Properties settings) {
        super(settings);
    }

    @Shadow
    private BlockState getCorrectStateForFluidBlock(Level world, BlockState state, BlockPos pos) {
        return state;
    }

    @Inject(method = "update", at = @At(value = "HEAD"), cancellable = true)
    private void bn_update(Level level, Entity entity, MapItemSavedData state, CallbackInfo info) {
        //TODO: Check for new Version
        //Code derived and adapted from Vanilla Minecraft Code in net.minecraft.world.item.MapItem.update
        if (level.dimensionType().hasCeiling()
                && level.dimension() == state.dimension
                && entity instanceof Player
        ) {
            int scale = 1 << state.scale;
            int sx = state.centerX;
            int sz = state.centerZ;
            int px = Mth.floor(entity.getX() - (double) sx) / scale + 64;
            int pz = Mth.floor(entity.getZ() - (double) sz) / scale + 64;
            int stepWidth = 128 / scale;
            if (level.dimensionType().hasCeiling()) {
                stepWidth /= 2;
            }
            MapItemSavedData.HoldingPlayer holdingPlayer = state.getHoldingPlayer((Player) entity);
            ++holdingPlayer.step;
            BlockPos.MutableBlockPos POS = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos POS2 = new BlockPos.MutableBlockPos();
            boolean bl = false;

            for (int xx = px - stepWidth + 1; xx < px + stepWidth; ++xx) {
                if ((xx & 15) == (holdingPlayer.step & 15) || bl) {
                    bl = false;
                    double d = 0.0;

                    for (int zz = pz - stepWidth - 1; zz < pz + stepWidth; ++zz) {
                        if (xx >= 0 && zz >= -1 && xx < 128 && zz < 128) {
                            int q = Mth.square(xx - px) + Mth.square(zz - pz);
                            boolean bl2 = q > (stepWidth - 2) * (stepWidth - 2);
                            int x = (sx / scale + xx - 64) * scale;
                            int z = (sz / scale + zz - 64) * scale;
                            Multiset<MapColor> multiset = LinkedHashMultiset.create();
                            LevelChunk levelChunk = level.getChunk(
                                    SectionPos.blockToSectionCoord(x),
                                    SectionPos.blockToSectionCoord(z)
                            );
                            if (!levelChunk.isEmpty()) {
                                int w = 0;
                                double height = 0.0;
                                //we do not want the special ceiling code for the nether
                                // if (level.dimensionType().hasCeiling()) {
                                // } else {
                                for (int bx = 0; bx < scale; ++bx) {
                                    for (int bz = 0; bz < scale; ++bz) {
                                        POS.set(x + bx, 0, z + bz);
                                        int testY = levelChunk.getHeight(
                                                Heightmap.Types.WORLD_SURFACE,
                                                POS.getX(),
                                                POS.getZ()
                                        ) + 1;
                                        BlockState blockState;
                                        if (testY <= level.getMinBuildHeight() + 1) {
                                            blockState = Blocks.BEDROCK.defaultBlockState();
                                        } else {
                                            //make sure we get under the nether ceiling and find the first "AIR" block
                                            do {
                                                POS.setY(--testY);
                                                blockState = levelChunk.getBlockState(POS);
                                            } while (blockState.is(Blocks.BEDROCK) || blockState.getMapColor(
                                                    level,
                                                    POS
                                            ) != MapColor.NONE && testY > level.getMinBuildHeight());

                                            do {
                                                --testY;
                                                POS.setY(testY);
                                                blockState = levelChunk.getBlockState(POS);
                                            } while (blockState.getMapColor(level, POS)
                                                    == MapColor.NONE && testY > level.getMinBuildHeight());

                                            if (testY > level.getMinBuildHeight()
                                                    && !blockState.getFluidState().isEmpty()) {
                                                int ab = testY - 1;
                                                POS2.set(POS);

                                                BlockState blockState2;
                                                do {
                                                    POS2.setY(ab--);
                                                    blockState2 = levelChunk.getBlockState(POS2);
                                                    ++w;
                                                } while (ab > level.getMinBuildHeight() && !blockState2.getFluidState()
                                                                                                       .isEmpty());

                                                blockState = this.getCorrectStateForFluidBlock(level, blockState, POS);
                                            }
                                        }

                                        state.checkBanners(level, POS.getX(), POS.getZ());
                                        height += (double) testY / (double) (scale * scale);
                                        multiset.add(blockState.getMapColor(level, POS));
                                    }
                                }

                                w /= scale * scale;
                                MapColor mapColor = Iterables.getFirst(
                                        Multisets.copyHighestCountFirst(multiset),
                                        MapColor.NONE
                                );
                                MapColor.Brightness brightness;
                                double f;
                                if (mapColor == MapColor.WATER) {
                                    f = (double) w * 0.1 + (double) (xx + zz & 1) * 0.2;
                                    if (f < 0.5) {
                                        brightness = Brightness.HIGH;
                                    } else if (f > 0.9) {
                                        brightness = Brightness.LOW;
                                    } else {
                                        brightness = Brightness.NORMAL;
                                    }
                                } else {
                                    f = (height - d) * 4.0 / (double) (scale + 4) + ((double) (xx + zz & 1) - 0.5) * 0.4;
                                    if (f > 0.6) {
                                        brightness = Brightness.HIGH;
                                    } else if (f < -0.6) {
                                        brightness = Brightness.LOW;
                                    } else {
                                        brightness = Brightness.NORMAL;
                                    }
                                }

                                d = height;
                                if (zz >= 0 && q < stepWidth * stepWidth && (!bl2 || (xx + zz & 1) != 0)) {
                                    bl |= state.updateColor(xx, zz, mapColor.getPackedId(brightness));
                                }
                            }
                        }
                    }
                }
            }

            info.cancel();
        }
    }
}
