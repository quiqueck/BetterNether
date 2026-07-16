package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.behaviours.interfaces.BehaviourOre;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class BlockOre extends BaseOreBlock implements BlockTagProvider, BehaviourOre {
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
            Supplier<Item> drop,
            int minCount,
            int maxCount,
            int experience,
            TagKey<Block> miningTag,
            boolean fireproof
    ) {
        super(
                Materials
                        .stone(settings, MapColor.COLOR_RED)
                        .strength(3, 5)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.NETHERRACK),
                drop,
                minCount,
                maxCount,
                experience,
                miningTag
        );
        this.fireproof = fireproof;
    }


    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        super.registerBlockTags(location, context);
        context.add(this, CommonBlockTags.NETHERRACK, CommonBlockTags.NETHER_ORES);
    }
}
