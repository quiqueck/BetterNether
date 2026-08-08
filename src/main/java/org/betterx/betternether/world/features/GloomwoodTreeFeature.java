package org.betterx.betternether.world.features;

import org.betterx.betternether.registry.block.NetherLeavesBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.world.features.configs.GloomwoodTreeConfiguration;
import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;
import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.build.TreeBuilder;
import org.betterx.betternether.world.tree.build.TreeVoxels;
import org.betterx.betternether.world.tree.canopy.GhostCanopy;
import org.betterx.betternether.world.tree.decay.DecayRepair;
import org.betterx.betternether.world.tree.math.Crackle;
import org.betterx.betternether.world.tree.palette.LeafMarks;
import org.betterx.betternether.world.tree.palette.LogBands;
import org.betterx.betternether.world.tree.palette.TreePalette;
import org.betterx.betternether.world.tree.skeleton.ForkingTrunk;
import de.ambertation.wover.feature.api.features.GrowableFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The gloomwood tree: a crackled, forking trunk that shades from the dark lower wood through the
 * transition log into the pale crown, carrying hooded canopies with bleached "eyes".
 *
 * <h2>Built on the tree library</h2>
 * Everything about the shape lives in {@link org.betterx.betternether.world.tree}; this class only
 * chooses the numbers and the blocks. That is deliberate - the library is meant to move to WorldWeaver,
 * and the amount of gloomwood-specific code here is the measure of whether the split is in the right
 * place.
 *
 * <h2>Leaf decay</h2>
 * Unlike the other BetterNether trees, this one places leaves that genuinely decay: the canopy is solved
 * before it is written, so every leaf is inside the vanilla budget, and the arms are aimed <em>through</em>
 * the canopies rather than stopping underneath them so that the solver has wood to measure from. Where
 * the hem of a large canopy still falls out of range, {@link DecayRepair#CARVE_BRANCH} grows a real
 * branch into it rather than hiding a detached log in the foliage.
 *
 * <h2>Bleached trees</h2>
 * {@link GloomwoodTreeConfiguration#bleachedChance} inverts the canopy palette on a fraction of the
 * trees - see {@link #palette}. Only the solitary placement sets it, so the pale specimens are the ones
 * standing on their own.
 * <p>
 * The palette is not only a marking: a bleached tree has ancient debris buried under its roots
 * ({@link #ancientDebrisRoots}). That makes the pale crown a landmark worth crossing a biome for, and it
 * is why the variant is confined to the lone trees in the open - one standing clear of a grove can be
 * seen and walked to, where the same tree inside a stand would be a thing you found by accident.
 */
public class GloomwoodTreeFeature extends NonOverlappingFeature<GloomwoodTreeConfiguration>
        implements GrowableFeature<GloomwoodTreeConfiguration> {

    public GloomwoodTreeFeature() {
        super(GloomwoodTreeConfiguration.CODEC);
    }

    @Override
    protected boolean isStructure(BlockState state) {
        return state.is(NetherWoodBlocks.MAT_GLOOMWOOD.getLog())
                || state.is(NetherWoodBlocks.MAT_GLOOMWOOD_DARK.getLog())
                || state.is(NetherWoodBlocks.GLOOMWOOD_TRANSITION_LOG);
    }

    /**
     * Gloomwood grows on the sculk floor of its own biome as well as on ordinary nether ground.
     * <p>
     * The sculk half has to be named separately: {@link BlocksHelper#isNetherGround} covers the gloomsculk
     * blocks, but the biome's surface rule leaves most of the floor as plain vanilla sculk, which is
     * SCULK_LIKE and nothing else. Without the second test the species would refuse the greater part of its
     * own biome.
     */
    @Override
    protected boolean isGround(BlockState state) {
        return BlocksHelper.isNetherGround(state) || BlocksHelper.isSculkLike(state);
    }

    /**
     * The three sizes a gloomwood comes in, largest first.
     * <p>
     * A smaller one is not just a shorter trunk: canopy body, dome, skirt depth and the eye marks all
     * scale with it, because a full-size hanging canopy on a 7-block trunk sits on the ground. The skirt
     * depth in particular tracks the body half-height - see the builder - so the hollow stays
     * proportional.
     * <p>
     * {@code clearance} is the headroom below which the variant is not even attempted. It is a cheap
     * pre-filter, not the real test: the built tree is measured exactly before anything is written. It
     * is deliberately an <em>under</em>-estimate - trunk height is random within the variant's range, so
     * a number tight enough for the tallest trunk would reject the shortest one, and the only cost of
     * guessing low is a build that the exact test then throws away.
     */
    private enum Size {
        FULL(11, 16, 2, 3, 3.0F, 1.6F, 0.8F,
             4.0F, 11.0F, 4.0F, 5.5F, -4.0F, 2.0F, 1.5F, 0.7F,
             3.0F, 1.6F, 3.4F, 24, 5.0F, 7.5F),
        // Two thirds of full on every canopy dimension, not just the trunk. A full-size canopy on a short
        // trunk drags its hem through the ground, and smoothing/wobble left at full size on a smaller
        // body swallow the skirt - the hollow stops reading and the lobe goes back to being a blob.
        // The band heights are absolute, not fractions of the trunk, so a small tree left on the full
        // set never reaches the pale crown at all - its whole trunk sits below the 7.5 boundary. These
        // are pulled down until the pale wood is a real part of a seven-block tree.
        SMALL(7, 10, 2, 2, 2.2F, 1.2F, 0.6F,
              2.6F, 6.5F, 2.5F, 3.25F, -2.2F, 1.2F, 1.0F, 0.45F,
              2.0F, 1.1F, 2.2F, 15, 2.6F, 4.2F),
        // The gloomwood's own biome is mostly low. Measured over a generated one (n=4256 floor cells with
        // air above them): half the sculk floor has 8 blocks of headroom or less, and only 16.5% has the
        // ~20 a SMALL actually needs. Worldgen never noticed - it simply put its trees in the tall parts
        // - but a player planting a sapling on the floor in front of them hit a tree that could not grow
        // there and no way to find out why. This is the variant that fits that floor: it takes 12, which
        // covers 35% of it.
        //
        // Below this is not worth having. A tree that fits under 10 is about five blocks tall in total,
        // by which point the hanging canopy this species is built around has no room to hang and the
        // thing stops reading as a gloomwood at all. The rest of the floor is handled the other way, by
        // BlockGloomwoodSapling refusing the bone meal rather than swallowing it.
        //
        // Unlike the two above it, TINY's clearance is the headroom at which it *always* fits rather
        // than an under-estimate - it is also what the sapling's bone meal check reads, and there a
        // number that is too low means an application spent for nothing. Its trunk range is narrow
        // enough (3-5) that the honest number costs almost nothing in worldgen: measured, 11 grows a
        // tree about two thirds of the time and 12 every time.
        TINY(3, 5, 2, 2, 1.2F, 0.8F, 0.45F,
             1.4F, 2.8F, 1.1F, 1.4F, -0.9F, 0.6F, 0.55F, 0.25F,
             1.0F, 0.6F, 1.2F, 12, 1.1F, 1.9F);

        final int minHeight, maxHeight, minArms, maxArms;
        final float spread, baseRadius, tipRadius;
        final float canopyRadius, bodyHeight, domeHeight, skirtDepth, verticalOffset, minRadius;
        final float smoothing, hemWobble;
        final float eyeHeight, eyeSeparation, eyeForward;
        final int clearance;
        final float darkUpTo, transitionUpTo;

        Size(
                int minHeight, int maxHeight, int minArms, int maxArms, float spread,
                float baseRadius, float tipRadius, float canopyRadius, float bodyHeight,
                float domeHeight, float skirtDepth, float verticalOffset, float minRadius,
                float smoothing, float hemWobble,
                float eyeHeight, float eyeSeparation, float eyeForward, int clearance,
                float darkUpTo, float transitionUpTo
        ) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.minArms = minArms;
            this.maxArms = maxArms;
            this.spread = spread;
            this.baseRadius = baseRadius;
            this.tipRadius = tipRadius;
            this.canopyRadius = canopyRadius;
            this.bodyHeight = bodyHeight;
            this.domeHeight = domeHeight;
            this.skirtDepth = skirtDepth;
            this.verticalOffset = verticalOffset;
            this.minRadius = minRadius;
            this.smoothing = smoothing;
            this.hemWobble = hemWobble;
            this.eyeHeight = eyeHeight;
            this.eyeSeparation = eyeSeparation;
            this.eyeForward = eyeForward;
            this.clearance = clearance;
            this.darkUpTo = darkUpTo;
            this.transitionUpTo = transitionUpTo;
        }
    }

    /**
     * How far up to bother looking for a ceiling. This has to clear the tallest tree the builder can
     * actually produce, not the {@link Size#clearance} pre-filter: the scan result is also the ceiling
     * the exact fit test measures against, so a cap set too low silently rejects every full-size tree in
     * an open cavern.
     */
    private static final int MAX_CLEARANCE_SCAN = 40;

    /**
     * Blocks of air the crown must keep above it. Fitting exactly is not good enough - a canopy grown
     * flush to the rock reads as squashed against the ceiling rather than as a tree standing under one.
     */
    private static final int CROWN_MARGIN = 2;

    /** One tree in this many stands on a molten root. */
    private static final int MOLTEN_ROOT_CHANCE = 2;

    /** How far out from the trunk centre the root swap looks - a full-size trunk is about 3 wide. */
    private static final int ROOT_RADIUS = 2;

    /**
     * How deep the ancient debris under a bleached tree's roots runs, drawn per column so the pocket is
     * ragged rather than a slab.
     */
    private static final int MIN_DEBRIS_DEPTH = 1;
    private static final int MAX_DEBRIS_DEPTH = 4;

    /**
     * How likely each column of a molten root is to be molten. Anything under 1 will do; a bit over
     * half keeps the patch clearly ragged without the molten wood becoming a minority speckle.
     * <p>
     * One block per column, never two: the molten log's texture is drawn as wood with the heat coming
     * out of the ground into it, and a second one stacked on top has that same near-white glow at its
     * foot with nothing below to explain it.
     */
    private static final float MOLTEN_COLUMN_CHANCE = 0.55F;

    /**
     * Grow a gloomwood: a randomly chosen size, stepped down until it fits, and nothing at all if even
     * the small one does not.
     * <p>
     * There are two tests, and both are needed. The headroom scan rejects positions under a low ceiling
     * before any work is done, but it only measures the column directly above the base - a canopy leans
     * out on its arms and can still meet rock. So the chosen variant is built into voxels and its real
     * bounding box is measured before a single block is written; a tree that came out taller than the
     * room falls through to the next size down. Nothing is committed until it is known to fit, so a
     * rejected tree leaves no half-grown stump behind.
     */
    @Override
    protected boolean grow(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            GloomwoodTreeConfiguration config,
            StructureGeneratorThreadContext context
    ) {
        final int headroom = clearanceAbove(world, pos);
        final Size[] sizes = Size.values();
        // Rolled once, before the size loop: a tree that is rebuilt a size down is the same tree, and
        // re-rolling per attempt would quietly raise the real chance above the configured one.
        final boolean bleached = config.bleachedChance > 0 && random.nextFloat() < config.bleachedChance;
        // One in three is a small gloomwood whatever the room available - the fallback below only ever
        // makes a tree smaller, so without this the small variant would exist solely under low ceilings
        // and an open cavern would fill with identical full-size trees.
        //
        // Deliberately SMALL rather than the last variant: TINY exists for ceilings nothing else fits
        // under, and starting there one time in three would scatter tiny trees through open caverns and
        // change what the biome looks like where it already worked. It stays a fallback.
        for (int i = random.nextInt(3) == 0 ? Size.SMALL.ordinal() : 0; i < sizes.length; i++) {
            final Size size = sizes[i];
            if (headroom < size.clearance) continue;

            final TreeBuilder builder = builder(random, size, bleached);
            final TreeSpace space = TreeSpace.of(world, pos);
            final TreeVoxels voxels = builder.build(space, random, builder.filterFor(world, space));
            if (voxels.minY() + voxels.sizeY() + CROWN_MARGIN > headroom) continue;

            if (builder.commit(world, space, voxels) > 0) {
                // Debris before the molten swap, not after: it looks for this tree's own logs to decide
                // which columns are root, and the swap would have turned some of them into a fourth block
                // it does not know about.
                if (bleached) ancientDebrisRoots(world, space, pos, random);
                if (random.nextInt(MOLTEN_ROOT_CHANCE) == 0) moltenRoot(world, space, pos, random);
                return true;
            }
        }
        return false;
    }

    /**
     * Swap some of the dark logs in the tree's lowest layer for the molten variant.
     * <p>
     * Column by column rather than all of them: converting the whole layer - which is what this used
     * to do - makes the choice read as a property of the tree rather than of the wood, since every
     * base block changes together and the result is a clean disc of molten wood. Leaving about half of
     * the columns dark reads as the heat coming up out of the ground in patches.
     * <p>
     * Only blocks that actually came out as dark gloomwood are touched, so a trunk whose base band
     * landed on transition log is left alone.
     * <p>
     * Done after the commit rather than through the log palette because it is about where the trunk meets
     * the ground, not about height: {@link LogBands} bands by distance up the trunk, and would happily
     * paint the bottom of a tree that grew off the side of a cliff.
     * <p>
     * Gated on the same {@link TreeSpace#canWrite} test the commit uses. A feature may only write within
     * a bounded zone around the chunk being generated, and a trunk near that edge would otherwise have
     * this reach past it - the writes are simply dropped there, leaving a root half converted.
     */
    private static void moltenRoot(
            ServerLevelAccessor world, TreeSpace space, BlockPos base, RandomSource random
    ) {
        final BlockState molten = NetherWoodBlocks.GLOOMWOOD_DARK_MOLTEN_LOG.defaultBlockState();
        final Block darkLog = NetherWoodBlocks.MAT_GLOOMWOOD_DARK.getLog();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -ROOT_RADIUS; dx <= ROOT_RADIUS; dx++) {
            for (int dz = -ROOT_RADIUS; dz <= ROOT_RADIUS; dz++) {
                // Drawn before the tests, not after, so that the pattern is the tree's own and does not
                // change with where the write zone's edge or the terrain happens to fall.
                final boolean isMolten = random.nextFloat() < MOLTEN_COLUMN_CHANCE;
                if (!isMolten || !space.canWrite(dx, dz)) continue;

                cursor.set(base.getX() + dx, base.getY(), base.getZ() + dz);
                if (!world.getBlockState(cursor).is(darkLog)) continue;
                world.setBlock(cursor, molten, BlocksHelper.SET_SILENT);
            }
        }
    }

    /**
     * Bury ancient debris under a bleached tree's roots.
     * <p>
     * The bleached palette is the tell, and this is what it tells: every column of the tree's lowest log
     * layer gets {@link #MIN_DEBRIS_DEPTH}-{@link #MAX_DEBRIS_DEPTH} blocks of debris hung under it. Only
     * columns that actually came out as log are touched, so the pocket sits entirely beneath the trunk
     * and none of it is visible from the surface - the tree has to be felled and dug out, which is the
     * point of tying it to a marking rather than to an ore distribution.
     * <p>
     * The depth is drawn per column and before the tests, for the same reason
     * {@link #moltenRoot} draws its own roll there: the pocket's shape is then the tree's, and does not
     * change with where the chunk's write zone or the terrain happens to fall.
     * <p>
     * Only ground is replaced, using the same {@link #isGround} test that decided the tree could stand
     * here, and a column stops at the first block that is not - so a root over a cave hangs its debris
     * down to the roof of it and no further, and nothing structural is ever overwritten.
     */
    private void ancientDebrisRoots(
            ServerLevelAccessor world, TreeSpace space, BlockPos base, RandomSource random
    ) {
        final BlockState debris = Blocks.ANCIENT_DEBRIS.defaultBlockState();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -ROOT_RADIUS; dx <= ROOT_RADIUS; dx++) {
            for (int dz = -ROOT_RADIUS; dz <= ROOT_RADIUS; dz++) {
                final int depth = MIN_DEBRIS_DEPTH
                        + random.nextInt(MAX_DEBRIS_DEPTH - MIN_DEBRIS_DEPTH + 1);
                if (!space.canWrite(dx, dz)) continue;

                cursor.set(base.getX() + dx, base.getY(), base.getZ() + dz);
                if (!isStructure(world.getBlockState(cursor))) continue;

                for (int down = 1; down <= depth; down++) {
                    cursor.set(base.getX() + dx, base.getY() - down, base.getZ() + dz);
                    if (!isGround(world.getBlockState(cursor))) break;
                    world.setBlock(cursor, debris, BlocksHelper.SET_SILENT);
                }
            }
        }
    }

    /**
     * The headroom below which no variant at all is attempted - the smallest {@link Size}'s clearance.
     * <p>
     * This is what a sapling asks before it accepts bone meal, and why {@link Size#TINY} carries the
     * headroom it <em>always</em> fits in rather than the under-estimate the larger two carry: at or
     * above this number {@link #grow} produced a tree in every measured attempt, so the application is
     * never spent for nothing. It is not the absolute floor - a short-trunked TINY does sometimes fit in
     * one less - but a gate has to promise, and one block of lost opportunity is the price of that.
     */
    public static final int MIN_CLEARANCE = Size.values()[Size.values().length - 1].clearance;

    /**
     * Whether a tree could stand at {@code pos}, for a sapling deciding whether bone meal is worth
     * taking.
     * <p>
     * Measured from {@code pos.above()} plus one for {@code pos} itself, because the caller is asking on
     * behalf of a sapling that is still standing there: {@code doGrowFeature} clears that block before
     * the feature runs, so the cell is free by the time {@link #grow} looks, but a sapling is not
     * {@code canBeReplaced} and scanning from {@code pos} would count zero and reject everywhere.
     */
    public static boolean hasRoomToGrow(BlockGetter level, BlockPos pos) {
        return 1 + clearanceAbove(level, pos.above()) >= MIN_CLEARANCE;
    }

    /**
     * Free blocks above {@code pos}, counting the base position itself, up to {@link #MAX_CLEARANCE_SCAN}.
     * Anything the tree could not overwrite ends the count.
     */
    private static int clearanceAbove(BlockGetter world, BlockPos pos) {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);
        int free = 0;
        while (free < MAX_CLEARANCE_SCAN) {
            final BlockState state = world.getBlockState(cursor);
            if (!state.isAir() && !state.canBeReplaced()) break;
            free++;
            cursor.move(0, 1, 0);
        }
        return free;
    }

    @Override
    public boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            GloomwoodTreeConfiguration configuration
    ) {
        return grow(
                level,
                pos,
                random,
                new GloomwoodTreeConfiguration(false, configuration.distance, configuration.bleachedChance),
                NetherThreadDataStorage.generatorForThread().context
        );
    }

    /**
     * A fresh builder per tree: the palette bakes in this tree's random facing for the eyes, and the
     * bark noise is seeded per tree so neighbouring gloomwoods do not share a crackle pattern.
     */
    private static TreeBuilder builder(RandomSource random, Size size, boolean bleached) {
        final float facing = random.nextFloat() * (float) Math.PI * 2;

        return TreeBuilder.create()
                          .trunk(ForkingTrunk.builder()
                                             .height(size.minHeight, size.maxHeight)
                                             .forkAt(0.5F)
                                             .arms(size.minArms, size.maxArms)
                                             .spread(size.spread)
                                             .lean(0.8F)
                                             .jitter(0.6F)
                                             .radius(size.baseRadius, size.tipRadius)
                                             // One anchor per arm, so each arm carries one tall hanging
                                             // ghost. Two overlapping tall lobes per arm merge into an
                                             // undifferentiated column and lose the silhouette entirely -
                                             // the taller the canopy, the more it wants to stand alone.
                                             .anchorsPerArm(1)
                                             .build())
                          .canopy(GhostCanopy.builder()
                                             .radius(size.canopyRadius)
                                             // ~15 tall against 8 wide. The body carries the elongation
                                             // rather than the dome: a taller dome just makes the thing
                                             // egg-shaped, where a taller body is what hangs.
                                             .bodyHeight(size.bodyHeight)
                                             .domeHeight(size.domeHeight)
                                             // Skirt depth tracks the body half-height so the hollow
                                             // stays proportional - a fixed depth in a longer body leaves
                                             // the lower half solid and the hem stops reading.
                                             .skirt(0.75F, size.skirtDepth, 0.5F)
                                             // Hangs the body 4 below the branch, which puts the
                                             // skirt's top ~3.5 under the anchor and leaves the wood
                                             // sitting in solid dome foliage.
                                             .verticalOffset(size.verticalOffset)
                                             .smoothing(size.smoothing)
                                             .hemWobble(size.hemWobble)
                                             .minRadius(size.minRadius)
                                             .build())
                          .palette(palette(random, facing, size, bleached))
                          // A hem 5.5 blocks below the anchor is well outside the 6-step decay budget
                          // once the path has to go around the hollow, so every lobe needs a carved
                          // spine down its middle. That is the intended outcome - a hanging canopy with
                          // wood running through it - but it costs several carves per lobe.
                          .maxCarves(64)
                          // +-0.35 blocks of ridging. Half the 0.8 tip radius is the ceiling here; past
                          // that the thin end of an arm comes apart into fragments, and while
                          // pruneLooseWood would then delete them, a branch that quietly loses its tip is
                          // not an improvement over one that never broke.
                          .bark(Crackle.bark(random.nextLong(), 0.7F, 0.55F))
                          .repair(DecayRepair.CARVE_BRANCH)
                          .build();
    }

    private static TreePalette palette(RandomSource random, float facing, Size size, boolean bleached) {
        // The transition log is a seam, not a band: it is the block on which the dark wood becomes pale,
        // and a column gets exactly one of them, somewhere between darkUpTo and transitionUpTo. Given a
        // band of its own it filled that whole range instead - two or three transition logs stacked up,
        // which reads as a third species of wood banding the trunk rather than as a joint.
        //
        // Every column of the trunk draws its own transition height, so the joint is ragged and the two
        // woods interleave across the band rather than meeting along a plane. That is what jitter used
        // to be for here, done at the resolution a one-block joint needs, which is why there is no
        // jitter any more: it would only push the joint outside the range these two numbers bound.
        final LogBands logs = LogBands
                .builder()
                .band(size.darkUpTo, () -> NetherWoodBlocks.MAT_GLOOMWOOD_DARK.getLog().defaultBlockState())
                .seam(size.transitionUpTo, () -> NetherWoodBlocks.GLOOMWOOD_TRANSITION_LOG.defaultBlockState())
                .top(() -> NetherWoodBlocks.MAT_GLOOMWOOD.getLog().defaultBlockState())
                .seamSeed(random.nextLong())
                .build();

        // A bleached tree is the same two leaves the other way round: the pale block, which normally only
        // marks the eyes, becomes the whole crown and the ordinary dark one is cut out of it for the face.
        // Nothing else about the tree changes - the wood, the shape and the mark geometry are the same, so
        // it reads as one of the same species rather than as a different tree.
        final Block canopyLeaves = bleached
                ? NetherLeavesBlocks.GLOOMWOOD_BLEACHED_LEAVES
                : NetherLeavesBlocks.GLOOMWOOD_LEAVES;
        final Block eyeLeaves = bleached
                ? NetherLeavesBlocks.GLOOMWOOD_LEAVES
                : NetherLeavesBlocks.GLOOMWOOD_BLEACHED_LEAVES;

        final LeafMarks leaves = LeafMarks
                .of(canopyLeaves::defaultBlockState)
                // One face on the tree, on whichever lobe ended up highest - a grove of ghosts, not a
                // tree covered in eyes. Height +3 puts the pair in the upper body where the dome starts
                // to close in, i.e. where a head would be on a hanging figure. forward 3.4 against a
                // radius of 4 sinks each sphere far enough in that the shell cuts a clean 3-block disc
                // out of it; sitting it flush on the radius leaves too few leaves inside to read.
                .eyesOnPrimaryLobe(
                        facing, size.eyeHeight, size.canopyRadius, size.eyeSeparation, size.eyeForward,
                        eyeLeaves::defaultBlockState
                )
                .build();

        return TreePalette.of(logs, leaves);
    }
}
