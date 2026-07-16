package org.betterx.betternether.blocks.materials;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Material property factories for BetterNether blocks.
 * <p>
 * These replicate the property sets that used to come from bclib's legacy behaviour builders.
 * The values are inlined here verbatim so block behaviour is unchanged.
 * <p>
 * Tags, loot tables and creative-tab placement are <em>not</em> handled here - those come from the
 * wover/bclib trait system and the {@code Behaviour*} marker interfaces applied at registration.
 */
public class Materials {
    public static final BlockBehaviour.Properties NETHER_GRASS = grass(MapColor.GRASS);

    public static final BlockBehaviour.Properties NETHER_SAPLING = plant(MapColor.PLANT)
            .randomTicks()
            .sound(SoundType.CROP)
            .noCollission()
            .noOcclusion();

    public static final BlockBehaviour.Properties NETHER_PLANT = plant(MapColor.PLANT)
            .sound(SoundType.CROP)
            .noOcclusion()
            .noCollission();

    // ------------------------------------------------------------------
    // Base material factories (formerly the bclib create* behaviour builders)
    // ------------------------------------------------------------------

    /**
     * A plant that does not block movement.
     */
    public static BlockBehaviour.Properties plant(MapColor color) {
        return plant(BlockBehaviour.Properties.of(), color);
    }

    public static BlockBehaviour.Properties plant(BlockBehaviour.Properties props, MapColor color) {
        return walkablePlant(props, color).noCollission();
    }

    /**
     * A plant that can be walked through/on (keeps its collision box).
     */
    public static BlockBehaviour.Properties walkablePlant(BlockBehaviour.Properties props, MapColor color) {
        return props
                .mapColor(color)
                .noOcclusion()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY);
    }

    public static BlockBehaviour.Properties grass(MapColor color) {
        return grass(BlockBehaviour.Properties.of(), color);
    }

    public static BlockBehaviour.Properties grass(BlockBehaviour.Properties props, MapColor color) {
        return plant(props, color)
                .noCollission()
                .noOcclusion()
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .sound(SoundType.GRASS);
    }

    /**
     * A vine that does not spread on its own (no random ticks).
     */
    public static BlockBehaviour.Properties staticVine(BlockBehaviour.Properties props, MapColor color) {
        return plant(props, color)
                .replaceable()
                .noCollission()
                .strength(0.2f)
                .sound(SoundType.VINE);
    }

    /**
     * Leaves that do not decay on their own (no random ticks).
     */
    public static BlockBehaviour.Properties staticLeaves(MapColor color, boolean flammable) {
        return staticLeaves(BlockBehaviour.Properties.of(), color, flammable);
    }

    public static BlockBehaviour.Properties staticLeaves(
            BlockBehaviour.Properties props,
            MapColor color,
            boolean flammable
    ) {
        final BlockBehaviour.Properties p = props
                .mapColor(color)
                .strength(0.2f)
                .noOcclusion()
                .isValidSpawn(Blocks::ocelotOrParrot)
                .isSuffocating(Blocks::never)
                .isViewBlocking(Blocks::never)
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(Blocks::never)
                .sound(SoundType.GRASS);
        if (flammable) {
            p.ignitedByLava();
        }
        return p;
    }

    public static BlockBehaviour.Properties cactus(
            BlockBehaviour.Properties props,
            MapColor color,
            boolean flammable
    ) {
        final BlockBehaviour.Properties p = props
                .mapColor(color)
                .randomTicks()
                .strength(0.4F)
                .sound(SoundType.WOOL)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();
        if (flammable) {
            p.ignitedByLava();
        }
        return p;
    }

    public static BlockBehaviour.Properties stone(BlockBehaviour.Properties props, MapColor color) {
        return props
                .mapColor(color)
                .strength(1.5F, 6.0F)
                .instrument(NoteBlockInstrument.BASEDRUM);
    }

    public static BlockBehaviour.Properties metal(BlockBehaviour.Properties props, MapColor color) {
        return props
                .mapColor(color)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL);
    }

    public static BlockBehaviour.Properties wood(MapColor color, boolean flammable) {
        return wood(BlockBehaviour.Properties.of(), color, flammable);
    }

    public static BlockBehaviour.Properties wood(
            BlockBehaviour.Properties props,
            MapColor color,
            boolean flammable
    ) {
        final BlockBehaviour.Properties p = props
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD);
        if (flammable) {
            p.ignitedByLava();
        }
        return p;
    }

    // ------------------------------------------------------------------
    // BetterNether material presets
    // ------------------------------------------------------------------

    public static BlockBehaviour.Properties makeNetherWood(MapColor color) {
        return wood(color, false)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties makeNetherGrass(MapColor color) {
        return grass(color)
                .isValidSpawn((state, world, pos, type) -> true);
    }

    // Props-threaded variants: apply the material defaults on top of an already-configured
    // (id-bearing) Properties instance instead of building a fresh (id-less) one. Used by the
    // block constructors that receive their properties from the block-registry definition.

    public static BlockBehaviour.Properties makeNetherWood(BlockBehaviour.Properties props, MapColor color) {
        return wood(props, color, false)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties makeNetherGrass(BlockBehaviour.Properties props, MapColor color) {
        return grass(props, color)
                .isValidSpawn((state, world, pos, type) -> true);
    }

    public static BlockBehaviour.Properties netherPlant(BlockBehaviour.Properties props) {
        return plant(props, MapColor.PLANT)
                .sound(SoundType.CROP)
                .noOcclusion()
                .noCollission();
    }

    public static BlockBehaviour.Properties netherSapling(BlockBehaviour.Properties props) {
        return plant(props, MapColor.PLANT)
                .randomTicks()
                .sound(SoundType.CROP)
                .noCollission()
                .noOcclusion();
    }
}
