package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.item.api.SmithingTemplateDefinition;
import org.betterx.wover.item.api.smithing.SmithingTemplates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class NetherTemplates {
    public static final ResourceLocation EMPTY_SLOT_BOWL = BetterNether.C.id("item/empty_slot_bowl");
    public static final ResourceLocation EMPTY_SLOT_BLOCK = BetterNether.C.id("item/empty_slot_block");

    public static final SmithingTemplateItem NETHER_BOWL_SMITHING_TEMPLATE = NetherItems
            .getItemRegistry()
            .defineSmithingTemplate("bowl_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_BOWL))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();

    public static final SmithingTemplateItem FLAMING_RUBY_TEMPLATE = NetherItems
            .getItemRegistry()
            .defineSmithingTemplate("flaming_ruby_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(SmithingTemplates.ARMOR_AND_TOOLS)
            .additionalSlotEmptyIcons(List.of(EMPTY_SLOT_BLOCK))
            .buildAndRegister();

    public static final SmithingTemplateItem CINCINNASITE_DIAMOND_TEMPLATE = NetherItems
            .getItemRegistry()
            .defineSmithingTemplate("cincinnasite_diamond_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_DIAMOND))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();

    public static void ensureStaticallyLoaded() {
    }
}
