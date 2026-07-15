package org.betterx.betternether.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class HydrogenJellyfishRenderState extends LivingEntityRenderState {
    // The pulse/leg animation is driven purely by ageInTicks (and wall-clock time),
    // and the entity's dynamic size is carried by the inherited LivingEntityRenderState.scale
    // (populated from EntityHydrogenJellyfish#getScale via the base extractRenderState),
    // so no extra per-frame fields are required here.
}
