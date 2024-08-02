package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.interfaces.SurvivesOnNetherrackNyliumAndSculk;
import org.betterx.betternether.interfaces.SurvivesOnSoilOrLogs;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockNetherGrass extends BaseBlockNetherGrass implements SurvivesOnNetherrackNyliumAndSculk {

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveOnTop(level, pos);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnNetherrackNyliumAndSculk.super.isTerrain(state);
    }

    public static class JunglePlant extends BlockNetherGrass implements BlockModelProvider {
        @Environment(EnvType.CLIENT)
        @Override
        public void provideBlockModels(WoverBlockModelGenerators generators) {
            var JP1 = BetterNether.C.mk("block/jungle_plant_1");
            var JP2 = BetterNether.C.mk("block/jungle_plant_2");
            var JP3 = BetterNether.C.mk("block/jungle_plant_3");
            BNModels.createComplex(
                    generators,
                    this,
                    List.of(
                            BNModels.ModelSource.of(
                                    WoverBlockModelGenerators.CROSS,
                                    "_1_a",
                                    List.of((id, all) -> Variant
                                            .variant()
                                            .with(VariantProperties.MODEL, id)
                                            .with(VariantProperties.WEIGHT, 10)
                                    ),
                                    BNModels.TextureSource.of(TextureSlot.CROSS, JP1)
                            ),
                            BNModels.ModelSource.of(
                                    BNModels.CROP_BLOCK_MODEL_LOCATION,
                                    "_1_b",
                                    List.of((id, all) -> Variant
                                            .variant()
                                            .with(VariantProperties.MODEL, id)
                                            .with(VariantProperties.WEIGHT, 10)
                                    ),
                                    BNModels.TextureSource.of(TextureSlot.TEXTURE, JP1)
                            ),
                            BNModels.ModelSource.of(
                                    BNModels.JUNGLE_PLANT_MODEL_LOCATION,
                                    "_2",
                                    List.of(
                                            (id, all) -> Variant.variant().with(VariantProperties.MODEL, id),
                                            (id, all) -> Variant
                                                    .variant()
                                                    .with(VariantProperties.MODEL, id)
                                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
                                            (id, all) -> Variant
                                                    .variant()
                                                    .with(VariantProperties.MODEL, id)
                                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180),
                                            (id, all) -> Variant
                                                    .variant()
                                                    .with(VariantProperties.MODEL, id)
                                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                    ),
                                    BNModels.TextureSource.of(TextureSlot.PARTICLE, JP2),
                                    BNModels.TextureSource.of(TextureSlot.TEXTURE, JP3)
                            ),
                            BNModels.ModelSource.of(
                                    WoverBlockModelGenerators.CROSS,
                                    "_3",
                                    List.of((id, all) -> Variant
                                            .variant()
                                            .with(VariantProperties.MODEL, id)
                                            .with(VariantProperties.WEIGHT, 2)
                                    ),
                                    BNModels.TextureSource.of(TextureSlot.CROSS, JP3)
                            )
                    )
            );
        }
    }


    public static class SwampGrass extends BlockNetherGrass implements BlockModelProvider {
        @Environment(EnvType.CLIENT)
        @Override
        public void provideBlockModels(WoverBlockModelGenerators generators) {
            BNModels.provideGrassBlockModels(generators, this, "swamp_grass", 3);
        }
    }

    public static class BoneGrass extends BaseBlockNetherGrass.OnEverything implements BlockModelProvider {
        @Environment(EnvType.CLIENT)
        @Override
        public void provideBlockModels(WoverBlockModelGenerators generators) {
            BNModels.provideGrassBlockModels(generators, this, "bone_grass", 3);
        }
    }

    public static class SepiaBoneGrass extends BaseBlockNetherGrass.OnEverything implements BlockModelProvider {
        @Environment(EnvType.CLIENT)
        @Override
        public void provideBlockModels(WoverBlockModelGenerators generators) {
            BNModels.provideGrassBlockModels(generators, this, "sepia_bone_grass", 3);
        }
    }

    public static class NetherGrass extends BlockNetherGrass implements BlockModelProvider {
        @Environment(EnvType.CLIENT)
        @Override
        public void provideBlockModels(WoverBlockModelGenerators generators) {
            final var T1 = BetterNether.C.mk("block/ngrass_1");
            final var T2 = BetterNether.C.mk("block/ngrass_2");
            final var T3 = BetterNether.C.mk("block/ngrass_3");

            BNModels.createComplex(
                    generators,
                    this,
                    List.of(
                            BNModels.ModelSource.of(
                                    BNModels.GRASS_FAN_MODEL_LOCATION,
                                    "_1",
                                    List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                                    BNModels.TextureSource.of(TextureSlot.TEXTURE, T1)
                            ),
                            BNModels.ModelSource.of(
                                    WoverBlockModelGenerators.CROSS,
                                    "_2",
                                    List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                                    BNModels.TextureSource.of(TextureSlot.CROSS, T2)
                            ),
                            BNModels.ModelSource.of(
                                    BNModels.GRASS_FAN_MODEL_LOCATION,
                                    "_3",
                                    List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                                    BNModels.TextureSource.of(TextureSlot.TEXTURE, T3)
                            )
                    )
            );
        }
    }
}

abstract class BaseBlockNetherGrass extends BasePlantBlock implements BehaviourPlant, BlockLootProvider {
    private static final VoxelShape SHAPE = box(4, 0, 4, 14, 12, 14);

    public BaseBlockNetherGrass() {
        super(Materials.makeNetherGrass(MapColor.TERRACOTTA_GRAY).offsetType(Block.OffsetType.XZ));
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(view, pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (!canSurvive(state, world, pos))
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouchOrShears(this);
    }

    public static class OnEverything extends BaseBlockNetherGrass implements SurvivesOnSoilOrLogs {
        public OnEverything() {
            super();
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return canSurviveOnTop(level, pos);
        }

        @Override
        public boolean isTerrain(BlockState state) {
            return SurvivesOnSoilOrLogs.super.isTerrain(state);
        }
    }
}
