package org.betterx.betternether.client;

import org.betterx.bclib.integration.modmenu.ModMenu;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.screen.ConfigScreen;
import org.betterx.betternether.registry.EntityRenderRegistry;
import org.betterx.betternether.registry.NetherParticles;

import net.fabricmc.api.ClientModInitializer;

public class BetterNetherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderRegistry.register();

        NetherParticles.register();
        ModMenu.addModMenuScreen(BetterNether.C.modId, ConfigScreen::new);
    }
}
