package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code betternether:ruby_fire}: the auto-smelt on block break, and the thorns-shaped
 * retaliation the enchantment definition actually describes.
 * <p>
 * Note what the enchantment really is - {@code "enchanted": "victim", "affected": "attacker"}. That
 * makes it a retaliation effect: the wearer being hit burns the attacker. Swinging a Fireruby sword at
 * something does <em>not</em> ignite it, which is why {@link #rubyFireBurnsTheAttackerNotTheVictim}
 * asserts the direction explicitly rather than just "something caught fire somewhere".
 */
public class RubyFireGameTest {
    private static final BlockPos PLAYER_POS = new BlockPos(1, 2, 1);
    private static final BlockPos ORE_POS = new BlockPos(3, 2, 3);
    private static final BlockPos VICTIM_POS = new BlockPos(5, 2, 1);
    private static final BlockPos ATTACKER_POS = new BlockPos(5, 2, 3);

    /**
     * {@code RubyFire#getDrops} replaces a broken block's drops with their blasting result. Iron ore
     * normally drops raw iron; with a Fireruby pickaxe it must drop the ingot instead.
     */
    @GameTest
    public void rubyFirePickaxeAutoSmeltsOreDrops(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        // setupItemStack is what puts ruby_fire on the pickaxe; a bare `new ItemStack` has no
        // enchantments at all and would make this test pass or fail for the wrong reason.
        final ItemStack pickaxe = gameReady(
                helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)
        );

        helper.setBlock(ORE_POS, Blocks.IRON_ORE);
        final BlockPos abs = helper.absolutePos(ORE_POS);
        final BlockState state = helper.getLevel().getBlockState(abs);

        Block.dropResources(state, helper.getLevel(), abs, null, player, pickaxe);

        final List<ItemStack> dropped = helper.getLevel()
                                              .getEntitiesOfClass(
                                                      net.minecraft.world.entity.item.ItemEntity.class,
                                                      new net.minecraft.world.phys.AABB(abs).inflate(3.0)
                                              )
                                              .stream()
                                              .map(net.minecraft.world.entity.item.ItemEntity::getItem)
                                              .toList();

        if (dropped.isEmpty()) {
            failures.add("breaking iron ore with a Fireruby pickaxe dropped nothing at all");
        } else {
            final boolean smelted = dropped.stream().anyMatch(s -> s.is(Items.IRON_INGOT));
            final boolean raw = dropped.stream().anyMatch(s -> s.is(Items.RAW_IRON));
            if (!smelted) {
                failures.add("expected an iron ingot from the auto-smelt but got " + dropped);
            }
            if (raw) {
                failures.add("raw iron was dropped as well - the original drops were not replaced");
            }
        }

        failIfAny(helper, "Ruby Fire auto-smelt regression", failures);
        helper.succeed();
    }

    /**
     * The retaliation direction, with the vanilla damage cooldown designed around rather than fought.
     * <p>
     * A {@code LivingEntity} that has just been hurt is immune for the next 20 ticks, so one victim can
     * only usefully be attacked once. Each attempt therefore gets a brand-new victim. The effect fires
     * on a 25% roll, so a single attempt proves nothing: 30 attempts make a false negative about a
     * one-in-5600 event.
     */
    @GameTest(maxTicks = 400)
    public void rubyFireBurnsTheAttackerNotTheVictim(GameTestHelper helper) {
        final Outcome rubyFire = retaliate(
                helper,
                () -> gameReady(helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ArmorSlot.CHESTPLATE_SLOT))
        );
        // Vanilla Thorns is written with the same "enchanted: victim, affected: attacker" shape as
        // ruby_fire. Running it through the identical harness separates "ruby_fire points the wrong way"
        // from "this test cannot observe retaliation at all".
        final Outcome thorns = retaliate(helper, () -> {
            final ItemStack chest = new ItemStack(Items.NETHERITE_CHESTPLATE);
            chest.enchant(
                    helper.getLevel()
                          .registryAccess()
                          .lookupOrThrow(Registries.ENCHANTMENT)
                          .getOrThrow(Enchantments.THORNS),
                    3
            );
            return chest;
        });

        final List<String> failures = new ArrayList<>();
        if (!thorns.attackerAffected()) {
            failures.add("control: vanilla Thorns III never retaliated in " + ATTEMPTS
                    + " hits either - the harness cannot observe post-attack effects, so the ruby_fire"
                    + " result proves nothing");
        } else {
            if (!rubyFire.attackerAffected()) {
                failures.add(ATTEMPTS + " hits on a Fireruby-armoured victim never affected the attacker");
            }
            // The wearer burning is only evidence of a wrong-way effect if the attacker did *not* burn.
            // Mob#doHurtTarget sets its target alight when the attacker itself is on fire, so once
            // ruby_fire has ignited the attacker inside doPostAttackEffects, the very same call can go on
            // to ignite the victim as a second-order consequence. Asserting on the victim unconditionally
            // made this test flaky, since that follow-on ignition is a difficulty-scaled random roll.
            if (rubyFire.victimAffected() && !rubyFire.attackerAffected()) {
                failures.add("the wearer caught fire while the attacker did not"
                        + " - the effect is pointed at the wrong entity");
            }
        }

        failIfAny(helper, "Ruby Fire retaliation regression", failures);
        helper.succeed();
    }

    /** The effect fires on a roll, so one attempt proves nothing; 30 make a false negative remote. */
    private static final int ATTEMPTS = 30;

    private record Outcome(boolean attackerAffected, boolean victimAffected) {}

    /**
     * Hits a freshly-armoured victim up to {@link #ATTEMPTS} times and reports who ended up hurt or on
     * fire.
     * <p>
     * Each attempt gets a brand-new victim on purpose: a {@code LivingEntity} that has just been hurt is
     * immune for the next 20 ticks, and this whole loop runs inside a single tick, so reusing one victim
     * would land exactly one real hit.
     */
    private static Outcome retaliate(GameTestHelper helper, Supplier<ItemStack> chestplate) {
        final Zombie attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, ATTACKER_POS);
        final float attackerFullHealth = attacker.getHealth();

        boolean attackerAffected = false;
        boolean victimAffected = false;

        for (int attempt = 0; attempt < ATTEMPTS && !attackerAffected; attempt++) {
            final Zombie victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, VICTIM_POS);
            victim.setItemSlot(EquipmentSlot.CHEST, chestplate.get());

            attacker.setRemainingFireTicks(0);
            attacker.setHealth(attackerFullHealth);
            attacker.invulnerableTime = 0;
            victim.setRemainingFireTicks(0);

            attacker.doHurtTarget(helper.getLevel(), victim);

            // "Affected" covers either half of a retaliation: Thorns only deals damage, ruby_fire deals
            // damage and ignites.
            if (attacker.getRemainingFireTicks() > 0 || attacker.getHealth() < attackerFullHealth) {
                attackerAffected = true;
            }
            if (victim.getRemainingFireTicks() > 0) {
                victimAffected = true;
            }
            victim.discard();
        }

        attacker.discard();
        return new Outcome(attackerAffected, victimAffected);
    }

    /** A stack carrying the default enchantments {@code setupItemStack} applies. */
    private static ItemStack gameReady(GameTestHelper helper, Item item) {
        final ItemStack stack = new ItemStack(item);
        ItemStackHelper.callItemStackSetupIfPossible(stack, helper.getLevel().registryAccess());
        return stack;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
