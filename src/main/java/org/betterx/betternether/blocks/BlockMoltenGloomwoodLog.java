package org.betterx.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The dark gloomwood log with lava still in it - the block at the foot of a tree standing on molten
 * ground. Its only behaviour beyond an ordinary log is the embers; everything else comes from traits at
 * the registration site.
 */
public class BlockMoltenGloomwoodLog extends RotatedPillarBlock {
    /**
     * Embers thrown per animate tick.
     * <p>
     * Emitted every call rather than on a 1-in-N chance: the client only calls animateTick on a given
     * block about 0.4 times a second, so a gate turns a handful of motes a minute into none at all. The
     * cinders on molten gloomsculk can afford one because a patch has dozens of blocks feeding it - a
     * root log is on its own, the same way a wisp is.
     */
    private static final int EMBERS = 2;

    public BlockMoltenGloomwoodLog(Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        for (int i = 0; i < EMBERS; i++) {
            final Direction face = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            // The fissures in the texture climb out of the bottom edge and thin out before the top, so
            // the embers are weighted low to match - squaring a 0..1 roll keeps them near the ground.
            final double up = random.nextDouble() * random.nextDouble();
            final double along = random.nextDouble();

            final double x = pos.getX() + 0.5 + face.getStepX() * 0.55
                    + (face.getStepX() == 0 ? along - 0.5 : 0.0);
            final double z = pos.getZ() + 0.5 + face.getStepZ() * 0.55
                    + (face.getStepZ() == 0 ? along - 0.5 : 0.0);

            world.addParticle(
                    ParticleTypes.SMALL_FLAME,
                    x, pos.getY() + up, z,
                    face.getStepX() * 0.005,
                    0.01 + random.nextDouble() * 0.02,
                    face.getStepZ() * 0.005
            );
        }
    }
}
