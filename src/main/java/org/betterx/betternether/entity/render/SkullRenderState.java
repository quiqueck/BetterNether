package org.betterx.betternether.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class SkullRenderState extends LivingEntityRenderState {
    public float swimAmount;
    public boolean rollTooBig;
}
