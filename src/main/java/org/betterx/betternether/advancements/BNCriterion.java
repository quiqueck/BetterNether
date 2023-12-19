package org.betterx.betternether.advancements;

import org.betterx.betternether.BetterNether;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class BNCriterion {
    public interface TriggerWithID<T extends CriterionTriggerInstance> extends CriterionTrigger<T> {
        ResourceLocation getId();
    }

    public static final ResourceLocation BREW_BLUE_ID = BetterNether.makeID("brew_blue");
    public static final ResourceLocation USED_FORGE_ID = BetterNether.makeID("used_forge");

    public static PlayerTrigger BREW_BLUE;
    public static PlayerTrigger USED_FORGE;
    public static ConvertByLightningTrigger CONVERT_BY_LIGHTNING;

    public static Criterion<PlayerTrigger.TriggerInstance> BREW_BLUE_CRITERION;
    public static Criterion<PlayerTrigger.TriggerInstance> USED_FORGE_ANY_CRITERION;

    public static <T extends TriggerWithID<?>> T register(T trigger) {
        return register(trigger.getId(), trigger);
    }

    public static <T extends CriterionTrigger<?>> T register(ResourceLocation id, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, trigger);
    }

    public static void register() {
        BREW_BLUE = register(BREW_BLUE_ID, new PlayerTrigger());
        USED_FORGE = register(USED_FORGE_ID, new PlayerTrigger());
        CONVERT_BY_LIGHTNING = register(new ConvertByLightningTrigger());

        BREW_BLUE_CRITERION = BNCriterion.BREW_BLUE.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        USED_FORGE_ANY_CRITERION = BNCriterion.USED_FORGE.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}
