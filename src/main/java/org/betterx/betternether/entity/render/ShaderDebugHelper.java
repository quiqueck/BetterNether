package org.betterx.betternether.entity.render;

// TODO(1.21.7): render pipeline overhaul.
//  This was a debug-only helper that loaded a custom GLSL ShaderInstance (via
//  com.mojang.blaze3d.shaders.Program / GlslPreprocessor) and wrapped it in a
//  RenderStateShard.ShaderStateShard to hot-reload a debug RenderType for the
//  firefly glow. The entire ShaderInstance/Program/ShaderStateShard API was removed
//  in the 1.21.5 render-pipeline overhaul (replaced by RenderPipeline), so the old
//  implementation can no longer compile and there is no clean 1:1 mapping.
//  It is unused in production (only RenderPhaseAccessor referenced it, and that
//  reference was already commented out), so the body is stubbed out until/unless the
//  debug shader tooling is re-implemented on top of RenderPipeline.
abstract class ShaderDebugHelper {
    ShaderDebugHelper() {
    }
}
