package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.rendering.RealLightsRenderObjects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow
    @Final
    private GameRenderState gameRenderState;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void GameRenderer(CallbackInfo ci) {
        RealLightsRenderObjects.object = new RealLightsRenderObjects();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        if (RealLightsRenderObjects.object != null) {
            RealLightsRenderObjects.object.update(this.gameRenderState.levelRenderState.cameraRenderState.pos);
        }
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void close(CallbackInfo ci) {
        if (RealLightsRenderObjects.object != null) {
            RealLightsRenderObjects.object.close();
        }
    }
}
