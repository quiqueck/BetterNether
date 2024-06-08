package org.betterx.betternether.items.materials;

import org.betterx.betternether.registry.NetherTemplates;
import org.betterx.wover.complex.api.tool.ToolTier;
import org.betterx.wover.complex.api.tool.ToolTier.ToolValues;
import org.betterx.wover.complex.api.tool.ToolTiers;

public class BNToolTiers {
    public static final ToolTier CINCINNASITE = ToolTier
            .builder("cincinnasite")
            .toolTier(BNToolMaterial.CINCINNASITE)
            .toolValuesWithOffset(ToolTiers.IRON_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier CINCINNASITE_DIAMOND = ToolTier
            .builder("cincinnasite_diamond")
            .toolTier(BNToolMaterial.CINCINNASITE_DIAMOND)
            .toolValuesWithOffset(
                    ToolTiers.DIAMOND_TOOL,
                    new ToolValues(0, 0.3f, NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE)
            )
            .build();

    public static final ToolTier NETHER_RUBY = ToolTier
            .builder("nether_ruby")
            .toolTier(BNToolMaterial.NETHER_RUBY)
            .toolValuesWithOffset(ToolTiers.DIAMOND_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier FLAMING_RUBY = ToolTier
            .builder("flaming_ruby")
            .toolTier(BNToolMaterial.FLAMING_RUBY)
            .toolValuesWithOffset(
                    ToolTiers.NETHERITE_TOOL,
                    new ToolValues(0, 0.4f, NetherTemplates.FLAMING_RUBY_TEMPLATE)
            )
            .build();

}
