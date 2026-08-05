package org.betterx.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Molten gloomsculk: {@link BlockTerrain} that throws cinders off its fissures.
 * <p>
 * The only reason this is its own class is the particle hook - everything else about the block comes
 * from its traits at the registration site.
 */
public class BlockMoltenGloomsculk extends BlockTerrain {
    /** One in this many animate ticks throws a cinder. */
    private static final int CINDER_CHANCE = 12;

    /** ...and one in this many cinders is a lava spit rather than an ember. */
    private static final int SPIT_CHANCE = 5;

    public BlockMoltenGloomsculk(Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        // Only vent where the rock is open to the air. Molten gloomsculk is a terrain block and comes in
        // slabs several deep - without this the buried blocks would spray cinders into the stone above
        // them, and a patch would cost as many particles as it has blocks rather than as it has surface.
        if (!world.getBlockState(pos.above()).isAir()) return;
        if (random.nextInt(CINDER_CHANCE) != 0) return;

        final double x = pos.getX() + random.nextDouble();
        final double y = pos.getY() + 1.0;
        final double z = pos.getZ() + random.nextDouble();

        if (random.nextInt(SPIT_CHANCE) == 0) {
            world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0);
        } else {
            world.addParticle(
                    ParticleTypes.SMALL_FLAME,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.01,
                    0.015 + random.nextDouble() * 0.025,
                    (random.nextDouble() - 0.5) * 0.01
            );
        }
    }
}
