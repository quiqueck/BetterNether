package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.set.wood.StrippedBark;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import org.jetbrains.annotations.NotNull;

public class NetherStrippedBark extends StrippedBark {
    @Override
    protected void modifyBlockEntry(WoodenComplexMaterial parentMaterial, @NotNull BlockEntry entry) {
        entry
                .setBlockTags(
                        BlockTags.LOGS,
                        parentMaterial.getBlockTag(WoodenComplexMaterial.TAG_LOGS)
                )
                .setItemTags(
                        ItemTags.LOGS,
                        parentMaterial.getItemTag(WoodenComplexMaterial.TAG_LOGS)
                );
    }
}
