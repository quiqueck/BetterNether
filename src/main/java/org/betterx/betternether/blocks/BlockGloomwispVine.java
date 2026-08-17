package org.betterx.betternether.blocks;

import de.ambertation.wover.block.api.BlockProperties;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnSolidTrait;
import org.betterx.bclib.util.LootUtil;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.mixin.common.BlockBehaviourPropertiesAccessor;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.SoundsRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A gloomwisp: a thin stalk carrying a single elongated, faintly glowing head.
 * <p>
 * Structurally this is the same upward-stacking plant as {@link BlockNetherCactus} - a column of
 * stem segments topped by a head, growing on random ticks - so it is modelled on that rather than on
 * {@link BlockNetherReed}, whose canSurvive hardcodes reed's "must have lava beside the ground block"
 * rule. The ground it accepts comes from the survival traits on the registration instead of being baked
 * in here - a {@link SurvivesOnSolidTrait} there, so a dug-up wisp replants on anything solid. It
 * dispatches over {@link #SHAPE} rather than a plain top/not-top boolean because the two stalk segments
 * carry different textures - see that field.
 */
public class BlockGloomwispVine extends Block {
    /**
     * How tall a <em>placed</em> wisp may be, counted in blocks <b>including the head</b> - so 6 here is
     * five stem segments under one head. Worldgen places them anywhere from {@link #MIN_HEIGHT} to this,
     * so a stand of wisps is ragged rather than a row of identical stalks, and a player stacking segments
     * by hand is not limited at all.
     */
    public static final int MAX_HEIGHT = 6;

    /**
     * How tall a wisp may get by growing on its own - see {@link #randomTick}. Counted the same way as
     * {@link #MAX_HEIGHT}, so 4 is three stem segments under one head.
     * <p>
     * Lower than {@link #MAX_HEIGHT} on purpose. The tall wisps are meant to be something the world put
     * there, not something any wisp becomes if left alone long enough: with one cap for both, every stand
     * eventually levelled out at the maximum and the raggedness worldgen went to the trouble of creating
     * was gone a few thousand ticks later.
     */
    public static final int MAX_NATURAL_HEIGHT = 4;

    /**
     * One random tick in this many adds a segment - see {@link #randomTick}.
     * <p>
     * A wisp is scenery, and the whole point of {@link #MAX_NATURAL_HEIGHT} is that a stand should keep
     * the shape worldgen gave it rather than converging on one height. A slow climb to that cap is what
     * makes the difference visible for any length of time; at the old one-in-sixteen a short wisp was at
     * its limit within an hour of the chunk being loaded.
     * <p>
     * A single block is random-ticked about every 68 seconds at the default {@code randomTickSpeed} of 3
     * ({@code 4096 / 3} ticks), so this is roughly 36 minutes per segment and a little under two hours
     * for a bare head to reach the cap - all of it in loaded, ticking chunks. The relationship is linear
     * if it wants retuning.
     */
    private static final int GROW_CHANCE = 32;

    /**
     * The shortest a wisp can be: just the head, sitting on the ground.
     */
    public static final int MIN_HEIGHT = 1;

    /**
     * Motes of smoke dropped per animate tick.
     * <p>
     * Not a 1-in-N chance, which is what this started as and why nothing showed. The client only calls
     * animateTick on a given block about 0.4 times a second (667 random samples per tick over a 32-block
     * cube), so gating it 1-in-8 on top of that came to one mote every twenty seconds. The cinders on
     * molten gloomsculk get away with a gate because a patch has dozens of exposed blocks feeding it; a
     * wisp is one block, so it has to emit on every call.
     */
    private static final int SMOKE_MOTES = 3;

    /**
     * Souls puffed off the head when it is sheared.
     */
    private static final int SHEAR_PARTICLES = 12;

    /**
     * Souls shaken loose when something arrives in the wisp.
     * <p>
     * A puff on entry, not a trickle per tick - see {@link #entityInside}. Higher than the two this was
     * while it fired every tick: a walker used to accumulate its souls over the several ticks it took to
     * clear a plant, and the same crossing now has to read off two entries (the stem and the head).
     */
    private static final int DISTURB_PARTICLES = 6;

    /**
     * Squared horizontal speed below which a wisp is left alone, mirroring the sweet berry bush's
     * per-axis 0.003.
     * <p>
     * The cheaper half of the "did something arrive" test in {@link #entityInside} - the entry check
     * there is what actually rules out standing still, and this rules out the case that check cannot
     * see: something creeping across the block boundary a hundredth at a time, which would otherwise
     * read as a fresh arrival on every tick it drifted back and forth over the edge.
     */
    private static final double DISTURB_SPEED_SQR = 9.0E-6;

    /**
     * Slack on the rewound box in {@link #entityInside}, so that a previous position flush against the
     * block face counts as having been outside it.
     * <p>
     * Something walking a grid-aligned line in even steps arrives exactly on the boundary, and whether
     * the rewound box lands a hair inside or a hair outside is then decided by accumulated float error
     * in the position - which is to say, arbitrarily, but consistently for any one walker. Without this
     * such a walker can cross a plant indefinitely and never once register as having arrived. Vanilla
     * deflates by {@code 1.0E-5} against the same hazard when it decides which blocks an entity is
     * inside; this is an order larger, and still far below the {@link #DISTURB_SPEED_SQR} floor, so it
     * cannot turn standing still into a stream of arrivals.
     */
    private static final double BOUNDARY_EPSILON = 1.0E-4;

    /**
     * Chance that a wisp chimes when something arrives in one of its segments.
     * <p>
     * Reads high next to the 0.08 this was, and is the same thing: that was a per-tick roll, and a
     * walker was inside a given segment for about seven ticks - fifteen rolls across the two segments
     * it clips, which came to a little over one chime per plant crossed. Two entry rolls at a half do
     * the same, so a stand still rings an occasional note rather than a peal.
     */
    private static final float CHIME_CHANCE = 0.5F;

    /** Quiet: a wisp is something you brush past, not something you knock over. */
    private static final float CHIME_VOLUME = 0.35F;

    /**
     * The band a chime's pitch is drawn from.
     * <p>
     * The sample is a single windchime rod struck once, so the repitching is what turns that one note
     * back into a set - a stand of wisps rings a spread of pitches instead of the same note over and
     * over, which is what one sample played flat sounds like.
     */
    private static final float CHIME_PITCH_MIN = 0.75F;
    private static final float CHIME_PITCH_SPREAD = 0.6F;

    /**
     * Louder than the chime, because it is the rare outcome and wants to be noticed - but still on the quiet
     * side of a block sound, since the wisp is right on top of whoever set it off.
     */
    private static final float BOON_VOLUME = 0.5F;

    /**
     * A much narrower band than the chime's.
     * <p>
     * The boon sample is a voice, and a voice does not survive the treatment a struck note does: the chime's
     * band works because repitching one rod just makes a different rod, while the same spread on a laugh
     * walks it from a small child to something adult and back. This band is only wide enough to keep two
     * payouts in a row from being audibly the same take.
     */
    private static final float BOON_PITCH_MIN = 0.92F;
    private static final float BOON_PITCH_SPREAD = 0.16F;

    /**
     * Chance that a wisp sheds experience when something arrives in one of its segments.
     * <p>
     * Fourteen times rarer than the chime, and rolled independently of it. Crossing a plant is two of
     * these rolls, so about one crossing in fifteen pays - a single wisp almost never does and walking
     * a whole stand occasionally does, which is the intent: a thing you notice, not a thing you farm.
     * Anyone who does want to farm it can, which is what {@link #RECHARGE_TICKS} sets the ceiling on
     * and the game rule turns off entirely.
     * <p>
     * Set to leave a crossing worth exactly what it was worth as a per-tick roll: seven ticks in a
     * segment at the old 0.005, two segments deep, came to 6.8% a plant, and two entries at this come
     * to 6.9%. What the move to entry-rolling changed is not what a walker gets but what standing
     * still gets, which is now nothing.
     */
    private static final float EXPERIENCE_CHANCE = 0.035F;

    /**
     * Total experience per payout, in the same one-to-three band as a smelted ore. Shed as that many
     * separate one-point orbs rather than one orb worth the lot - see {@link #shedExperience}.
     */
    private static final int EXPERIENCE_MIN = 1;
    private static final int EXPERIENCE_SPREAD = 3;

    /**
     * How long a wisp stays drained after paying out, in ticks, and the extra it may draw on top -
     * so one to two minutes, per plant, rolled fresh each time.
     * <p>
     * This is what makes the drip farmable without making it a fountain. The roll above is per tick,
     * so anything that keeps an entity moving through a wisp indefinitely - a water stream, a mob
     * pacing its pen - hits {@link #EXPERIENCE_CHANCE} about every ten seconds, and without a
     * recharge a field of wisps would pay out at whatever rate the builder could push mobs through
     * it. With it, a plant is worth at most {@link #EXPERIENCE_MIN}..{@code MIN + SPREAD - 1} points a
     * minute no matter how hard it is worked, so the yield scales with how many wisps were planted
     * rather than with traffic - which is a farm you build once, not one you idle in.
     * <p>
     * The spread is there so a stand disturbed all at once does not come back all at once and start
     * ringing in lockstep.
     * <p>
     * A player crossing a stand is barely touched by this: they are inside any one plant for a few
     * ticks, so the odds of coming back to the same wisp inside a minute <em>and</em> winning the
     * roll again are remote. It binds farms, not walkers.
     */
    private static final int RECHARGE_TICKS = 1200;
    private static final int RECHARGE_SPREAD_TICKS = 1200;

    /**
     * Horizontal radius, in blocks, that shed orbs are scattered over.
     * <p>
     * Slightly wider than the head, so the orbs land in a patch around the foot of the plant rather
     * than in the one spot the stalk occupies - the same read as ore experience popping out of the
     * block you just broke.
     */
    private static final double SCATTER_RADIUS = 0.75;

    /**
     * Height above the head's block origin that orbs are shaken loose at - just inside the hem, where
     * the ash falls out, so they are seen to come off the head and drop past the stalk to the floor.
     */
    private static final double SHED_HEIGHT = 0.4;

    private static final VoxelShape STEM_SHAPE = box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape HEAD_SHAPE = box(3, 0, 3, 13, 15, 13);

    /**
     * TOP is the head, MIDDLE the stalk directly beneath it, BOTTOM any stalk below that. The two stalk
     * shapes carry different textures: the pale-to-dark gradient is split across them, so a full-height
     * wisp reads as one gradient running from the head down to the ground rather than repeating.
     */
    public static final EnumProperty<BlockProperties.TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    /**
     * Set by shearing the head: a sheared wisp stops growing.
     * <p>
     * Vanilla's own property rather than a bespoke one, both because it already means "leave this
     * alone" and because the blockstate keys only on {@link #SHAPE} - an unlisted property is a
     * wildcard there, so this costs no extra model variants.
     */
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    /**
     * Which way the head looks. Dispatched on by the model, so every value costs blockstate variants.
     */
    public static final EnumProperty<WispRotation> ROTATION = EnumProperty.create("rotation", WispRotation.class);

    /**
     * Whether this wisp takes the random horizontal offset that keeps a stand off a visible grid.
     * <p>
     * Unlisted in the blockstate, like {@link #PERSISTENT}, so it costs no model variants - it feeds the offset
     * function rather than the model. Cleared by placing while sneaking, which parks the wisp dead centre on its
     * block for anyone laying them out deliberately.
     */
    public static final BooleanProperty OFFSET = BooleanProperty.create("offset");

    /**
     * Whether this wisp has already paid out and is still recharging - see {@link #RECHARGE_TICKS}.
     * <p>
     * Set on every segment of the plant at once and cleared by a scheduled tick per segment, so the
     * cooldown is per <em>plant</em>: a six-block stalk is one wisp and pays like one, rather than
     * six times over because a walker clips several of its segments.
     * <p>
     * A blockstate flag rather than a table of positions kept on the side, because that is the one
     * place a per-block cooldown survives the chunk unloading with the scheduled tick that ends it -
     * and it can be read straight off the state {@code entityInside} was handed, which matters for
     * something that runs every tick for every entity in the plant.
     * <p>
     * Unlike {@link #PERSISTENT} this one is dispatched on, so it does cost model variants: a spent
     * head shuts its eyes. That is the closed frame of the blink strip, cut out as its own static
     * sprite, because texture animation runs per sprite and there is no way to hold one block's
     * animation on a frame - see {@code NetherModels.gloomwispVineModelTrait}. The stalk segments
     * carry the flag too (the cooldown is per plant) but look no different for it.
     */
    public static final BooleanProperty SPENT = BooleanProperty.create("spent");

    public BlockGloomwispVine(Properties settings) {
        super(stateAwareOffset(settings));
        this.registerDefaultState(getStateDefinition()
                .any()
                .setValue(SHAPE, BlockProperties.TripleShape.TOP)
                .setValue(PERSISTENT, false)
                .setValue(ROTATION, WispRotation.RANDOM)
                .setValue(OFFSET, true)
                .setValue(SPENT, false));
    }

    /**
     * Makes the block's XZ offset conditional on {@link #OFFSET}.
     * <p>
     * Wraps whatever function the registration already installed rather than restating vanilla's position hashing,
     * so an offset wisp keeps exactly the jitter it had before this property existed. Runs on the properties on
     * the way into {@code super}, which is the last moment before {@code BlockStateBase} copies the function out
     * of them and the setter stops having any effect.
     */
    private static Properties stateAwareOffset(Properties settings) {
        final var accessor = (BlockBehaviourPropertiesAccessor) settings;
        final BlockBehaviour.OffsetFunction jitter = accessor.betternether$getOffsetFunction();
        if (jitter != null) {
            accessor.betternether$setOffsetFunction(
                    (state, pos) -> state.getValue(OFFSET) ? jitter.evaluate(state, pos) : Vec3.ZERO
            );
        }
        return settings;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE, PERSISTENT, ROTATION, OFFSET, SPENT);
    }

    /**
     * A hand-placed wisp looks back at whoever placed it and, unless they were sneaking, is jittered.
     * <p>
     * Both of these only ever get written here. Everything else that puts a wisp in the world - worldgen, a wisp
     * growing another segment, {@code /setblock} - goes through the default state and so keeps the random
     * rotation and the offset, which is the behaviour the plant had before it could be aimed at all.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        return state
                .setValue(ROTATION, WispRotation.forHorizontal(context.getHorizontalDirection()))
                .setValue(OFFSET, !context.isSecondaryUseActive());
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        // the block carries OffsetType.XZ, so the shape has to travel with the model or the outline
        // sits beside the wisp you are looking at
        final Vec3 offset = state.getOffset(pos);
        final VoxelShape shape = state.getValue(SHAPE) == BlockProperties.TripleShape.TOP
                ? HEAD_SHAPE
                : STEM_SHAPE;
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canSurvive(state, world, pos))
            return Blocks.AIR.defaultBlockState();
        // Off the incoming state, not off defaultBlockState(): this runs on every neighbour update, and rebuilding
        // from the default would quietly reset a hand-placed wisp's aim and offset the first time anything near it
        // changed.
        return state.setValue(SHAPE, shapeAt(world, pos));
    }

    /**
     * TOP when nothing of this block sits above, MIDDLE when the head is directly above, BOTTOM otherwise -
     * so the segment right under the head always gets the bright half of the gradient.
     */
    private BlockProperties.TripleShape shapeAt(LevelReader world, BlockPos pos) {
        if (world.getBlockState(pos.above()).getBlock() != this)
            return BlockProperties.TripleShape.TOP;
        return world.getBlockState(pos.above(2)).getBlock() == this
                ? BlockProperties.TripleShape.BOTTOM
                : BlockProperties.TripleShape.MIDDLE;
    }

    /**
     * Shearing the head stops the wisp growing any taller.
     * <p>
     * The flag is set on every segment of the plant, not just the one clicked, so a wisp that is later
     * broken back to a stump stays sheared rather than quietly resuming.
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!LootUtil.isShear(stack) || state.getValue(PERSISTENT)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        if (!level.isClientSide()) {
            for (BlockPos p = base(level, pos); level.getBlockState(p).getBlock() == this; p = p.above()) {
                level.setBlock(p, level.getBlockState(p).setValue(PERSISTENT, true), BlocksHelper.SET_SILENT);
            }
            stack.hurtAndBreak(1, player, hand);
            // A puff of souls off the head, so the shear reads as having done something. Sent from the
            // server rather than spawned locally: useItemOn runs on both sides, and a client-side
            // addParticle would only ever be seen by the player holding the shears.
            if (level instanceof ServerLevel server) {
                final Vec3 offset = state.getOffset(pos);
                server.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        pos.getX() + offset.x + 0.5,
                        pos.getY() + offset.y + 0.6,
                        pos.getZ() + offset.z + 0.5,
                        SHEAR_PARTICLES,
                        0.18, 0.22, 0.18, 0.0
                );
            }
        }
        level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    /**
     * Souls and a chime when something pushes through the wisp - or, on the rare tick the wisp pays out,
     * souls and the boon run instead of the chime.
     * <p>
     * Server-side only, and the sound goes out with a null "except" so the walker hears their own
     * chime too - unlike the shear above, which is predicted locally by the player holding the shears
     * and so excludes them.
     */
    @Override
    public void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier insideBlockEffectApplier,
            boolean isPrecise
    ) {
        if (!(level instanceof ServerLevel server) || !(entity instanceof LivingEntity living)) {
            return;
        }

        // A drained wisp does not respond at all until its recharge lands - no souls, no chime, no
        // advancement, no payout - which is the whole of what "spent" looks like from outside. Checked
        // first because it is a single property read, and in a pen it drops most of the traffic.
        if (state.getValue(SPENT)) {
            return;
        }

        // Where the entity went this tick, as a from->to vector. A player is client-authoritative and
        // its position is written straight out of the movement packet, so the server's own old position
        // is not a reliable "where it was" for one - the client's reported delta is.
        final Vec3 travelled = entity.isClientAuthoritative()
                ? entity.getKnownMovement()
                : entity.position().subtract(entity.oldPosition());
        if (travelled.horizontalDistanceSqr() < DISTURB_SPEED_SQR) {
            return;
        }

        // Entering the plant is the event, not being in it. Rewind the entity by the distance it covered
        // this tick: if it was already overlapping this block back there, it did not arrive, it stayed -
        // and a wisp someone is standing in has already given what it is going to give.
        // <p>
        // Level-triggering was wrong in two ways that only show up with livestock. A cow milling about
        // inside a plant re-rolled every single tick, so a plant under a herd was worked far harder than
        // one being walked through; and the moment a plant came back from its recharge the animal still
        // standing in it set it off again immediately, which is not a disturbance, just the same cow.
        // Rolling on arrival makes a plant worth the same whoever crosses it, and ties a farm's yield to
        // how many animals are pushed through rather than to how many are parked in it.
        if (entity.getBoundingBox().move(travelled.reverse()).deflate(BOUNDARY_EPSILON).intersects(new AABB(pos))) {
            return;
        }

        final RandomSource random = server.getRandom();
        // Offset applied for the same reason as the shear puff: the block carries a random XZ offset,
        // so particles spawned on the grid position would sit beside the plant rather than in it.
        final Vec3 offset = state.getOffset(pos);
        server.sendParticles(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + offset.x + 0.5,
                pos.getY() + offset.y + 0.5,
                pos.getZ() + offset.z + 0.5,
                DISTURB_PARTICLES,
                0.12, 0.16, 0.12, 0.0
        );

        if (entity instanceof ServerPlayer serverPlayer) {
            // Awarded on the disturbance rather than on the chime: the chime is a 1-in-12 gate, and an
            // advancement that only sometimes fires when you do the thing reads as broken.
            BNCriterion.DISTURBED_WISP.trigger(serverPlayer);
        }

        // Rolled before the chime, because a payout speaks in its own voice and silences the chime for that
        // tick - the two never overlap, so what you hear tells you which of the two happened.
        final boolean shed = shedExperience(server, state, pos, living, random);

        if (shed) {
            level.playSound(
                    null,
                    pos,
                    SoundsRegistry.BLOCK_GLOOMWISP_BOON.value(),
                    SoundSource.BLOCKS,
                    BOON_VOLUME,
                    BOON_PITCH_MIN + random.nextFloat() * BOON_PITCH_SPREAD
            );
        } else if (random.nextFloat() < CHIME_CHANCE) {
            level.playSound(
                    null,
                    pos,
                    SoundsRegistry.BLOCK_GLOOMWISP_CHIME.value(),
                    SoundSource.BLOCKS,
                    CHIME_VOLUME,
                    CHIME_PITCH_MIN + random.nextFloat() * CHIME_PITCH_SPREAD
            );
        }
    }

    /**
     * The occasional handful of orbs shaken out of a wisp's head.
     * <p>
     * Anything alive sets this off, not just players. A wisp does not know who is pushing through it,
     * and one rung by a piglin dropping nothing was the difference between a plant that can be farmed
     * and one that can only be walked through - which is what {@link #RECHARGE_TICKS} exists to keep
     * honest, since a mob can lean on a plant indefinitely and a player cannot. The advancement is
     * still players-only, fired from the same roll that produced the orbs so it cannot claim a payout
     * that never happened.
     * <p>
     * The orbs come off the head and fall to the floor in a patch around the plant rather than
     * appearing in one spot: a payout under a mob two rooms away should leave something lying on the
     * ground to come and collect, which is the whole of what makes it a farm.
     *
     * @param disturber whatever pushed through the plant; only used for the advancement.
     * @return whether orbs were actually shed, which is what the caller swaps the chime out on.
     */
    private boolean shedExperience(
            ServerLevel level,
            BlockState state,
            BlockPos pos,
            LivingEntity disturber,
            RandomSource random
    ) {
        // No SPENT check here: entityInside has already returned for a drained plant, and one guard for
        // the whole dormant state is what keeps the orbs from ever diverging from the souls and the chime.
        if (!level.getGameRules().get(NetherGameRules.GLOOMWISP_DROPS_EXPERIENCE)
                || random.nextFloat() >= EXPERIENCE_CHANCE) {
            return false;
        }

        // Off the head, whichever segment was actually brushed - the orbs are meant to be seen falling
        // out of the light. Every segment of a plant shares one XZ offset (vanilla's XZ offset function
        // hashes x and z only), so the disturbed segment's is the head's too.
        final Vec3 offset = state.getOffset(pos);
        final BlockPos head = head(level, pos);
        final double x = head.getX() + offset.x + 0.5;
        final double y = head.getY() + offset.y + SHED_HEIGHT;
        final double z = head.getZ() + offset.z + 0.5;

        final int amount = EXPERIENCE_MIN + random.nextInt(EXPERIENCE_SPREAD);
        for (int i = 0; i < amount; i++) {
            // Uniform over the disc rather than over the radius, so the orbs do not bunch up on the stalk.
            final double angle = random.nextDouble() * Math.PI * 2;
            final double distance = SCATTER_RADIUS * Math.sqrt(random.nextDouble());
            final Vec3 outward = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));

            // Built directly instead of through ExperienceOrb.award, which is what ore blocks use: award
            // packs the amount into as few orbs as vanilla's value tiers allow, so a three-point payout
            // through it is one orb worth three at one spot - the opposite of the handful this wants. It
            // would also roll to merge the result into any orb already lying within a block, which for a
            // scatter this tight is the same collapse a second time. The direction gives each orb a pop
            // away from the stalk before gravity takes it down; they do not re-merge on the ground,
            // because vanilla only merges orbs whose entity ids happen to be 40 apart.
            level.addFreshEntity(new ExperienceOrb(
                    level,
                    new Vec3(x + outward.x * distance, y, z + outward.z * distance),
                    outward,
                    1
            ));
        }

        drain(level, pos, random);
        if (disturber instanceof ServerPlayer player) {
            BNCriterion.WISP_SHED_EXPERIENCE.trigger(player);
        }
        return true;
    }

    /**
     * Marks the whole plant spent and books the tick that brings it back.
     * <p>
     * Every segment gets the flag and its own scheduled tick, on the same deadline: the flag has to be
     * on each of them because {@code entityInside} reads it off whichever segment was brushed, and the
     * tick has to be on each of them because a segment broken off in the meantime should not take the
     * rest of the plant's recharge with it.
     */
    private void drain(ServerLevel level, BlockPos pos, RandomSource random) {
        final int recharge = RECHARGE_TICKS + random.nextInt(RECHARGE_SPREAD_TICKS);
        for (BlockPos p = base(level, pos); level.getBlockState(p).getBlock() == this; p = p.above()) {
            level.setBlock(p, level.getBlockState(p).setValue(SPENT, true), BlocksHelper.SET_SILENT);
            level.scheduleTick(p, this, recharge);
        }
    }

    /**
     * The end of a recharge: the wisp lights back up and can pay out again.
     * <p>
     * The only scheduled tick this block books, so it needs no further guard - and if one arrives at a
     * wisp that is not spent (a segment broken and replaced inside the cooldown, say) clearing a flag
     * that is already clear is exactly the right thing to do.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(SPENT)) {
            level.setBlock(pos, state.setValue(SPENT, false), BlocksHelper.SET_SILENT);
        }
    }

    /**
     * The lowest segment of the wisp {@code pos} belongs to.
     */
    private BlockPos base(Level level, BlockPos pos) {
        BlockPos p = pos;
        while (level.getBlockState(p.below()).getBlock() == this) p = p.below();
        return p;
    }

    /**
     * The head of the wisp {@code pos} belongs to - the topmost segment, which is where the light and
     * the smoke are. Walked rather than read off {@link #SHAPE}, so it is right even for a stalk whose
     * shapes have not caught up with an update yet.
     */
    private BlockPos head(Level level, BlockPos pos) {
        BlockPos p = pos;
        while (level.getBlockState(p.above()).getBlock() == this) p = p.above();
        return p;
    }

    /**
     * On whatever ground the registration's traits accept - or on another wisp, which is how a stalk
     * carries the segments above it.
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos downPos = pos.below();
        BlockState down = world.getBlockState(downPos);
        return SurvivesOnBlockTrait.survivesOn(this, down)
                || SurvivesOnSolidTrait.survivesOn(this, world, downPos, down, Direction.UP)
                || down.getBlock() == this;
    }

    /**
     * Smoke falling out of the wisp's hem.
     * <p>
     * {@link ParticleTypes#ASH} rather than {@code SMOKE}: smoke is built with gravity {@code -0.1}, so
     * it climbs no matter what velocity it is handed, and would fight the effect the whole way. Ash has
     * gravity {@code +0.1}, a 20-tick life and no collision, which is what lets it sink past the stalk
     * and drift to the floor. Its provider also negates the y velocity it is given, hence the positive
     * value below for downward motion.
     * <p>
     * A wisp that has just paid out stops smoking until it recharges - one half of the visible dormancy,
     * the other being the closed eyes the model dispatch gives it (see {@link #SPENT}). Withholding the
     * ash costs nothing, since the flag is already on the state the client has; dimming the head instead
     * would mean a block light update on every payout, which on a field of wisps built to be farmed is a
     * steady drip of relighting for a cosmetic.
     */
    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(SHAPE) != BlockProperties.TripleShape.TOP || state.getValue(SPENT)) return;

        // the head is drawn offset with the rest of the block, so the smoke has to be too
        final Vec3 offset = state.getOffset(pos);
        for (int i = 0; i < SMOKE_MOTES; i++) {
            world.addParticle(
                    ParticleTypes.ASH,
                    pos.getX() + offset.x + 0.3 + random.nextDouble() * 0.4,
                    // just inside the hem, so it appears to fall out of the wisp rather than off its base
                    pos.getY() + offset.y + 0.1 + random.nextDouble() * 0.25,
                    pos.getZ() + offset.z + 0.3 + random.nextDouble() * 0.4,
                    0.0, 0.4 + random.nextDouble() * 0.4, 0.0
            );
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
            return;
        }
        if (state.getValue(PERSISTENT)) return;
        if (state.getValue(SHAPE) == BlockProperties.TripleShape.TOP && random.nextInt(GROW_CHANCE) == 0) {
            BlockPos up = pos.above();
            // getLengthDown counts this block too, so it is the wisp's total height including the head -
            // the same thing MAX_NATURAL_HEIGHT measures, and the growth stops with three stems under it.
            // MAX_NATURAL_HEIGHT rather than MAX_HEIGHT: a wisp that grew here stops there, while one the
            // world placed - or one a player stacked - keeps whatever height it was given.
            if (world.isEmptyBlock(up)
                    && BlocksHelper.getLengthDown(world, pos, this) < MAX_NATURAL_HEIGHT) {
                // The new head inherits this segment's aim and offset rather than taking the default. The offset
                // especially: a wisp whose segments disagreed about it would grow a stalk that stepped sideways
                // halfway up, since the offset moves the model and the outline with it.
                BlocksHelper.setWithUpdate(
                        world, up,
                        defaultBlockState()
                                .setValue(ROTATION, state.getValue(ROTATION))
                                .setValue(OFFSET, state.getValue(OFFSET))
                );
                // this segment is now under the head; anything below it drops to BOTTOM via updateShape
                BlocksHelper.setWithUpdate(
                        world, pos,
                        state.setValue(SHAPE, BlockProperties.TripleShape.MIDDLE)
                );
            }
        }
    }
}
