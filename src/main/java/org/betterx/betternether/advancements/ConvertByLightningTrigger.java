package org.betterx.betternether.advancements;

import org.betterx.betternether.BetterNether;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class ConvertByLightningTrigger
        extends SimpleCriterionTrigger<ConvertByLightningTrigger.TriggerInstance>
        implements BNCriterion.TriggerWithID<ConvertByLightningTrigger.TriggerInstance> {
    public static final ResourceLocation ID = BetterNether.C.id("convert_by_lightning");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer serverPlayer, ItemLike item) {
        this.trigger(serverPlayer, (triggerInstance) -> triggerInstance.matches(new ItemStack(item)));
    }

    public Criterion<TriggerInstance> match(ItemLike item) {
        return BNCriterion
                .CONVERT_BY_LIGHTNING
                .createCriterion(
                        new TriggerInstance(ItemPredicate.Builder.item().of(item).build())
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
        public void validate(CriterionValidator criterionValidator) {
            SimpleInstance.super.validate(criterionValidator);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
