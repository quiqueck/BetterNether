package org.betterx.betternether.blocks;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

/**
 * Which way a gloomwisp's head looks.
 * <p>
 * Four compass values plus {@link #RANDOM}, which is the default and means "let the client pick". A blockstate
 * variant can only rotate a model by whole quarter turns, so there is no finer aim to offer than these four.
 * <p>
 * RANDOM exists so that the two ways a wisp can come into the world stay distinct without anything having to know
 * which one happened. Worldgen and growth place the default state and get the weighted four-way pick they have
 * always had - the same wisp keeps looking the same way, because the client seeds that pick from the block
 * position. Only a player placing one by hand writes a compass value, and from then on the wisp is aimed and stays
 * aimed. Nothing has to distinguish a feature from a player: not writing the property is what "random" means.
 */
public enum WispRotation implements StringRepresentable {
    RANDOM("random"),
    NORTH("north"),
    EAST("east"),
    SOUTH("south"),
    WEST("west");

    private final String name;

    WispRotation(String name) {
        this.name = name;
    }

    /**
     * The quarter turn, in degrees, this rotation applies to the head model. Meaningless for {@link #RANDOM},
     * which is not a single rotation - that case carries all four in a weighted list instead.
     */
    public int yRotation() {
        return switch (this) {
            case RANDOM, NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
        };
    }

    /**
     * The rotation for a wisp being placed by a player looking along {@code facing}.
     * <p>
     * Mirrored, so the head ends up looking back at whoever put it down rather than away with them. {@code facing}
     * is the direction the placer is looking, so the wisp wants its opposite - place one while walking north and
     * it turns south, to meet you.
     */
    public static WispRotation forHorizontal(Direction facing) {
        return switch (facing.getOpposite()) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
