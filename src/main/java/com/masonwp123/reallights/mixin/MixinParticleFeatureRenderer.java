package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.rendering.RealLightsRenderObjects;
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
        if (RealLightsRenderObjects.object != null) {
            RealLightsRenderObjects.object.bind(renderPass);
        }
    }
}
