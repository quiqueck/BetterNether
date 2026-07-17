package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class BlockOre extends DropExperienceBlock implements BlockTagProvider {
    /**
     * Whether this ore's block item should survive lava.
     * <p>
     * Currently inert. It used to be applied through {@code CustomBlockItemProvider#getCustomBlockItem},
     * which wover only ever consults from the legacy {@code BlockRegistry#registerLegacy} path; since
     * BetterNether moved to {@code defineDefaultBlock(...).buildAndRegister()} the hook is never called, so
     * the flag has had no effect for a while. The same is true of the {@code fireproof} parameters on
     * {@code NetherBlocks#registerStairs} and {@code NetherBlocks#registerSlab}, which their building
     * overloads ignore. Re-wire all of them together via {@code BlockDefinition#withBlockItem(...)} plus
     * {@code ItemDefinition#fireResistant()} - doing it for the ores alone would be inconsistent.
     */
    public final boolean fireproof;

    public BlockOre(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties settings,
            int experience,
            boolean fireproof
    ) {
        super(
                UniformInt.of(experience > 0 ? 1 : 0, experience),
                Materials
                        .stone(settings, MapColor.COLOR_RED)
                        .strength(3, 5)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.NETHERRACK)
        );
        this.fireproof = fireproof;
    }


    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.NETHERRACK, CommonBlockTags.NETHER_ORES);
    }
}
