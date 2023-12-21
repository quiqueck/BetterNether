package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.betternether.blockentities.BlockEntityFurnace;
import org.betterx.betternether.registry.BlockEntitiesRegistry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

public class BlockNetherFurnace extends AbstractFurnaceBlock implements BehaviourStone, DropSelfLootProvider<BlockNetherFurnace> {
    public static final MapCodec<BlockNetherFurnace> CODEC = simpleCodec(BlockNetherFurnace::new);

    private BlockNetherFurnace(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    public BlockNetherFurnace(Block source) {
        super(BlockBehaviour.Properties
                .ofFullCopy(source)
                .requiresCorrectToolForDrops()
                .lightLevel(BlockNetherFurnace::getLuminance)
        );
    }

    private static int getLuminance(BlockState blockState) {
        return blockState.getOptionalValue(BlockStateProperties.LIT).orElse(false) ? 13 : 0;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFurnace(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return createFurnaceTicker(world, type, BlockEntitiesRegistry.NETHERRACK_FURNACE);
    }

    @Override
    protected void openContainer(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BlockEntityFurnace) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double d = (double) pos.getX() + 0.5D;
            double e = pos.getY();
            double f = (double) pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double h = random.nextDouble() * 0.6D - 0.3D;
            double i = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : h;
            double j = random.nextDouble() * 6.0D / 16.0D;
            double k = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : h;
            world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
        }
    }
}
