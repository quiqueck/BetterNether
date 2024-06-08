package org.betterx.betternether.items.materials;

import org.betterx.betternether.registry.NetherTemplates;
import org.betterx.wover.complex.api.tool.ToolTier;
import org.betterx.wover.complex.api.tool.ToolTier.ToolValues;
import org.betterx.wover.complex.api.tool.ToolTiers;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;

public class BNToolTiers {
    public static final ToolTier CINCINNASITE = ToolTier
            .builder("cincinnasite")
            .toolTier(BNToolMaterial.CINCINNASITE)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValuesWithOffset(ToolTiers.IRON_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier CINCINNASITE_DIAMOND = ToolTier
            .builder("cincinnasite_diamond")
            .toolTier(BNToolMaterial.CINCINNASITE_DIAMOND)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValuesWithOffset(
                    ToolTiers.DIAMOND_TOOL,
                    new ToolValues(0, 0.3f, NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE)
            )
            .build();

    public static final ToolTier NETHER_RUBY = ToolTier
            .builder("nether_ruby")
            .toolTier(BNToolMaterial.NETHER_RUBY)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValuesWithOffset(ToolTiers.DIAMOND_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier FLAMING_RUBY = ToolTier
            .builder("flaming_ruby")
            .toolTier(BNToolMaterial.FLAMING_RUBY)
            .blockTag(CommonBlockTags.NEEDS_NETHERITE_TOOL)
            .toolValuesWithOffset(
                    ToolTiers.NETHERITE_TOOL,
                    new ToolValues(0, 0.4f, NetherTemplates.FLAMING_RUBY_TEMPLATE)
            )
            .build();

}
