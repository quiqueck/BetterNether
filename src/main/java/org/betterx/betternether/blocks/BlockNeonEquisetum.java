package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.interfaces.SurvivesOnNetherrack;

import net.minecraft.world.level.material.MapColor;

public class BlockNeonEquisetum extends BaseVineBlock.Growing implements SurvivesOnNetherrack {
    public BlockNeonEquisetum() {
        super(
                Materials.NETHER_PLANT
                        .mapColor(MapColor.COLOR_GREEN)
                        .lightLevel(s -> 15)
                        .offsetType(OffsetType.XZ)
                        .randomTicks()
                        .instabreak(),
                32,
                2,
                128
        );
    }

//    @Override
//    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
//        BlockState up = level.getBlockState(pos.above());
//        return up.getBlock() == this || isSurvivable(up);
//    }
}
//public class BlockNeonEquisetum extends BlockBaseNotFull implements BonemealableBlock, SurvivesOnNetherrack, BehaviourPlant, BlockLootProvider, AddMineableShears {
//    protected static final VoxelShape SHAPE_SELECTION = box(2, 0, 2, 14, 16, 14);
//    public static final EnumProperty<BlockProperties.TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
//
//    public BlockNeonEquisetum() {
//        super(Materials.NETHER_PLANT
//                .mapColor(MapColor.COLOR_GREEN)
//                .lightLevel(s -> 15)
//                .offsetType(OffsetType.XZ)
//                .randomTicks()
//                .instabreak()
//        );
//        this.setRenderLayer(BNRenderLayer.CUTOUT);
//        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, TripleShape.BOTTOM));
//        setDropItself(false);
//    }
//
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
//        stateManager.add(SHAPE);
//    }
//
//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
//        Vec3 vec3d = state.getOffset(view, pos);
//        return SHAPE_SELECTION.move(vec3d.x, vec3d.y, vec3d.z);
//    }
//
//    @Environment(EnvType.CLIENT)
//    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
//        return 1.0F;
//    }
//
//    @Override
//    public boolean propagatesSkylightDown(BlockState state, BlockGetter view, BlockPos pos) {
//        return true;
//    }
//
//    @Override
//    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
//        BlockState up = world.getBlockState(pos.above());
//        return up.getBlock() == this || isSurvivable(up);
//    }
//
//    @Override
//    public BlockState updateShape(
//            BlockState state,
//            Direction facing,
//            BlockState neighborState,
//            LevelAccessor world,
//            BlockPos pos,
//            BlockPos neighborPos
//    ) {
//        if (!canSurvive(state, world, pos))
//            return Blocks.AIR.defaultBlockState();
//        else if (world.getBlockState(pos.below()).getBlock() != this)
//            return state.setValue(SHAPE, BlockProperties.TripleShape.BOTTOM);
//        else if (state.getValue(SHAPE) == BlockProperties.TripleShape.BOTTOM)
//            return state.setValue(SHAPE, BlockProperties.TripleShape.TOP);
//        else
//            return state;
//    }
//
//    @Override
//    public void setPlacedBy(
//            Level world,
//            BlockPos pos,
//            BlockState state,
//            @Nullable LivingEntity placer,
//            ItemStack itemStack
//    ) {
//        BlockPos upPos = pos.above();
//        if (world.getBlockState(upPos).getBlock() == this) {
//            world.setBlockAndUpdate(upPos, defaultBlockState().setValue(SHAPE, TripleShape.MIDDLE));
//            upPos = upPos.above();
//            if (world.getBlockState(upPos).getBlock() == this) {
//                world.setBlockAndUpdate(upPos, defaultBlockState().setValue(SHAPE, TripleShape.TOP));
//            }
//        }
//    }
//
//    @Override
//    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
//        MutableBlockPos blockPos = new MutableBlockPos().set(pos);
//        for (int y = pos.getY() - 1; y > 1; y--) {
//            blockPos.setY(y);
//            if (world.getBlockState(blockPos).getBlock() != this)
//                return world.getBlockState(blockPos).getBlock() == Blocks.AIR;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
//        return world.getBlockState(pos.above(8)).getBlock() != this
//                && world.getBlockState(pos.below()).canBeReplaced()
//                && world.getBlockState(pos.below(2)).canBeReplaced();
//    }
//
//    @Override
//    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
//        BlocksHelper.setWithUpdate(world, pos, state.setValue(SHAPE, BlockProperties.TripleShape.TOP));
//        BlocksHelper.setWithUpdate(world, pos.below(), defaultBlockState());
//    }
//
//    @Override
//    public boolean isRandomlyTicking(BlockState state) {
//        return state.getValue(SHAPE) == BlockProperties.TripleShape.BOTTOM;
//    }
//
//    @Override
//    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
//        super.randomTick(state, world, pos, random);
//
//        if (random.nextInt(8) == 0) {
//            if (isBonemealSuccess(world, random, pos, state)) {
//                if (world.getBlockState(pos.above()).getBlock() != this) {
//                    performBonemeal(world, random, pos, state);
//                } else {
//                    BlocksHelper.setWithUpdate(world, pos, state.setValue(SHAPE, BlockProperties.TripleShape.MIDDLE));
//                    BlocksHelper.setWithUpdate(world, pos.below(), defaultBlockState());
//                }
//            }
//        }
//    }
//
//    @Override
//    public @org.jetbrains.annotations.Nullable LootTable.Builder registerBlockLoot(
//            @NotNull ResourceLocation location,
//            @NotNull LootLookupProvider provider,
//            @NotNull ResourceKey<LootTable> tableKey
//    ) {
//        return provider.dropWithSilkTouchOrHoeOrShears(this);
//    }
//}
