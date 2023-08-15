package org.betterx.betternether.blocks;

import org.betterx.betternether.interfaces.SurvivesOnSoulGroundAndSculk;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockSoulGrass extends BaseBlockNetherGrass.OnEverything  {
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            world.addParticle(
                    ParticleTypes.PORTAL,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble() * 2,
                    pos.getZ() + random.nextDouble(),
                    random.nextDouble() * 0.05,
                    -1,
                    random.nextDouble() * 0.05
            );
        }
    }
}
