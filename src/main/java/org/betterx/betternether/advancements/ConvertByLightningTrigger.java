package org.betterx.betternether.advancements;

import org.betterx.betternether.BetterNether;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
// 26.2 split net.minecraft.advancements.criterion into .predicates and .triggers. Unlike 26.3,
// SimpleCriterionTrigger.SimpleInstance#player() still returns Optional<ContextAwarePredicate>
// here, so only the package of that type changed.
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

import java.util.Optional;

public class ConvertByLightningTrigger
        extends SimpleCriterionTrigger<ConvertByLightningTrigger.TriggerInstance> implements BNCriterion.TriggerWithID<ConvertByLightningTrigger.TriggerInstance> {
    public static final Identifier ID = BetterNether.C.id("convert_by_lightning");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayer serverPlayer, ItemLike item) {
        this.trigger(serverPlayer, (triggerInstance) -> triggerInstance.matches(new ItemStack(item)));
    }

    public Criterion<TriggerInstance> match(ItemLike item) {
        return BNCriterion
                .CONVERT_BY_LIGHTNING
                .createCriterion(
                        new TriggerInstance(ItemPredicate.Builder.item()
                                                                 .of(BuiltInRegistries.ITEM, item)
                                                                 .build())
                );
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public static class TriggerInstance implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
                .group(
                        ItemPredicate
                                .CODEC
                                .fieldOf("item")
                                .forGetter((triggerInstance) -> triggerInstance.item))
                .apply(instance, TriggerInstance::new));
        private final ItemPredicate item;

        public TriggerInstance(ItemPredicate itemPredicate) {
            this.item = itemPredicate;
        }

        public boolean matches(ItemStack itemStack) {
            return this.item.test(itemStack);
        }

        @Override
        public void validate(ValidationContextSource validator) {
            SimpleInstance.super.validate(validator);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
