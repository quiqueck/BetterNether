package org.betterx.betternether.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// The nested Wood/Stone/Metal/Obsidian/Glass shims (WP6.14 sweep) are gone: only Metal and Glass were ever
// actually used, and both added nothing beyond BNPane itself (pure passthrough constructors, needed only
// because this class used to be abstract). Every registration site now uses plain BNPane, with the material
// carried by the registration's traits. The constructor's strength(0.3, 0.3)/noOcclusion() (an R1 violation
// - it mutated the incoming Properties) moves to each registration site as explicit chained/trait setters,
// applied AFTER the material trait so the override still wins (matches the previous last-write-wins order).
// The dropSelf flag and the getDrops() override it drove are gone too: every dropSelf=true registration
// already carried NetherLoot.dropSelfNoExplosion(), which generates the very table the override hard-coded,
// and every dropSelf=false one already had a loot table for super.getDrops() to roll. The override was a
// second loot path that only ever restated the first.
public class BNPane extends IronBarsBlock {
    public BNPane(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState neighbor, Direction facing) {
        if (neighbor.getBlock() == this) {
            if (!facing.getAxis().isHorizontal()) {
                return false;
            }

            if (state.getValue(PROPERTY_BY_DIRECTION.get(facing)) && neighbor.getValue(PROPERTY_BY_DIRECTION.get(facing.getOpposite()))) {
                return true;
            }
        }

        return super.skipRendering(state, neighbor, facing);
    }
}
