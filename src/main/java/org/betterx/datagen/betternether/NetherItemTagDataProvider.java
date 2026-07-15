package org.betterx.datagen.betternether;

import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class NetherItemTagDataProvider extends WoverTagProvider.ForItems {
    public NetherItemTagDataProvider(ModCore modCore) {
        super(modCore, Set.of(
                NetherTags.FLAMING_RUBY_PRIMARY
        ));
    }

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        context.add(
                NetherTags.FLAMING_RUBY_ENCHANTABLE,
                NetherItems.FLAMING_RUBY_SET.getAll()
        );

        context.add(
                NetherTags.OBSIDIAN_BREAKER_ENCHANTABLE,
                NetherTags.NETHER_PICKAXES
        );

        // Repair-ingredient tags referenced by the tool/armor recipes (ToolMaterial.repairItems()).
        // Without these the smithing/crafting recipes fail to load ("Missing tag <name>/repair").
        context.add(BNToolMaterial.CINCINNASITE.repairItems(), NetherItems.CINCINNASITE_INGOT);
        context.add(BNToolMaterial.CINCINNASITE_DIAMOND.repairItems(), Items.DIAMOND);
        context.add(BNToolMaterial.NETHER_RUBY.repairItems(), NetherItems.NETHER_RUBY);
        context.add(BNToolMaterial.FLAMING_RUBY.repairItems(), Blocks.SCULK_CATALYST.asItem());
    }
}
