package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.rendering.RealLightingTexelBuffer;
import com.masonwp123.reallights.client.rendering.RealLightingUniform;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void GameRenderer(CallbackInfo ci) {
        RealLightingUniform.uniform = new RealLightingUniform();
        RealLightingTexelBuffer.texelBuffer = new RealLightingTexelBuffer();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        if (RealLightingUniform.uniform != null) {
            RealLightingUniform.uniform.update();
        }
        if (RealLightingTexelBuffer.texelBuffer != null) {
            RealLightingTexelBuffer.texelBuffer.update();
        }
    }
}
