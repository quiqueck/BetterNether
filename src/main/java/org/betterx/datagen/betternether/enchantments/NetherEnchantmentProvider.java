package org.betterx.datagen.betternether.enchantments;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.NetherTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverEnchantmentProvider;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

import java.util.List;

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
                                itemGetter.getOrThrow(NetherTags.FLAMING_RUBY_PRIMARY),
                                10, 1,
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
                                new ChangeItemDamage(
                                        LevelBasedValue.constant(1.0F)
                                ),
                                new Ignite(LevelBasedValue.constant(100.0F))
                        ),
                        // Half of all hits. At the old quarter a short fight often produced nothing at
                        // all - four hits miss entirely about a third of the time - which read as the
                        // enchantment being broken rather than unlucky. At a half, four hits leave a
                        // 94% chance of at least one proc.
                        LootItemRandomChanceCondition.randomChance(
                                EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.5F))
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.KNOCKBACK,
                        new AddValue(LevelBasedValue.perLevel(1.0f, 2.0f))
                )
        );

        NetherEnchantments.OBSIDIAN_BREAKER.register(context, Enchantment
                .enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                10, 5,
                                Enchantment.dynamicCost(20, 20),
                                Enchantment.dynamicCost(120, 20),
                                1,
                                EquipmentSlotGroup.MAINHAND
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED.unwrapKey().orElseThrow().location(),
                                NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED,
                                new LevelBasedValue.Lookup(List.of(6f, 12f, 18f), new LevelBasedValue.LevelsSquared(9.0F)),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
        );
    }
}
