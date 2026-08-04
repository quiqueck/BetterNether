package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherDecorBlocks;

import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12, KEEP per plan - genuine block-entity-adjacent behaviour, not
// dissolved): its dead canSuffocate/isSimpleFullBlock/allowsSpawning overrides are gone; lightLevel()/
// noOcclusion() move to the registration site (NetherDecorBlocks.GEYSER). Always dropped itself unconditionally
// via BlockBase's inherited getDrops() override (no loot table json was generated for it), reproduced
// explicitly as NetherLoot.dropSelfNoExplosion().
public class BlockGeyser extends Block {
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 4, 15);

    public BlockGeyser(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 5; i++) {
            world.addParticle(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.4 + random.nextDouble() * 0.1,
                    pos.getY() + 0.125,
                    pos.getZ() + 0.4 + random.nextDouble() * 0.1,
                    random.nextDouble() * 0.02 - 0.01,
                    0.05D + random.nextDouble() * 0.05,
                    random.nextDouble() * 0.02 - 0.01
            );

            world.addParticle(
                    ParticleTypes.LARGE_SMOKE,
                    pos.getX() + 0.4 + random.nextDouble() * 0.1,
                    pos.getY() + 0.125,
                    pos.getZ() + 0.4 + random.nextDouble() * 0.1,
                    random.nextDouble() * 0.02 - 0.01,
                    0.05D + random.nextDouble() * 0.05,
                    random.nextDouble() * 0.02 - 0.01
            );

            world.addParticle(
                    ParticleTypes.LAVA,
                    pos.getX() + 0.4 + random.nextDouble() * 0.1,
                    pos.getY() + 0.125 + random.nextDouble() * 0.1,
                    pos.getZ() + 0.4 + random.nextDouble() * 0.1,
                    random.nextDouble() * 0.02 - 0.01,
                    0.05D + random.nextDouble() * 0.05,
                    random.nextDouble() * 0.02 - 0.01
            );
        }

        if (random.nextDouble() < 0.1D) {
            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    SoundEvents.FIRE_AMBIENT,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    false
            );
        }
        if (random.nextDouble() < 0.1D) {
            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    false
            );
        }
        if (random.nextDouble() < 0.1D) {
            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    SoundEvents.LAVA_POP,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    false
            );
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.fireImmune() && entity instanceof LivingEntity) {
            entity.hurt(world.damageSources().inFire(), 3F);
            entity.igniteForSeconds(1);
        }

        super.stepOn(world, pos, state, entity);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below())
                    .is(CommonBlockTags.TERRAIN);//.isFaceSturdy(world, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canSurvive(state, world, pos))
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }
}
