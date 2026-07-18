package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockMushroomFir;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockMushroomFirSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.Stem;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MushroomFirMaterial extends NetherWoodenMaterial<MushroomFirMaterial> {
    public MushroomFirMaterial() {
        super("mushroom_fir", MapColor.COLOR_BLUE, MapColor.COLOR_BLUE);
        this.setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    // mushroom_fir's trapdoor is exactly stalagnate's shared (no-side) trapdoor mesh -
                    // generate its child model/blockstate/item from the template instead of hand-authoring.
                    .replace(new NetherWoodSlots.TrapdoorTemplate())
                    .add(TrunkSlot.create(BlockMushroomFir::new, NetherRender.cutout()))
                    .add(Sapling.create(BlockMushroomFirSapling::new, NetherSurvival.netherMycelium()))
                    // The stem delegates its item model to the dedicated trunk model; its blockstate and
                    // block model are hand-authored.
                    .add(new Stem() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
                            return ModelTraitLibrary.externalModelDelegatedItem(
                                    () -> BetterNether.C.mk("block/mushroom_fir_trunk_middle")
                            );
                        }
                    });
    }

    public Block getStem() {
        return getBlock(NetherSlots.STEM);
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }
}
