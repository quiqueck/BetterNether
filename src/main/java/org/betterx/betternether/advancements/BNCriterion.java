package org.betterx.betternether.advancements;

import org.betterx.betternether.BetterNether;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class BNCriterion {
    public interface TriggerWithID<T extends CriterionTriggerInstance> extends CriterionTrigger<T> {
        Identifier getId();
    }

    public static final Identifier BREW_BLUE_ID = BetterNether.C.id("brew_blue");
    public static final Identifier USED_FORGE_ID = BetterNether.C.id("used_forge");
    public static final Identifier DISTURBED_WISP_ID = BetterNether.C.id("disturbed_wisp");
    public static final Identifier BURNED_GLOOMSCULK_CRYSTAL_ID = BetterNether.C.id("burned_gloomsculk_crystal");
    public static final Identifier WISP_SHED_EXPERIENCE_ID = BetterNether.C.id("wisp_shed_experience");

    public static PlayerTrigger BREW_BLUE;
    public static PlayerTrigger USED_FORGE;
    public static PlayerTrigger DISTURBED_WISP;
    public static PlayerTrigger BURNED_GLOOMSCULK_CRYSTAL;
    public static PlayerTrigger WISP_SHED_EXPERIENCE;
    public static ConvertByLightningTrigger CONVERT_BY_LIGHTNING;

    public static Criterion<PlayerTrigger.TriggerInstance> BREW_BLUE_CRITERION;
    public static Criterion<PlayerTrigger.TriggerInstance> USED_FORGE_ANY_CRITERION;
    public static Criterion<PlayerTrigger.TriggerInstance> DISTURBED_WISP_CRITERION;
    public static Criterion<PlayerTrigger.TriggerInstance> BURNED_GLOOMSCULK_CRYSTAL_CRITERION;
    public static Criterion<PlayerTrigger.TriggerInstance> WISP_SHED_EXPERIENCE_CRITERION;

    public static <T extends TriggerWithID<?>> T register(T trigger) {
        return register(trigger.getId(), trigger);
    }

    public static <T extends CriterionTrigger<?>> T register(Identifier id, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, trigger);
    }

    public static void register() {
        BREW_BLUE = register(BREW_BLUE_ID, new PlayerTrigger());
        USED_FORGE = register(USED_FORGE_ID, new PlayerTrigger());
        DISTURBED_WISP = register(DISTURBED_WISP_ID, new PlayerTrigger());
        BURNED_GLOOMSCULK_CRYSTAL = register(BURNED_GLOOMSCULK_CRYSTAL_ID, new PlayerTrigger());
        WISP_SHED_EXPERIENCE = register(WISP_SHED_EXPERIENCE_ID, new PlayerTrigger());
        CONVERT_BY_LIGHTNING = register(new ConvertByLightningTrigger());

        BREW_BLUE_CRITERION = BNCriterion.BREW_BLUE.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        USED_FORGE_ANY_CRITERION = BNCriterion.USED_FORGE.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        DISTURBED_WISP_CRITERION = BNCriterion.DISTURBED_WISP.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        BURNED_GLOOMSCULK_CRYSTAL_CRITERION = BNCriterion.BURNED_GLOOMSCULK_CRYSTAL.createCriterion(new PlayerTrigger.TriggerInstance(
                Optional.empty()));
        WISP_SHED_EXPERIENCE_CRITERION = BNCriterion.WISP_SHED_EXPERIENCE.createCriterion(new PlayerTrigger.TriggerInstance(
                Optional.empty()));
    }
}
