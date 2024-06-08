package org.betterx.datagen.betternether.enchantments;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverEnchantmentProvider;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

public class NetherEnchantmentProvider extends WoverEnchantmentProvider {
    public NetherEnchantmentProvider(ModCore modCore) {
        super(modCore, "Nether Enchantments");
    }

    @Override
    protected void bootstrap(BootstrapContext<Enchantment> context) {
        final HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
        final HolderGetter<DamageType> damageGetter = context.lookup(Registries.DAMAGE_TYPE);

        NetherEnchantments.RUBY_FIRE.register(context, Enchantment
                .enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(NetherTags.FLAMING_RUBY_ENCHANTABLE),
                                itemGetter.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                                1, 1,
                                Enchantment.dynamicCost(20, 20),
                                Enchantment.dynamicCost(120, 20),
                                1,
                                EquipmentSlotGroup.ANY
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.VICTIM,
                        EnchantmentTarget.ATTACKER,
                        AllOf.entityEffects(
                                new DamageEntity(
                                        LevelBasedValue.constant(2.0F),
                                        LevelBasedValue.constant(7.0F),
                                        damageGetter.getOrThrow(DamageTypes.INDIRECT_MAGIC)
                                ),
                                new DamageItem(
                                        LevelBasedValue.constant(1.0F)
                                ),
                                new Ignite(LevelBasedValue.constant(100.0F))
                        ),
                        LootItemRandomChanceCondition.randomChance(
                                EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.25F))
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.KNOCKBACK,
                        new AddValue(LevelBasedValue.perLevel(1.0f, 2.0f))
                )
        );
    }
}
