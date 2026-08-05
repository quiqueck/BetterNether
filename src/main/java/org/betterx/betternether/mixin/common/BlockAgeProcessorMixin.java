package org.betterx.betternether.mixin.common;

import org.betterx.betternether.registry.block.NetherObsidianBlocks;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherGameRules;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//make some ruined portals blue
@Mixin(BlockAgeProcessor.class)
public class BlockAgeProcessorMixin {
    @Inject(method = "processBlock", at = @At(value = "HEAD"), cancellable = true)
    void bn_processBlock(
            LevelReader levelReader,
            BlockPos blockPos,
            BlockPos blockPos2,
            StructureBlockInfo structureBlockInfo,
            StructureBlockInfo structureBlockInfo2,
            StructurePlaceSettings structurePlaceSettings,
            CallbackInfoReturnable<StructureBlockInfo> cir
    ) {
        final boolean makeBlue = (blockPos.getX() + blockPos.getZ()) % 3 == 0;

        if (makeBlue
                && structureBlockInfo2.state().is(Blocks.OBSIDIAN)
                && bn_blueRuinedPortalsEnabled(levelReader)) {
            final BlockPos structurePos = structureBlockInfo2.pos();
            final RandomSource random = structurePlaceSettings.getRandom(structurePos);

            Block block = random.nextFloat() < 0.15F ? NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN : NetherObsidianBlocks.BLUE_OBSIDIAN;
            cir.setReturnValue(new StructureTemplate.StructureBlockInfo(
                    structurePos,
                    block.defaultBlockState(),
                    structureBlockInfo2.nbt()
            ));
            cir.cancel();
        }
    }

    private static boolean bn_blueRuinedPortalsEnabled(LevelReader levelReader) {
        if (levelReader instanceof ServerLevelAccessor serverLevelAccessor) {
            return serverLevelAccessor.getLevel().getGameRules().get(NetherGameRules.GENERATE_BLUE_RUINED_PORTALS);
        }
        return true;
    }
}
