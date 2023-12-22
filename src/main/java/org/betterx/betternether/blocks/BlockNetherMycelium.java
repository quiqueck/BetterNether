package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.bonemeal.BonemealNyliumLike;
import org.betterx.bclib.api.v3.levelgen.features.BCLConfigureFeature;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.util.LootUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BlockNetherMycelium extends BlockBase implements BonemealNyliumLike, BehaviourStone {
    public static final BooleanProperty IS_BLUE = BooleanProperty.create("blue");
    private BonemealAPI.FeatureProvider vegetationFeature;

    public BlockNetherMycelium() {
        super(FabricBlockSettings.copyOf(Blocks.NETHERRACK).mapColor(MapColor.COLOR_GRAY).requiresTool());
        this.registerDefaultState(getStateDefinition().any().setValue(IS_BLUE, false));
        this.setDropItself(false);
    }

    public void setVegetationFeature(BCLConfigureFeature<? extends Feature<?>, ?> vegetationFeature) {
        this.vegetationFeature = () -> vegetationFeature.configuredFeature;
    }

    public void setVegetationFeature(BonemealAPI.FeatureProvider vegetationFeature) {
        this.vegetationFeature = vegetationFeature;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(IS_BLUE);
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        world.addParticle(ParticleTypes.MYCELIUM,
                pos.getX() + random.nextDouble(),
                pos.getY() + 1.1D,
                pos.getZ() + random.nextDouble(),
                0.0D, 0.0D, 0.0D
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (LootUtil.isCorrectTool(this, state, tool)) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0)
                return Collections.singletonList(new ItemStack(this.asItem()));
            else
                return Collections.singletonList(new ItemStack(Blocks.NETHERRACK));
        } else
            return super.getDrops(state, builder);
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader blockGetter,
            BlockPos blockPos,
            BlockState blockState
    ) {
        return vegetationFeature != null && BonemealNyliumLike.super.isValidBonemealTarget(
                blockGetter,
                blockPos,
                blockState
        );
    }

    @Override
    public Block getHostBlock() {
        return this;
    }

    @Override
    public @Nullable Holder<? extends ConfiguredFeature<?, ?>> getCoverFeature() {
        return vegetationFeature.getFeature();
    }
}