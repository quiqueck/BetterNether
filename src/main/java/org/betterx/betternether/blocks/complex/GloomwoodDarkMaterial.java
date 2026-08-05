package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.registry.block.NetherStoneBlocks;

import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.types.Bark;
import de.ambertation.wover.sets.api.blocks.types.Log;

import net.minecraft.world.level.material.MapColor;

/**
 * The dark gloomwood set - the sculked lower trunk. A material in its own right rather than a shade of
 * {@link GloomwoodMaterial}: it is flat sculked timber off the log's ring colours, with none of the light
 * set's catalyst gradient.
 * <p>
 * Carries no boat: this wood is dead and waterlogged through, so the set drops the boat and chest-boat slots.
 */
public class GloomwoodDarkMaterial extends NetherWoodenMaterial<GloomwoodDarkMaterial> {

    public GloomwoodDarkMaterial() {
        super("gloomwood_dark", MapColor.COLOR_BLUE, MapColor.COLOR_BLUE);
        this.setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        // See GloomwoodMaterial: standard texture layout, so the generating wover slots go back in place of
        // NetherWoodenMaterial's hand-authored-model ones.
        return super.createDefaultDefinitions()
                    .replace(new Log(true))
                    .replace(new Log(false))
                    .replace(new Bark(true))
                    .replace(new Bark(false))
                    .replace(new NetherWoodSlots.TrapdoorTemplate())
                    // no boat for the dark wood - and a chest boat is still a boat
                    .remove(SlotType.BOAT)
                    .remove(SlotType.CHEST_BOAT);
    }
}
