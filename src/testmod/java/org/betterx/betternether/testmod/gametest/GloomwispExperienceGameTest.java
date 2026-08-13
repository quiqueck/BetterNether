package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockGloomwispVine;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the experience a gloomwisp sheds when something pushes through it - the behaviour
 * {@code GameRuleGameTest} explicitly leaves alone, since driving it needs the push-through
 * interaction rather than the gamerule.
 * <p>
 * Four things are asserted that the code cannot be read for: that a <em>mob</em> sets it off (the
 * payout used to be players-only, which is what stopped it being farmable), that the orbs are real
 * entities lying on the ground in a patch around the plant rather than experience posted straight
 * into whoever tripped it, that a plant pays once and then has to recharge, which is the only thing
 * standing between "farmable" and "a fountain", and that a plant which has paid out goes properly
 * <em>dormant</em> rather than merely stopping its ash.
 * <p>
 * That last one is here because it was got wrong: only the ash was gated, and the souls a wisp sheds
 * on being disturbed went on firing every tick something walked through it. In a cow pen that is
 * every tick, so plants that were in fact spent around ninety percent of the time never appeared to
 * stop. {@link #aHerdOfCowsDrainsThePlantsItWalksThrough} and {@link #aDrainedPlantDoesNotReactAtAll}
 * are the two halves of that: the plants do go dark, and a dark plant does nothing.
 * <p>
 * And a fifth: the wisp rolls on something <em>arriving</em>, not on something being in it. The pair
 * that pins that down are {@link #aMobLingeringInsideNeverTriggersAgain} and
 * {@link #aPlantRechargingUnderAMobIsNotRetriggeredByIt}, and it is what the {@link #ARRIVAL} and
 * {@link #LINGER} distances in the helpers below exist to tell apart.
 * <p>
 * <b>On the rolls.</b> {@code BlockGloomwispVine.EXPERIENCE_CHANCE} is 0.035 per arrival, so no single
 * push proves anything and every test here has to keep pushing. The direct-call tests drive
 * {@code entityInside} {@link #ROLLS} times in one tick, which makes a false negative a
 * {@code 0.965^8000} event - too small to write down.
 * {@link #aMobPushingThroughShedsOrbsOntoTheGround} instead earns its arrivals by walking, about one
 * per segment per patrol leg, which comes to a couple of hundred rolls over its timeout.
 */
public class GloomwispExperienceGameTest {
    private static final BlockPos GROUND = new BlockPos(2, 1, 2);
    private static final BlockPos STEM = GROUND.above();
    private static final BlockPos HEAD = STEM.above();

    /** Half-width of the floor laid under the plant, in blocks - wide enough to catch every orb. */
    private static final int FLOOR_RADIUS = 2;

    /**
     * Disturbances per direct-call test. See the class note: this is the number that makes "no payout"
     * mean the payout is broken rather than that the dice were unkind.
     */
    private static final int ROLLS = 8000;

    /**
     * How far a pusher is made to have travelled to count as having arrived in the plant, in blocks -
     * comfortably more than the block plus the widest entity here, so the rewound box lands clear of it.
     */
    private static final double ARRIVAL = 2.5;

    /**
     * The counterpart: enough to clear the speed gate, far too little to have come from outside the
     * block. This is a cow shifting its weight in a plant it is already standing in.
     */
    private static final double LINGER = 0.05;

    /**
     * The hand-walked patrol in {@link #aMobPushingThroughShedsOrbsOntoTheGround}: a stride around a
     * walking mob's, over a span just wide enough to carry it clear of the plant and back.
     * <p>
     * Deliberately not round numbers. A patrol stepping an even fraction from the block centre puts every
     * arrival on the block boundary at once, where it is decided by float error rather than by geometry -
     * measured, an aligned 0.2 stride registered a third of the crossings it actually made, which left
     * this test a coin flip dressed up as an assertion. An odd stride walks the phase across the boundary
     * instead, so a crossing registers as one. The short reach is the other half: it keeps the round trip
     * to about a dozen ticks, which is a couple of hundred rolls over the timeout.
     */
    private static final double PATROL_STEP = 0.37;
    private static final double PATROL_REACH = 1.1;

    /**
     * How far a shed orb may be from the stalk, horizontally.
     * <p>
     * {@code SCATTER_RADIUS} (0.75) plus the quarter-block the {@code ExperienceOrb} constructor pushes
     * an orb along the direction it is handed, plus a little slack. The plants here are placed with
     * {@code OFFSET} cleared so the stalk sits dead centre on its block and this can be measured
     * against the block centre rather than against the same offset the block itself computed.
     */
    private static final double MAX_SCATTER = 1.1;

    /** Every orb is worth one point - that is what makes a payout a handful rather than a single orb. */
    private static final int ORB_VALUE = 1;

    /** {@code EXPERIENCE_MIN} .. {@code EXPERIENCE_MIN + EXPERIENCE_SPREAD - 1}. */
    private static final int MIN_PAYOUT = 1;
    private static final int MAX_PAYOUT = 3;

    /** The cow pen: the two ends the herd is driven between, and the plants standing in the middle. */
    private static final BlockPos HERD_WEST = new BlockPos(1, 2, 3);
    private static final BlockPos HERD_EAST = new BlockPos(6, 2, 3);
    private static final List<BlockPos> HERD_PLANTS = List.of(
            new BlockPos(3, 1, 2),
            new BlockPos(3, 1, 4),
            new BlockPos(4, 1, 3),
            new BlockPos(4, 1, 5)
    );
    private static final int HERD_SIZE = 6;

    /**
     * A mob walking back and forth across a wisp, driven through the same vanilla entry Entity#move
     * uses to report what it passed through, until the plant pays out and the orbs come to rest.
     * <p>
     * The end-to-end case: no test-only call into the block, no hand-built block state. If this passes
     * and the direct-call tests below pass, the wiring between them is the only thing left untested.
     */
    @GameTest(maxTicks = 1200)
    public void aMobPushingThroughShedsOrbsOntoTheGround(GameTestHelper helper) {
        plant(helper);
        final Zombie walker = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, STEM);

        // A NoAI mob never travels, so it is walked across the stalk by hand. It has to leave the block
        // and come back rather than shuffle about inside it: the wisp rolls on arrival, so a mob that
        // never crosses the boundary never disturbs anything - which is the behaviour
        // aMobLingeringInsideNeverTriggersAgain asserts from the other side.
        final double[] x = {walker.getX()};
        final double[] step = {PATROL_STEP};
        final double centre = walker.getX();
        helper.onEachTick(() -> {
            if (Math.abs(x[0] - centre) > PATROL_REACH) {
                step[0] = -step[0];
            }
            // Stamping the old position first is what makes this a step rather than a teleport: the
            // server does exactly this at the top of every entity tick, and the wisp measures how far
            // something came from the gap between the two. Without it the zombie's old position stays
            // where it spawned, and the block sees one enormous stride that started inside the plant.
            walker.setOldPosAndRot();
            final Vec3 from = walker.position();
            x[0] += step[0];
            walker.setPos(x[0], from.y, from.z);
            walker.applyEffectsFromBlocks(from, walker.position());
        });

        helper.succeedWhen(() -> {
            final List<ExperienceOrb> orbs = orbs(helper);
            final List<String> failures = new ArrayList<>();
            if (orbs.isEmpty()) {
                failures.add("a mob pushed through the wisp for " + helper.getTick()
                        + " ticks and it never shed anything - mobs cannot trigger the payout");
            }
            for (ExperienceOrb orb : orbs) {
                if (!orb.onGround()) {
                    failures.add("an orb is still in the air at y=" + orb.getY() + " - not yet settled");
                }
                if (orb.getY() > helper.absolutePos(HEAD).getY()) {
                    failures.add("an orb came to rest above the head (y=" + orb.getY()
                            + ") instead of falling to the floor");
                }
            }
            failIfAny(helper, "Gloomwisp mob-triggered payout regression", failures);
        });
    }

    /**
     * Where the orbs actually appear, measured in the tick they are spawned in, before gravity has had
     * a chance to move them.
     * <p>
     * The stem is the segment disturbed here and the head is two blocks up, so "the orbs came off the
     * head" is a real assertion rather than a restatement of where the entity was.
     */
    @GameTest
    public void orbsAreScatteredAroundTheFootOfThePlant(GameTestHelper helper) {
        plant(helper);
        disturb(helper, STEM, ROLLS);

        final List<ExperienceOrb> orbs = orbs(helper);
        final List<String> failures = new ArrayList<>();
        if (orbs.isEmpty()) {
            failures.add("nothing was shed in " + ROLLS + " disturbances");
        }

        final Vec3 stalk = Vec3.atCenterOf(helper.absolutePos(HEAD));
        int total = 0;
        for (ExperienceOrb orb : orbs) {
            total += orb.getValue();
            if (orb.getValue() != ORB_VALUE) {
                failures.add("an orb is worth " + orb.getValue() + ", expected " + ORB_VALUE
                        + " - the payout was not split into separate orbs");
            }
            final double distance = Math.sqrt(
                    Math.pow(orb.getX() - stalk.x, 2) + Math.pow(orb.getZ() - stalk.z, 2)
            );
            if (distance > MAX_SCATTER) {
                failures.add("an orb spawned " + distance + " blocks from the stalk, further than the "
                        + MAX_SCATTER + " the scatter allows");
            }
            // The head, not the stem that was actually brushed: SHED_HEIGHT (0.4) above the head's origin.
            final double expectedY = helper.absolutePos(HEAD).getY() + 0.4;
            if (Math.abs(orb.getY() - expectedY) > 0.1) {
                failures.add("an orb spawned at y=" + orb.getY() + " rather than at the head (" + expectedY
                        + ") - it is coming off the disturbed segment instead");
            }
        }
        if (!orbs.isEmpty() && (total < MIN_PAYOUT || total > MAX_PAYOUT)) {
            failures.add("the payout was " + total + " points, outside the documented "
                    + MIN_PAYOUT + ".." + MAX_PAYOUT);
        }
        // Two orbs on top of each other would mean one spawn point used for all of them.
        if (orbs.size() > 1 && orbs.stream().map(o -> o.getX() + "/" + o.getZ()).distinct().count() == 1) {
            failures.add("all " + orbs.size() + " orbs spawned at the same spot - they are not scattered");
        }

        failIfAny(helper, "Gloomwisp orb scatter regression", failures);
        helper.succeed();
    }

    /**
     * The rate limit: one payout per plant, then nothing until the recharge tick lands.
     * <p>
     * Both segments are checked, and both are disturbed after the payout, because the cooldown is per
     * plant - a stalk that only marked the segment that was brushed would pay out again from the one
     * beside it, which for a walker clipping two segments at once is no cooldown at all.
     */
    @GameTest
    public void aPlantPaysOnceAndThenHasToRecharge(GameTestHelper helper) {
        plant(helper);
        disturb(helper, STEM, ROLLS);

        final List<String> failures = new ArrayList<>();
        final int paid = orbs(helper).size();
        if (paid == 0) {
            failures.add("nothing was shed in " + ROLLS + " disturbances");
        }

        final ServerLevel level = helper.getLevel();
        for (BlockPos segment : List.of(STEM, HEAD)) {
            final BlockPos abs = helper.absolutePos(segment);
            if (!level.getBlockState(abs).getValue(BlockGloomwispVine.SPENT)) {
                failures.add(segment + " is not marked spent after the plant paid out");
            }
            if (!level.getBlockTicks().hasScheduledTick(abs, NetherVineBlocks.GLOOMWISP_VINE)) {
                failures.add(segment + " has no recharge tick booked - it would stay spent forever");
            }
        }

        // Both segments, so a second payout out of the untouched one would show up here.
        disturb(helper, STEM, ROLLS);
        disturb(helper, HEAD, ROLLS);
        if (orbs(helper).size() > paid) {
            failures.add("a spent plant paid out again: " + paid + " orbs became " + orbs(helper).size());
        }

        // The scheduled tick, run by hand rather than waited out - the delay is a minute or more.
        helper.tickBlock(STEM);
        helper.tickBlock(HEAD);
        for (BlockPos segment : List.of(STEM, HEAD)) {
            if (level.getBlockState(helper.absolutePos(segment)).getValue(BlockGloomwispVine.SPENT)) {
                failures.add(segment + " is still spent after its recharge tick");
            }
        }

        disturb(helper, STEM, ROLLS);
        if (orbs(helper).size() <= paid) {
            failures.add("a recharged plant never paid out again in " + ROLLS + " disturbances");
        }

        failIfAny(helper, "Gloomwisp recharge regression", failures);
        helper.succeed();
    }

    /**
     * A mob that is not going anywhere gets nothing - the same speed gate that stops a wisp ringing
     * under someone standing in it. It is what keeps a farm to mobs actually being moved through the
     * plants rather than one mob parked in one forever.
     */
    @GameTest
    public void aMobStandingStillShedsNothing(GameTestHelper helper) {
        plant(helper);
        final Zombie idler = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, STEM);
        // A freshly spawned entity has not ticked, so its old position is not its position yet.
        idler.setOldPosAndRot();

        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(STEM);
        for (int i = 0; i < ROLLS; i++) {
            level.getBlockState(abs).entityInside(level, abs, idler, InsideBlockEffectApplier.NOOP, true);
        }

        final List<String> failures = new ArrayList<>();
        if (!orbs(helper).isEmpty()) {
            failures.add("a motionless mob shook " + orbs(helper).size() + " orbs out of the wisp");
        }
        failIfAny(helper, "Gloomwisp movement-gate regression", failures);
        helper.succeed();
    }

    /**
     * The gamerule still turns the whole thing off - including, importantly, the cooldown: a plant
     * that "paid out" nothing must not come back spent, or turning the rule off would leave a field
     * of drained wisps behind it.
     */
    @GameTest
    public void theGameRuleTurnsThePayoutOff(GameTestHelper helper) {
        plant(helper);
        final ServerLevel level = helper.getLevel();
        final var rules = level.getGameRules();
        final List<String> failures = new ArrayList<>();

        rules.set(NetherGameRules.GLOOMWISP_DROPS_EXPERIENCE, false, level.getServer());
        try {
            disturb(helper, STEM, ROLLS);
            if (!orbs(helper).isEmpty()) {
                failures.add("gloomwisp_drops_experience is off and the wisp still shed "
                        + orbs(helper).size() + " orbs");
            }
            if (level.getBlockState(helper.absolutePos(STEM)).getValue(BlockGloomwispVine.SPENT)) {
                failures.add("a wisp that shed nothing was still marked spent");
            }
        } finally {
            rules.set(NetherGameRules.GLOOMWISP_DROPS_EXPERIENCE, true, level.getServer());
        }

        failIfAny(helper, "Gloomwisp gamerule regression", failures);
        helper.succeed();
    }

    /**
     * The scenario a stand of wisps is actually planted into: a herd of ordinary animals with their own
     * AI, walking across the plants of their own accord, and the plants going dark behind them.
     * <p>
     * Everything above drives the block from the side - {@code entityInside} called by hand, or a mob
     * shoved back and forth. This one only spawns cows and points their navigation at the far wall, and
     * asserts on {@code SPENT}, the flag {@code animateTick} reads to decide whether the head still
     * smokes. That makes it the test for "the wisps in my cow pen never stop smoking", which is a
     * complaint about the visible state of the plant rather than about experience.
     */
    @GameTest(maxTicks = 1200)
    public void aHerdOfCowsDrainsThePlantsItWalksThrough(GameTestHelper helper) {
        arena(helper);
        for (BlockPos ground : HERD_PLANTS) {
            plantAt(helper, ground);
        }

        final List<Cow> herd = new ArrayList<>();
        for (int i = 0; i < HERD_SIZE; i++) {
            herd.add(helper.spawn(EntityTypes.COW, new BlockPos(1, 2, 2 + (i % 4))));
        }

        // Cows left to their own devices mostly stand around, so each one is sent to whichever end of
        // the pen it is not at. The plants are in the middle, so every crossing walks through them.
        helper.onEachTick(() -> {
            for (Cow cow : herd) {
                if (cow.getNavigation().isDone()) {
                    final BlockPos target = cow.getX() < helper.absolutePos(HERD_WEST).getX() + 4
                            ? HERD_EAST
                            : HERD_WEST;
                    final Vec3 to = Vec3.atCenterOf(helper.absolutePos(target));
                    cow.getNavigation().moveTo(to.x, to.y, to.z, 1.2);
                }
            }
        });

        helper.succeedWhen(() -> {
            final long drained = HERD_PLANTS.stream()
                                            .filter(ground -> helper.getLevel()
                                                                    .getBlockState(helper.absolutePos(ground.above(2)))
                                                                    .getValue(BlockGloomwispVine.SPENT))
                                            .count();
            if (drained == 0) {
                throw helper.assertionException(Component.literal(
                        "Gloomwisp herd regression:\n - " + HERD_SIZE + " cows walked over "
                                + HERD_PLANTS.size() + " wisps for " + helper.getTick()
                                + " ticks and not one head went dark - livestock never drains a plant,"
                                + " so a stand in a pen would smoke forever"
                ));
            }
        });
    }

    /**
     * Being in a wisp is not the same as arriving in one: a mob that is already standing in a plant and
     * only shifting about never sets it off again, however long it stays.
     * <p>
     * The rule this asserts is that the roll happens on arrival. Without it a plant under a herd is
     * worked once per animal <em>per tick</em> rather than once per animal that walks into it, and a
     * cow that has parked in a wisp is worth more to a farm than one that walks through - which is
     * backwards, and is what made a pen full of standing cows into the best possible use of the plant.
     */
    @GameTest
    public void aMobLingeringInsideNeverTriggersAgain(GameTestHelper helper) {
        plant(helper);
        final Zombie loiterer = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, STEM);
        linger(helper, STEM, loiterer, ROLLS);

        final List<String> failures = new ArrayList<>();
        if (!orbs(helper).isEmpty()) {
            failures.add("a mob that never left the plant shook " + orbs(helper).size()
                    + " orbs out of it in " + ROLLS + " ticks of milling about inside");
        }
        if (helper.getLevel().getBlockState(helper.absolutePos(STEM)).getValue(BlockGloomwispVine.SPENT)) {
            failures.add("a mob that never left the plant still managed to drain it");
        }

        // Control: the identical entity, identical block, one thing changed - it came from outside.
        push(helper, STEM, loiterer, ROLLS);
        if (orbs(helper).isEmpty()) {
            failures.add("control: the same mob arriving from outside shed nothing either, so this test"
                    + " cannot tell arriving from lingering and the negative above proves nothing");
        }

        failIfAny(helper, "Gloomwisp arrival-trigger regression", failures);
        helper.succeed();
    }

    /**
     * The specific case that follows from it: a plant that recharges under an animal already standing
     * in it does not treat that animal as having just walked in.
     * <p>
     * Worth its own test rather than leaning on the one above, because this is where a level-triggered
     * plant would leak: the recharge lands, the cow is still there and still shuffling, and the plant
     * pays out again immediately - a farm that needs no mob movement at all, only mobs.
     */
    @GameTest
    public void aPlantRechargingUnderAMobIsNotRetriggeredByIt(GameTestHelper helper) {
        plant(helper);
        final Zombie resident = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, STEM);

        push(helper, STEM, resident, ROLLS);
        final int paid = orbs(helper).size();
        final List<String> failures = new ArrayList<>();
        if (paid == 0) {
            failures.add("the plant never paid out, so there is no recharge to observe");
        }

        // The recharge lands while it is still standing there.
        helper.tickBlock(STEM);
        helper.tickBlock(HEAD);
        if (helper.getLevel().getBlockState(helper.absolutePos(STEM)).getValue(BlockGloomwispVine.SPENT)) {
            failures.add("the recharge tick did not wake the plant back up");
        }

        linger(helper, STEM, resident, ROLLS);
        if (orbs(helper).size() > paid) {
            failures.add("a recharged plant paid out again to the mob that was already standing in it: "
                    + paid + " orbs became " + orbs(helper).size());
        }

        failIfAny(helper, "Gloomwisp recharge-retrigger regression", failures);
        helper.succeed();
    }

    /**
     * A drained wisp does not react to being pushed through - the half of {@code SPENT} that is not
     * about experience at all.
     * <p>
     * This is the test for the plant that would not stop smoking in a cow pen. The ash in
     * {@code animateTick} was already gated on {@code SPENT}, but the souls {@code entityInside} sheds
     * were not, and a plant with livestock walking over it is disturbed on very nearly every tick - so
     * a stand that was in fact spent ninety percent of the time never stopped emitting.
     * <p>
     * The particles cannot be observed from a headless server (they are broadcast down player
     * connections there is no way to read here), so the assertion rides on the {@code disturbed_wisp}
     * advancement instead: it sits on the same early return as the souls and the chime, so a spent
     * plant that still awards it is a spent plant that is still emitting. The fresh-plant control in
     * the same test is what makes the negative mean anything.
     */
    @GameTest
    public void aDrainedPlantDoesNotReactAtAll(GameTestHelper helper) {
        plant(helper);
        final AdvancementHolder disturbed = helper.getLevel()
                                                  .getServer()
                                                  .getAdvancements()
                                                  .get(Identifier.parse("betternether:disturbed_wisp"));
        final List<String> failures = new ArrayList<>();
        if (disturbed == null) {
            failures.add("betternether:disturbed_wisp is not a loaded advancement");
            failIfAny(helper, "Gloomwisp dormancy regression", failures);
        }

        // Control: a fresh plant does react, through the very same call the negative below uses.
        final ServerPlayer onFresh = MockPlayers.inLevel(helper, STEM);
        push(helper, STEM, onFresh, 1);
        if (!onFresh.getAdvancements().getOrStartProgress(disturbed).isDone()) {
            failures.add("control: pushing through a fresh wisp did not award disturbed_wisp -"
                    + " this test cannot observe the plant reacting, so the negative below proves nothing");
        }

        // Drain it the same way anything else does, then push through the dead plant.
        disturb(helper, STEM, ROLLS);
        if (!helper.getLevel().getBlockState(helper.absolutePos(STEM)).getValue(BlockGloomwispVine.SPENT)) {
            failures.add("the plant never paid out in " + ROLLS + " disturbances, so it is not spent"
                    + " and the dormancy check below would be vacuous");
        }

        final ServerPlayer onSpent = MockPlayers.inLevel(helper, STEM);
        push(helper, STEM, onSpent, ROLLS);
        if (onSpent.getAdvancements().getOrStartProgress(disturbed).isDone()) {
            failures.add("a spent wisp still reacted to " + ROLLS + " pushes - it is not dormant,"
                    + " so it goes on shedding souls and chiming while its head is dark");
        }

        failIfAny(helper, "Gloomwisp dormancy regression", failures);
        helper.succeed();
    }

    /**
     * A two-segment wisp on a patch of netherrack: a stem the mob stands in and the head above it.
     * <p>
     * {@code OFFSET} is cleared so the stalk sits dead centre on its block - see {@link #MAX_SCATTER}.
     * The floor is not decoration: without ground the wisp fails {@code canSurvive} and the first
     * neighbour update deletes it.
     */
    private static void plant(GameTestHelper helper) {
        for (int x = -FLOOR_RADIUS; x <= FLOOR_RADIUS; x++) {
            for (int z = -FLOOR_RADIUS; z <= FLOOR_RADIUS; z++) {
                helper.setBlock(GROUND.offset(x, 0, z), Blocks.NETHERRACK);
            }
        }
        plantAt(helper, GROUND);
    }

    /** A stem and a head on top of the given ground block. */
    private static void plantAt(GameTestHelper helper, BlockPos ground) {
        final BlockState base = NetherVineBlocks.GLOOMWISP_VINE
                .defaultBlockState()
                .setValue(BlockGloomwispVine.OFFSET, false);
        helper.setBlock(ground.above(), base.setValue(BlockGloomwispVine.SHAPE, BlockProperties.TripleShape.MIDDLE));
        helper.setBlock(ground.above(2), base.setValue(BlockGloomwispVine.SHAPE, BlockProperties.TripleShape.TOP));
    }

    /** A floored pen for the herd to walk up and down. */
    private static void arena(GameTestHelper helper) {
        for (int x = 0; x <= 7; x++) {
            for (int z = 0; z <= 7; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.NETHERRACK);
            }
        }
    }

    /**
     * Pushes a mob through one segment {@code times} over, in a single tick.
     * <p>
     * Goes through {@code BlockState#entityInside}, the same call {@code Entity#checkInsideBlocks}
     * makes, so everything under test still runs; what it skips is waiting out the thousands of ticks
     * a 0.005 chance needs. The mob is moved once and never ticked again, which leaves its old
     * position where it spawned and so keeps the speed gate open for every call.
     */
    private static void disturb(GameTestHelper helper, BlockPos segment, int times) {
        final Zombie walker = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, segment);
        push(helper, segment, walker, times);
        walker.discard();
    }

    /**
     * The same push, by a given entity - used where the disturber has to be a player, because what is
     * being read afterwards is an advancement.
     * <p>
     * The entity is placed in the segment having apparently just covered {@link #ARRIVAL} blocks, so
     * every call reads as a fresh arrival - which is what the wisp rolls on. {@link #linger} is the
     * counterpart that has it already inside instead.
     */
    private static void push(GameTestHelper helper, BlockPos segment, LivingEntity pusher, int times) {
        move(helper, segment, pusher, ARRIVAL);
        repeat(helper, segment, pusher, times);
    }

    /**
     * The same, for something that was already standing in the plant: it has moved far enough to clear
     * the speed gate, but not far enough to have come from outside the block.
     */
    private static void linger(GameTestHelper helper, BlockPos segment, LivingEntity pusher, int times) {
        move(helper, segment, pusher, LINGER);
        repeat(helper, segment, pusher, times);
    }

    /**
     * Parks the entity in the middle of the segment and tells it how far it travelled to get there.
     * <p>
     * Which of the two ways of saying that lands depends on the entity: a mob's movement is read off the
     * server's own old position, but a player is client-authoritative and the wisp reads
     * {@code getKnownMovement} - what the client last reported - instead. A mock player has no client,
     * so its known movement stays zero and it counts as standing perfectly still wherever it is put.
     */
    private static void move(GameTestHelper helper, BlockPos segment, LivingEntity pusher, double travelled) {
        final Vec3 to = Vec3.atCenterOf(helper.absolutePos(segment)).add(0.0, -0.5, 0.0);
        pusher.setOldPosAndRot(to.subtract(travelled, 0.0, 0.0), pusher.getYRot(), pusher.getXRot());
        pusher.setPos(to);
        if (pusher instanceof ServerPlayer player) {
            player.setKnownMovement(new Vec3(travelled, 0.0, 0.0));
        }
    }

    private static void repeat(GameTestHelper helper, BlockPos segment, LivingEntity pusher, int times) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(segment);
        for (int i = 0; i < times; i++) {
            level.getBlockState(abs).entityInside(level, abs, pusher, InsideBlockEffectApplier.NOOP, true);
        }
    }

    private static List<ExperienceOrb> orbs(GameTestHelper helper) {
        return helper.getLevel().getEntitiesOfClass(
                ExperienceOrb.class,
                new AABB(helper.absolutePos(GROUND)).inflate(8.0)
        );
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
