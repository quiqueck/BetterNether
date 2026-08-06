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

/**
 * Makes some ruined portals blue.
 * <p>
 * 26.2 changed {@code StructureProcessor#processBlock}: the fourth parameter used to be the <em>original</em>
 * {@code StructureBlockInfo} and is now a {@code BlockPos templateRelativePos}. Verified with
 * {@code javap -p -l net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor}:
 * <pre>
 * 26.1: (LevelReader level, BlockPos targetPosition, BlockPos referencePos,
 *        StructureBlockInfo originalBlockInfo, StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings)
 * 26.2: (LevelReader level, BlockPos targetPosition, BlockPos referencePos,
 *        BlockPos templateRelativePos, StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings)
 * </pre>
 * Only the processed info was ever read here, so the fix is purely the parameter list. This build has no refmap
 * and no mixin annotation processor, so the stale descriptor compiled but could not apply - and because
 * {@code processBlock} is not overloaded, that is a hard failure at mixin-apply time, not a silent no-op.
 */
@Mixin(BlockAgeProcessor.class)
public class BlockAgeProcessorMixin {
    @Inject(method = "processBlock", at = @At(value = "HEAD"), cancellable = true)
    void bn_processBlock(
            LevelReader levelReader,
            BlockPos targetPosition,
            BlockPos referencePos,
            BlockPos templateRelativePos,
            StructureBlockInfo processedBlockInfo,
            StructurePlaceSettings structurePlaceSettings,
            CallbackInfoReturnable<StructureBlockInfo> cir
    ) {
        final boolean makeBlue = (targetPosition.getX() + targetPosition.getZ()) % 3 == 0;

        if (makeBlue
                && processedBlockInfo.state().is(Blocks.OBSIDIAN)
                && bn_blueRuinedPortalsEnabled(levelReader)) {
            final BlockPos structurePos = processedBlockInfo.pos();
            final RandomSource random = structurePlaceSettings.getRandom(structurePos);

            Block block = random.nextFloat() < 0.15F ? NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN : NetherObsidianBlocks.BLUE_OBSIDIAN;
            cir.setReturnValue(new StructureTemplate.StructureBlockInfo(
                    structurePos,
                    block.defaultBlockState(),
                    processedBlockInfo.nbt()
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
