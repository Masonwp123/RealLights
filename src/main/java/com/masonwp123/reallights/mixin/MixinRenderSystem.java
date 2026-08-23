package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.rendering.RealLightsRenderObjects;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adding uniforms to this method allows most shaders to get the uniforms
@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "bindDefaultUniforms", at = @At("HEAD"))
    private static void bindDefaultUniforms(RenderPass renderPass, CallbackInfo ci) {
        if (RealLightsRenderObjects.object != null) {
            RealLightsRenderObjects.object.bind(renderPass);
        }
    }
}
