package org.betterx.betternether.registry;

import org.betterx.bclib.particles.BCLParticleType;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.particles.BNParticleProvider;

import net.minecraft.core.particles.SimpleParticleType;

public class NetherParticles {
    public static SimpleParticleType BLUE_DRIPPING_OBSIDIAN_TEAR;
    public static SimpleParticleType BLUE_FALLING_OBSIDIAN_TEAR;
    public static SimpleParticleType BLUE_LANDING_OBSIDIAN_TEAR;

    public static SimpleParticleType BLUE_DRIPPING_OBSIDIAN_WEEP;
    public static SimpleParticleType BLUE_FALLING_OBSIDIAN_WEEP;
    public static SimpleParticleType BLUE_LANDING_OBSIDIAN_WEEP;

    public static SimpleParticleType DRIPPING_OBSIDIAN_WEEP;
    public static SimpleParticleType FALLING_OBSIDIAN_WEEP;
    public static SimpleParticleType LANDING_OBSIDIAN_WEEP;

    public static void register() {
        BLUE_DRIPPING_OBSIDIAN_TEAR = BCLParticleType.register(
                BetterNether.C.id("blue_dripping_obsidian_tear"),
                BNParticleProvider.ObsidianTearHangProvider::new
        );

        BLUE_FALLING_OBSIDIAN_TEAR = BCLParticleType.register(
                BetterNether.C.id("blue_falling_obsidian_tear"),
                BNParticleProvider.ObsidianTearFallProvider::new
        );

        BLUE_LANDING_OBSIDIAN_TEAR = BCLParticleType.register(
                BetterNether.C.id("blue_landing_obsidian_tear"),
                BNParticleProvider.ObsidianTearLandProvider::new
        );


        if (Configs.MAIN.getBoolean("particles", "weeping", true)) {
            BLUE_DRIPPING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("blue_dripping_obsidian_weep"),
                    BNParticleProvider.ObsidianWeepHangProvider::new
            );

            BLUE_FALLING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("blue_falling_obsidian_weep"),
                    BNParticleProvider.ObsidianWeepFallProvider::new
            );

            BLUE_LANDING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("blue_landing_obsidian_weep"),
                    BNParticleProvider.ObsidianWeepLandProvider::new
            );


            DRIPPING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("dripping_obsidian_weep"),
                    BNParticleProvider.ObsidianVanillaWeepHangProvider::new
            );

            FALLING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("falling_obsidian_weep"),
                    BNParticleProvider.ObsidianVanillaWeepFallProvider::new
            );

            LANDING_OBSIDIAN_WEEP = BCLParticleType.register(
                    BetterNether.C.id("landing_obsidian_weep"),
                    BNParticleProvider.ObsidianVanillaWeepLandProvider::new
            );
        } else {
            BLUE_DRIPPING_OBSIDIAN_WEEP = BLUE_DRIPPING_OBSIDIAN_TEAR;
            DRIPPING_OBSIDIAN_WEEP = BLUE_DRIPPING_OBSIDIAN_TEAR;

            BLUE_FALLING_OBSIDIAN_WEEP = BLUE_FALLING_OBSIDIAN_TEAR;
            FALLING_OBSIDIAN_WEEP = BLUE_FALLING_OBSIDIAN_TEAR;

            BLUE_LANDING_OBSIDIAN_WEEP = BLUE_LANDING_OBSIDIAN_TEAR;
            LANDING_OBSIDIAN_WEEP = BLUE_LANDING_OBSIDIAN_TEAR;
        }
    }
}
