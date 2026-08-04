package org.betterx.betternether.items.materials;

import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.complex.api.equipment.ToolTier;
import de.ambertation.wover.complex.api.equipment.ToolTier.ToolValues;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;

public class BNToolTiers {
    public static final ToolTier CINCINNASITE = ToolTier
            .builder("cincinnasite")
            .level(ToolTiers.IRON_TOOL.level)
            .toolMaterial(BNToolMaterial.CINCINNASITE)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValuesWithOffset(ToolTiers.IRON_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier CINCINNASITE_DIAMOND = ToolTier
            .builder("cincinnasite_diamond")
            .level(ToolTiers.DIAMOND_TOOL.level)
            .toolMaterial(BNToolMaterial.CINCINNASITE_DIAMOND)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValuesWithOffset(
                    ToolTiers.DIAMOND_TOOL,
                    new ToolValues(0, 0.3f, NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE)
            )
            .build();

    public static final ToolTier NETHER_RUBY = ToolTier
            .builder("nether_ruby")
            .level(ToolTiers.DIAMOND_TOOL.level)
            .toolMaterial(BNToolMaterial.NETHER_RUBY)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValuesWithOffset(ToolTiers.DIAMOND_TOOL, new ToolValues(0, 0.2f))
            .build();

    public static final ToolTier FLAMING_RUBY = ToolTier
            .builder("flaming_ruby")
            .level(ToolTiers.NETHERITE_TOOL.level)
            .toolMaterial(BNToolMaterial.FLAMING_RUBY)
            .blockTag(MineableTags.NEEDS_NETHERITE_TOOL)
            .toolValuesWithOffset(
                    ToolTiers.NETHERITE_TOOL,
                    new ToolValues(0, 0.4f, NetherTemplates.FLAMING_RUBY_TEMPLATE)
            )
            .build();

}
