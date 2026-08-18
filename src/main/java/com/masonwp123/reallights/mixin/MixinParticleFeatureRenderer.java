package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.rendering.RealLightingTexelBuffer;
import com.masonwp123.reallights.client.rendering.RealLightingUniform;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Particles do not use bindDefaultUniforms in RenderSystem, so this way, we set the uniform manually
@Mixin(ParticleFeatureRenderer.class)
public class MixinParticleFeatureRenderer {

    @Inject(method = "prepareRenderPass", at = @At("TAIL"))
    private void prepareRenderPass(RenderPass renderPass, CallbackInfo ci) {
        if (RealLightingUniform.uniform != null) {
            renderPass.setUniform("RealLightsUniform", RealLightingUniform.uniform.buffer);
        }

        if (RealLightingTexelBuffer.texelBuffer != null) {
            renderPass.setUniform("RealLightsTexelBuffer", RealLightingTexelBuffer.texelBuffer.buffer.currentBuffer());
        }
    }
}
