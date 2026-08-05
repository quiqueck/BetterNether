package org.betterx.betternether.integrations.wthit;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.BlockCommonPlant;

import net.minecraft.resources.Identifier;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;

public class WailaPlugin implements IWailaClientPlugin {
    static class Options {
        /**
         * The config toggle owned by wthit's built-in vanilla plugin. Since wthit 16 the key is
         * {@code minecraft:plant.crop_progress}; {@code minecraft:crop_progress} only survives as a
         * config-migration alias and is no longer a registered key.
         */
        public static final Identifier CROP_PROGRESS =
                Identifier.withDefaultNamespace("plant.crop_progress");
    }

    @Override
    public void register(IClientRegistrar registrar) {
        BetterNether.C.log.info("Registering Waila-/Wthit-Plugin.");

        registrar.body(NetherPlantProvider.INSTANCE, BlockCommonPlant.class);
    }
}
