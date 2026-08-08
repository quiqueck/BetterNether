package org.betterx.datagen.betternether;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.world.item.Items;

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
                NetherEquipmentItems.FLAMING_RUBY_SET.getAll()
        );

        context.add(
                NetherTags.OBSIDIAN_BREAKER_ENCHANTABLE,
                NetherTags.NETHER_PICKAXES
        );

        // Repair-ingredient tags referenced by the tool/armor recipes (ToolMaterial.repairItems()).
        // Without these the smithing/crafting recipes fail to load ("Missing tag <name>/repair").
        context.add(BNToolMaterial.CINCINNASITE.repairItems(), NetherResourceItems.CINCINNASITE_INGOT);
        context.add(BNToolMaterial.CINCINNASITE_DIAMOND.repairItems(), Items.DIAMOND);
        context.add(BNToolMaterial.NETHER_RUBY.repairItems(), NetherResourceItems.NETHER_RUBY);
        // Either gloomsculk crystal repairs flaming ruby gear. Both are the burning orange crystal the
        // tier is themed on, and taking both keeps the farmable budded one and the silk-touch-only
        // ground variant equally useful.
        context.add(
                BNToolMaterial.FLAMING_RUBY.repairItems(),
                NetherTerrainBlocks.GLOOMSCULK_CRYSTAL.asItem(),
                NetherTerrainBlocks.GLOOMSCULK_GEODE_CRYSTAL.asItem()
        );
    }
}
