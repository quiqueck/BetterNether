package org.betterx.betternether.items.materials;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class BNToolMaterial {
    public static final ToolMaterial CINCINNASITE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            512,
            6.2F,
            2.5F,
            16,
            BNArmorMaterials.CINCINNASITE.value().repairIngredient()
    );

    public static final ToolMaterial CINCINNASITE_DIAMOND = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            2061,
            8.2F,
            3.7F,
            14,
            TagManager.ITEMS.makeTag(BetterNether.C.mk("cincinnasite_diamond/repair"))
    );

    public static final ToolMaterial NETHER_RUBY = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            2561,
            7.1F,
            3.1F,
            18,
            BNArmorMaterials.NETHER_RUBY.value().repairIngredient()
    );

    public static final ToolMaterial FLAMING_RUBY = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2861,
            10.4F,
            6.0F,
            32,
            BNArmorMaterials.FLAMING_RUBY.value().repairIngredient()
    );
}
