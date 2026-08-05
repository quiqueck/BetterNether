/**
 * A level-agnostic tree construction library.
 *
 * <h2>Why this lives here</h2>
 * This package is written to be lifted into WorldWeaver as {@code wover-tree-api} once a second and third
 * tree are expressed through it. Until then it sits inside BetterNether so it can be iterated on together
 * with its first real consumer ({@code GloomwoodTreeFeature}).
 *
 * <h2>The rule that makes the move possible</h2>
 * <strong>Nothing in this package or its subpackages may import {@code org.betterx.bclib} or
 * {@code org.betterx.betternether}.</strong> Only vanilla and {@code de.ambertation.wover} are allowed.
 * <p>
 * That is not stylistic. BCLib <em>depends on</em> WorldWeaver ({@code required_dependencies} in BCLib's
 * {@code gradle.properties} lists {@code worldweaver}), so the dependency cannot run the other way: a
 * future {@code wover-tree-api} would not be able to see BCLib at all. In particular this package
 * deliberately does <em>not</em> use BCLib's {@code org.betterx.bclib.sdf} - {@link
 * org.betterx.betternether.world.tree.math.Volume} is a small standalone replacement for the part of it a
 * tree needs. The compiler cannot enforce this from inside BetterNether, which sees both libraries, so it
 * has to be held up by review.
 *
 * <h2>How a tree is built</h2>
 * The pipeline is deliberately "compute everything, then write once":
 * <ol>
 *     <li>A {@link org.betterx.betternether.world.tree.skeleton.TrunkShape} produces a
 *         {@link org.betterx.betternether.world.tree.skeleton.Skeleton} - line segments with radii, plus
 *         the {@link org.betterx.betternether.world.tree.skeleton.Anchor}s where foliage attaches.</li>
 *     <li>A {@link org.betterx.betternether.world.tree.canopy.CanopyShape} turns each anchor into a
 *         {@link org.betterx.betternether.world.tree.math.Volume}.</li>
 *     <li>Both are rasterised into a {@link org.betterx.betternether.world.tree.build.TreeVoxels} buffer
 *         in tree-local coordinates - no world writes yet.</li>
 *     <li>{@link org.betterx.betternether.world.tree.decay.LeafDecay} solves the buffer so that every
 *         surviving leaf is close enough to a log for vanilla leaf decay to keep it alive, repairing the
 *         geometry where it is not.</li>
 *     <li>Only then is the buffer committed to the level.</li>
 * </ol>
 * Step 4 is the reason for the buffer. Leaf decay is a property of the <em>finished</em> tree, so it
 * cannot be decided while the tree is still being drawn, and it must be able to change the geometry -
 * which is impossible once blocks are in the world.
 *
 * @see org.betterx.betternether.world.tree.decay.LeafDecay
 */
package org.betterx.betternether.world.tree;
