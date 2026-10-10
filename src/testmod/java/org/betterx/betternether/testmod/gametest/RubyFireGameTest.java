package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.block.NetherOreBlocks;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
     * Nether gold ore drops gold nuggets, and nuggets have no blasting recipe - so converting the drops
     * alone left the nuggets untouched (#248). The ore itself blasts into a gold ingot, and that is what
     * a Fireruby pickaxe must produce, without the nuggets on top.
     */
    @GameTest
    public void rubyFirePickaxeSmeltsNetherGoldOreIntoAnIngot(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        final ItemStack pickaxe = gameReady(
                helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)
        );

        final Broken broken = breakOre(helper, player, pickaxe, Blocks.NETHER_GOLD_ORE);

        final List<String> failures = new ArrayList<>();
        if (broken.drops().stream().noneMatch(s -> s.is(Items.GOLD_INGOT))) {
            failures.add("expected a gold ingot from nether gold ore but got " + broken.drops());
        }
        if (broken.drops().stream().anyMatch(s -> s.is(Items.GOLD_NUGGET))) {
            failures.add("gold nuggets were dropped - the ore's drops were not replaced: " + broken.drops());
        }
        failIfAny(helper, "Ruby Fire nether gold ore regression (#248)", failures);
        helper.succeed();
    }

    /**
     * Blasting XP is fractional (raw iron 0.7, quartz 0.2). It used to be summed into an {@code int},
     * which truncated every one of those to zero (#167). Like a furnace, the fraction must be paid out
     * as a chance, so a run of iron ore breaks has to yield some XP. 0.3^20 makes a false failure
     * irrelevant; iron ore itself drops no XP, so every orb seen here comes from the auto-smelt.
     */
    @GameTest
    public void rubyFirePickaxeAwardsFractionalSmeltingXp(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        final ItemStack pickaxe = gameReady(
                helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)
        );

        int xp = 0;
        boolean smelted = true;
        for (int i = 0; i < 20; i++) {
            final Broken broken = breakOre(helper, player, pickaxe, Blocks.IRON_ORE);
            xp += broken.xp();
            smelted &= broken.drops().stream().anyMatch(s -> s.is(Items.IRON_INGOT));
        }

        final List<String> failures = new ArrayList<>();
        if (!smelted) failures.add("the iron ore was not auto-smelted, so this test measured nothing");
        if (xp == 0) failures.add("20 auto-smelted iron ores awarded no XP at all");
        failIfAny(helper, "Ruby Fire smelting XP regression (#167)", failures);
        helper.succeed();
    }

    /**
     * Silk Touch asks for the block itself, so it wins over the auto-smelt (#168): a Fireruby pickaxe
     * with Silk Touch drops the ore block, unsmelted, and - exactly like a vanilla Silk Touch pickaxe -
     * no XP (the XP comes later, from the furnace). Quartz ore is the case from #167, iron ore the
     * case where the drop would otherwise have been an ingot.
     */
    @GameTest
    public void rubyFirePickaxeWithSilkTouchDoesNotSmelt(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        final ItemStack pickaxe = gameReady(
                helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT)
        );
        pickaxe.enchant(
                helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                      .getOrThrow(Enchantments.SILK_TOUCH),
                1
        );

        final List<String> failures = new ArrayList<>();
        for (Block ore : List.of(Blocks.NETHER_QUARTZ_ORE, Blocks.IRON_ORE, Blocks.NETHER_GOLD_ORE)) {
            final Broken broken = breakOre(helper, player, pickaxe, ore);
            final boolean onlyTheOre = !broken.drops().isEmpty()
                    && broken.drops().stream().allMatch(s -> s.is(ore.asItem()));
            if (!onlyTheOre) {
                failures.add(ore.getName().getString() + " with Silk Touch dropped " + broken.drops()
                        + " instead of the block itself");
            }
            if (broken.xp() > 0) {
                failures.add(ore.getName().getString() + " with Silk Touch awarded " + broken.xp()
                        + " XP; vanilla Silk Touch awards none");
            }
        }
        failIfAny(helper, "Ruby Fire Silk Touch regression (#168)", failures);
        helper.succeed();
    }

    /**
     * Mining with Ruby Fire must pay, on average, what mining normally and then smelting what Ruby Fire
     * smelted pays: the block's own mining XP plus the smelting recipe's XP for every smelted item. For
     * ores whose drop does not smelt (coal, diamond, redstone, ...) nothing is smelted, so only the
     * mining XP is due - and the drops must be the vanilla ones.
     * <p>
     * Both sides are measured the way a player experiences them - a real {@code destroyBlock} by a
     * survival player, every orb absorbed through {@code playerTouch} - so orb merging and pickup are part
     * of what is checked. Both pickaxes are the bare Fireruby pickaxe item (same tier, no Mending, no
     * Fortune); the Ruby Fire one only gets {@code ruby_fire} on top. The vanilla side adds the furnace XP
     * of the smelted drops (or, when the ore block itself was smelted - nether gold ore, whose nuggets
     * do not smelt - of the ore block).
     */
    @GameTest(maxTicks = 100)
    public void rubyFireXpMatchesMiningAndSmeltingOnAverage(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        final Item pickaxeItem = NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT);
        final ItemStack plainPickaxe = new ItemStack(pickaxeItem);
        final ItemStack rubyPickaxe = new ItemStack(pickaxeItem);
        rubyPickaxe.enchant(NetherEnchantments.RUBY_FIRE.getHolder(helper.getLevel().registryAccess()), 1);

        final List<String> failures = new ArrayList<>();
        final StringBuilder numbers = new StringBuilder();
        for (Block ore : XP_ORES) {
            final String name = BuiltInRegistries.BLOCK.getKey(ore).toString();
            final ItemStack oreResult = smeltingResult(helper, new ItemStack(ore));
            final Stats vanilla = new Stats();
            final Stats rubyFire = new Stats();
            boolean droppedSomething = false;
            boolean oreSmelted = false;
            boolean anySmelted = false;
            for (int i = 0; i < XP_SAMPLES; i++) {
                final Broken plain = mine(helper, player, plainPickaxe, ore);
                final Broken fire = mine(helper, player, rubyPickaxe, ore);
                droppedSomething |= !plain.drops().isEmpty();

                double furnaceXp = 0;
                for (ItemStack drop : plain.drops()) furnaceXp += smeltingXp(helper, drop) * drop.getCount();
                // Nothing in the drop smelts, but Ruby Fire produced what smelting the ore block yields
                // (and mining normally does not drop it): the ore block itself went into the furnace.
                if (furnaceXp == 0 && !oreResult.isEmpty()
                        && fire.drops().stream().anyMatch(d -> d.is(oreResult.getItem()))
                        && plain.drops().stream().noneMatch(d -> d.is(oreResult.getItem()))) {
                    furnaceXp = smeltingXp(helper, new ItemStack(ore));
                    oreSmelted = true;
                }
                if (furnaceXp == 0 && !sameItems(plain.drops(), fire.drops())) {
                    failures.add(name + ": nothing smeltable was dropped, yet Ruby Fire dropped "
                            + fire.drops() + " instead of " + plain.drops());
                    break;
                }
                anySmelted |= furnaceXp > 0;
                vanilla.add(plain.xp() + furnaceXp);
                rubyFire.add(fire.xp());
            }
            if (!droppedSomething) {
                failures.add(name + ": the plain pickaxe never got a drop - wrong tool tier, nothing measured");
                continue;
            }
            // Four standard errors of the difference: a correct implementation fails about once in 16000.
            final double tolerance = 4 * Math.sqrt((vanilla.variance() + rubyFire.variance()) / XP_SAMPLES);
            numbers.append(String.format(
                    "%n  %s%s: ruby fire %.3f, mine+smelt %.3f (+-%.3f)",
                    name, anySmelted ? (oreSmelted ? " [ore smelted]" : " [drops smelted]") : " [not smelted]",
                    rubyFire.mean(), vanilla.mean(), tolerance
            ));
            if (Math.abs(rubyFire.mean() - vanilla.mean()) > tolerance) {
                failures.add(name + ": Ruby Fire paid " + String.format("%.3f", rubyFire.mean())
                        + " XP per block over " + XP_SAMPLES + " breaks, mining and smelting pays "
                        + String.format("%.3f", vanilla.mean()) + String.format(" (+-%.3f)", tolerance));
            }
        }
        helper.getLevel().getServer().sendSystemMessage(Component.literal("Ruby Fire XP:" + numbers));
        failIfAny(helper, "Ruby Fire XP average regression", failures);
        helper.succeed();
    }

    /** Every vanilla ore (and deepslate variant), ancient debris, and BetterNether's ores. */
    private static final List<Block> XP_ORES = List.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS,
            NetherOreBlocks.CINCINNASITE_ORE, NetherOreBlocks.NETHER_RUBY_ORE,
            NetherOreBlocks.NETHER_LAPIS_ORE, NetherOreBlocks.NETHER_REDSTONE_ORE
    );

    private static final int XP_SAMPLES = 200;

    private static boolean sameItems(List<ItemStack> a, List<ItemStack> b) {
        return a.stream().map(ItemStack::getItem).collect(Collectors.toSet())
                .equals(b.stream().map(ItemStack::getItem).collect(Collectors.toSet()));
    }

    private static ItemStack smeltingResult(GameTestHelper helper, ItemStack stack) {
        final SingleRecipeInput input = new SingleRecipeInput(stack);
        return helper.getLevel()
                     .recipeAccess()
                     .getRecipeFor(RecipeType.SMELTING, input, helper.getLevel())
                     .map(holder -> holder.value().assemble(input))
                     .orElse(ItemStack.EMPTY);
    }

    private static final class Stats {
        private int n;
        private double sum;
        private double sumSq;

        void add(double v) {
            n++;
            sum += v;
            sumSq += v * v;
        }

        double mean() {
            return sum / n;
        }

        double variance() {
            return n < 2 ? 0 : (sumSq - sum * sum / n) / (n - 1);
        }
    }

    private static float smeltingXp(GameTestHelper helper, ItemStack stack) {
        return helper.getLevel()
                     .recipeAccess()
                     .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), helper.getLevel())
                     .map(holder -> holder.value().experience())
                     .orElse(0.0f);
    }

    /**
     * Places {@code ore} and has {@code player} mine it with {@code tool} through
     * {@code ServerPlayerGameMode#destroyBlock}, then lets the player absorb every orb it produced (so a
     * merged orb counts for all it holds) and collects the drops.
     */
    private static Broken mine(GameTestHelper helper, ServerPlayer player, ItemStack tool, Block ore) {
        helper.setBlock(ORE_POS, ore);
        final BlockPos abs = helper.absolutePos(ORE_POS);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool.copy());
        final int before = player.totalExperience;
        player.gameMode.destroyBlock(abs);

        final AABB box = new AABB(abs).inflate(3.0);
        final List<ItemStack> drops = new ArrayList<>();
        for (ItemEntity item : helper.getLevel().getEntitiesOfClass(ItemEntity.class, box)) {
            drops.add(item.getItem().copy());
            item.discard();
        }
        for (ExperienceOrb orb : helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, box)) {
            while (!orb.isRemoved()) {
                player.takeXpDelay = 0;
                orb.playerTouch(player);
            }
        }
        return new Broken(drops, player.totalExperience - before);
    }

    private record Broken(List<ItemStack> drops, int xp) {}

    /**
     * Places {@code ore}, breaks it the way {@code ServerPlayerGameMode#destroyBlock} would, and
     * collects (then removes) every item and XP orb it produced, so the call can be repeated.
     */
    private static Broken breakOre(GameTestHelper helper, ServerPlayer player, ItemStack tool, Block ore) {
        helper.setBlock(ORE_POS, ore);
        final BlockPos abs = helper.absolutePos(ORE_POS);
        final BlockState state = helper.getLevel().getBlockState(abs);
        helper.setBlock(ORE_POS, Blocks.AIR);

        Block.dropResources(state, helper.getLevel(), abs, null, player, tool);

        final AABB box = new AABB(abs).inflate(3.0);
        final List<ItemStack> drops = new ArrayList<>();
        for (ItemEntity item : helper.getLevel().getEntitiesOfClass(ItemEntity.class, box)) {
            drops.add(item.getItem().copy());
            item.discard();
        }
        int xp = 0;
        for (ExperienceOrb orb : helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, box)) {
            xp += orb.getValue();
            orb.discard();
        }
        return new Broken(drops, xp);
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

    /**
     * The same retaliation, but with a player swinging instead of a mob, and asserting the knockback
     * as well as the fire. Ruby Fire is supposed to hit whoever attacks the wearer, and a player is an
     * attacker like any other.
     * <p>
     * The knockback comes from an ApplyEntityImpulse in the post-attack effect rather than from the
     * enchantment's minecraft:knockback component: that component only boosts the knockback the holder
     * deals when attacking, so it does nothing for someone attacking the holder.
     */
    @GameTest(maxTicks = 400)
    public void rubyFireBurnsAndShovesAPlayerAttacker(GameTestHelper helper) {
        final ServerPlayer attacker = MockPlayers.survival(helper, ATTACKER_POS);
        final float fullHealth = attacker.getHealth();

        boolean burned = false;
        boolean shoved = false;

        for (int attempt = 0; attempt < ATTEMPTS && !(burned && shoved); attempt++) {
            final Zombie victim = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, VICTIM_POS);
            victim.setItemSlot(EquipmentSlot.CHEST, gameReady(
                    helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ArmorSlot.CHESTPLATE_SLOT)
            ));

            attacker.setRemainingFireTicks(0);
            attacker.setHealth(fullHealth);
            attacker.setDeltaMovement(Vec3.ZERO);

            attacker.attack(victim);

            if (attacker.getRemainingFireTicks() > 0) burned = true;
            if (attacker.getDeltaMovement().lengthSqr() > 1.0E-4) shoved = true;
            victim.discard();
        }

        final List<String> failures = new ArrayList<>();
        if (!burned) {
            failures.add(ATTEMPTS + " player hits never set the player on fire");
        }
        if (!shoved) {
            failures.add(ATTEMPTS + " player hits never knocked the player back");
        }

        failIfAny(helper, "Ruby Fire player-attacker regression", failures);
        helper.succeed();
    }

    /**
     * The wearer being a player rather than a mob must not change anything. This is the combination the
     * effect is actually for - a player in Fireruby armour being attacked.
     */
    @GameTest(maxTicks = 400)
    public void rubyFireRetaliatesWhenTheWearerIsAPlayer(GameTestHelper helper) {
        final Zombie attacker = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, ATTACKER_POS);
        final ServerPlayer wearer = MockPlayers.survival(helper, VICTIM_POS);
        wearer.setItemSlot(EquipmentSlot.CHEST, gameReady(
                helper, NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ArmorSlot.CHESTPLATE_SLOT)
        ));

        boolean attackerAffected = false;
        for (int attempt = 0; attempt < ATTEMPTS && !attackerAffected; attempt++) {
            attacker.setRemainingFireTicks(0);
            wearer.setHealth(wearer.getMaxHealth());
            wearer.invulnerableTime = 0;

            attacker.doHurtTarget(helper.getLevel(), wearer);

            if (attacker.getRemainingFireTicks() > 0) attackerAffected = true;
        }

        if (!attackerAffected) {
            helper.fail(Component.literal(
                    ATTEMPTS + " hits on a Fireruby-armoured player never set the attacker on fire"
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * An arrow counts as an attack too, and a bow is the obvious way to fight a player wearing this.
     * <p>
     * The arrow really has to fly: {@code LivingEntity#hurtServer} does not run post-attack enchantment
     * effects at all, so calling it with an arrow damage source proves nothing. The effects come from
     * {@code AbstractArrow#onHitEntity}, which only happens on a genuine hit - the earlier version of
     * this check called hurtServer directly and reported a flat zero for exactly that reason.
     */
    @GameTest(maxTicks = 600)
    public void rubyFireRetaliatesAgainstArrows(GameTestHelper helper) {
        final ServerPlayer shooter = MockPlayers.survival(helper, ATTACKER_POS);
        final boolean[] burned = {false};
        final int[] landed = {0};
        final Zombie[] target = new Zombie[1];

        var seq = helper.startSequence();
        for (int i = 0; i < ARROW_ATTEMPTS; i++) {
            seq = seq.thenExecute(() -> {
                         final Zombie victim = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, VICTIM_POS);
                         victim.setItemSlot(EquipmentSlot.CHEST, gameReady(
                                 helper,
                                 NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ArmorSlot.CHESTPLATE_SLOT)
                         ));
                         target[0] = victim;
                         shooter.setRemainingFireTicks(0);

                         final Arrow arrow = new Arrow(
                                 helper.getLevel(), shooter,
                                 new ItemStack(Items.ARROW), new ItemStack(Items.BOW)
                         );
                         arrow.snapTo(victim.getX(), victim.getY() + 0.6, victim.getZ() - 1.5, 0.0f, 0.0f);
                         arrow.setBaseDamage(4.0);
                         arrow.shoot(0.0, 0.0, 1.0, 1.5f, 0.0f);
                         helper.getLevel().addFreshEntity(arrow);
                     })
                     .thenIdle(5)
                     .thenExecute(() -> {
                         if (shooter.getRemainingFireTicks() > 0) burned[0] = true;
                         final Zombie victim = target[0];
                         if (victim != null) {
                             if (victim.getHealth() < victim.getMaxHealth()) landed[0]++;
                             victim.discard();
                         }
                     });
        }

        seq.thenExecute(() -> {
               final List<String> failures = new ArrayList<>();
               if (landed[0] == 0) {
                   failures.add("no arrow ever hit - the test never exercised the effect");
               } else if (!burned[0]) {
                   failures.add(landed[0] + " arrow hits never set the shooter on fire");
               }
               failIfAny(helper, "Ruby Fire arrow regression", failures);
           })
           .thenSucceed();
    }

    /** At a 50% proc, 20 arrow hits miss entirely about once in a million. */
    private static final int ARROW_ATTEMPTS = 20;

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
        final Zombie attacker = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, ATTACKER_POS);
        final float attackerFullHealth = attacker.getHealth();

        boolean attackerAffected = false;
        boolean victimAffected = false;

        for (int attempt = 0; attempt < ATTEMPTS && !attackerAffected; attempt++) {
            final Zombie victim = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, VICTIM_POS);
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
