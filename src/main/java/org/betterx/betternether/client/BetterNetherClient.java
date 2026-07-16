package org.betterx.betternether.client;

import org.betterx.bclib.integration.modmenu.ModMenu;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.BNRenderLayer;
import org.betterx.betternether.config.screen.ConfigScreen;
import org.betterx.betternether.registry.EntityRenderRegistry;
import org.betterx.betternether.registry.NetherParticles;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;

public class BetterNetherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerRenderLayers();
        EntityRenderRegistry.register();

        NetherParticles.register();
        ModMenu.addModMenuScreen(BetterNether.C.modId, ConfigScreen::new);
    }

    /**
     * A block that is neither registered here nor carries a {@code render_type} in its model renders as
     * SOLID, so every cutout/translucent block needs one of the two. BetterNether declares the layer on the
     * block class ({@link org.betterx.betternether.client.IRenderTypeable}), which nothing consumes on its
     * own - bclib's PostInitAPI only walks its own (deprecated) RenderLayerProvider, and BetterNether's
     * blocks descend from its own BlockBase instead.
     * <p>
     * TODO(1.21.7): replace IRenderTypeable with ClientBlockTraits.RENDER_LAYER traits at registration, the
     *  way BetterEnd does it - this loop and bclib's RenderLayerProvider are both on the way out.
     */
    private void registerRenderLayers() {
        BuiltInRegistries.BLOCK.forEach(block -> {
            if (!(block instanceof IRenderTypeable typeable)) return;
            final BNRenderLayer layer = typeable.getRenderLayer();
            if (layer == BNRenderLayer.CUTOUT) BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
            else if (layer == BNRenderLayer.TRANSLUCENT)
                BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
        });
    }
}
