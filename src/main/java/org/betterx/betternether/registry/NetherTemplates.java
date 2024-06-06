package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.item.api.smithing.SmithingTemplates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class NetherTemplates {
    public static final ResourceLocation EMPTY_SLOT_BOWL = BetterNether.C.id("item/empty_slot_bowl");
    public static final ResourceLocation EMPTY_SLOT_BLOCK = BetterNether.C.id("item/empty_slot_block");

    public static final SmithingTemplateItem NETHER_BOWL_SMITHING_TEMPLATE = NetherItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "bowl_upgrade",
                    List.of(EMPTY_SLOT_BOWL),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );

    public static final SmithingTemplateItem FLAMING_RUBY_TEMPLATE = NetherItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "flaming_ruby_upgrade",
                    SmithingTemplates.ARMOR_AND_TOOLS,
                    List.of(EMPTY_SLOT_BLOCK)
            );

    public static final SmithingTemplateItem CINCINNASITE_DIAMOND_TEMPLATE = NetherItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "cincinnasite_diamond_upgrade",
                    List.of(SmithingTemplates.EMPTY_SLOT_DIAMOND),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );

    public static void ensureStaticallyLoaded() {
    }
}
