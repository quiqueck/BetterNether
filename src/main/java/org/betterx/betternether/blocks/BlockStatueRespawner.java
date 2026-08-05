package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12, KEEP per plan - genuine behaviour, not dissolved): its dead
// canSuffocate/isSimpleFullBlock/allowsSpawning overrides are gone; lightLevel()/noOcclusion() move to the
// registration site (NetherFunctionalBlocks.PIG_STATUE_RESPAWNER). setDropItself(false) is gone too - it only ever
// made the block fall through to the vanilla loot-table-driven getDrops(), which is what happens by default
// once the class no longer extends BlockBase.
public class BlockStatueRespawner extends Block {
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape CULL_SHAPE = Shapes.or(
            box(9, 0, 4, 13, 12, 8),
            box(5, 0, 8, 7, 6, 10),
            box(9, 0, 8, 13, 12, 122),
            box(5, 0, 6, 7, 6, 8)
    );
    private static final DustParticleOptions EFFECT = new DustParticleOptions(0xFF0000, 1.0F);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    // Stored as Item + count, not a pre-built ItemStack: this constructor runs during block
    // registration at mod bootstrap, well before item data components are bound (they bind only
    // after the datapack/recipe reload) - constructing an ItemStack that early throws "Components
    // not bound yet". The stack is materialized lazily at actual use time in useWithoutItem() below.
    private final Item requiredItemType;
    private final int requiredItemCount;

    public BlockStatueRespawner(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TOP, false));

        Item item = BuiltInRegistries.ITEM.getOptional(BuiltInRegistries.ITEM.getKey(Items.GLOWSTONE))
                                          .orElse(Items.GLOWSTONE);
        if (item == Items.AIR)
            item = Items.GLOWSTONE;
        requiredItemType = item;
        requiredItemCount = 4;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, TOP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState) {
        return CULL_SHAPE;
    }


    @Override
    public InteractionResult useWithoutItem(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == requiredItemType && stack.getCount() >= requiredItemCount) {
            float y = state.getValue(TOP) ? 0.4F : 1.4F;
            if (!player.isCreative()) {
                player.getMainHandItem().shrink(requiredItemCount);
            }
            for (int i = 0; i < 50; i++)
                world.addParticle(EFFECT,
                        pos.getX() + world.getRandom().nextFloat(),
                        pos.getY() + y + world.getRandom().nextFloat() * 0.2,
                        pos.getZ() + world.getRandom().nextFloat(), 0, 0, 0
                );
            player.sendOverlayMessage(Component.translatable("message.spawn_set", new Object[0]));
            if (!world.isClientSide()) {
                ((ServerPlayer) player).setRespawnPosition(
                        new ServerPlayer.RespawnConfig(
                                LevelData.RespawnData.of(world.dimension(), pos, player.getYHeadRot(), 0.0F),
                                false
                        ),
                        true
                );
            }
            player.playSound(SoundEvents.TOTEM_USE, 0.7F, 1.0F);
            return InteractionResult.SUCCESS;
        } else {
            player.sendOverlayMessage(Component.translatable(
                    "message.spawn_help",
                    new ItemStack(requiredItemType, requiredItemCount)
            ));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(TOP))
            return true;
        BlockState up = world.getBlockState(pos.above());
        return up.isAir() || (up.getBlock() == this && up.getValue(TOP));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide())
            BlocksHelper.setWithUpdate(world, pos.above(), state.setValue(TOP, true));
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
        if (state.getValue(TOP)) {
            return world.getBlockState(pos.below()).getBlock() == this ? state : Blocks.AIR.defaultBlockState();
        } else {
            return world.getBlockState(pos.above()).getBlock() == this ? state : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return BlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return BlocksHelper.mirrorHorizontal(state, mirror, FACING);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() && state.getValue(TOP) && world.getBlockState(pos.below()).getBlock() == this) {
            world.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
        }
        return super.playerWillDestroy(world, pos, state, player);
    }
}
