package org.betterx.betternether.blocks;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;

public class BlockOre extends DropExperienceBlock {
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
                settings
        );
        this.fireproof = fireproof;
    }
}
