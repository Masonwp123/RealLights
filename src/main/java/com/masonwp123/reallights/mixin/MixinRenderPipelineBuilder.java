package com.masonwp123.reallights.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.renderer.ShaderDefines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RenderPipeline.Builder.class)
public abstract class MixinRenderPipelineBuilder {

    @Shadow
    public abstract RenderPipeline.Builder withUniform(String name, UniformType type);

    @Shadow
    public abstract RenderPipeline.Builder withUniform(String name, UniformType type, TextureFormat format);

    @Shadow
    public abstract RenderPipeline.Builder withShaderDefine(String key);

    @Shadow
    private Optional<ShaderDefines.Builder> definesBuilder;

    @Inject(method = "withSampler", at = @At("TAIL"))
    void withSampler(String sampler, CallbackInfoReturnable<RenderPipeline.Builder> cir) {

        // We do this so that any shader that uses Sampler2 (lighting) will be included
        if (!sampler.equals("Sampler2"))
            return;

        // Ensure we don't add the uniforms twice
        if (this.definesBuilder.isPresent() && this.definesBuilder.get().build().flags().contains("USE_REAL_LIGHTS"))
            return;

        // Tell the shader that real lights is enabled
        withShaderDefine("USE_REAL_LIGHTS");

        // Ensure the uniform is added
        withUniform("RealLightsUniform", UniformType.UNIFORM_BUFFER);

        // Mojang does not yet support RGBA32F as a format, so instead we encode our data into RED8I
        withUniform("RealLightsTexelBuffer", UniformType.TEXEL_BUFFER, TextureFormat.RED8I);
    }
}
