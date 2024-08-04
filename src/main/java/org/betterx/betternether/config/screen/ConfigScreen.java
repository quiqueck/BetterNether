package org.betterx.betternether.config.screen;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ConfigScreen extends de.ambertation.wunderlib.ui.vanilla.ConfigScreen {
    public static final ResourceLocation BN_LOGO_LOCATION = BetterNether.C.id("icon.png");

    public ConfigScreen(Screen parent) {
        super(parent, BN_LOGO_LOCATION, Component.translatable("bn_config"), List.of(Configs.CLIENT, Configs.WORLD, Configs.GAME_RULES));
    }

    @Override
    public void onClose() {
        super.onClose();
        Configs.saveConfigs();
    }
}
