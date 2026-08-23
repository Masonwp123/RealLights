package com.masonwp123.reallights.client.rendering;

import com.masonwp123.reallights.client.RealLight;
import com.masonwp123.reallights.client.RealLightsClient;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RealLightsRenderObjects implements AutoCloseable {
    @Unique
    private static final int LIGHT_INFO_SIZE = 32; //Info can fit in two Vec4s (16 each)

    @Unique
    private static final int UTB_SIZE = com.masonwp123.reallights.RealLights.MAX_LIGHTS * LIGHT_INFO_SIZE;
    @Unique
    public final MappableRingBuffer texelBuffer = new MappableRingBuffer(() -> "Real Lighting UTB", GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER | GpuBuffer.USAGE_MAP_WRITE, UTB_SIZE);

    @Unique
    private static final int UBO_SIZE = new Std140SizeCalculator().putInt().get();
    @Unique
    public final GpuBuffer uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "Real Lighting UBO", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, UBO_SIZE);


    @Nullable
    public static RealLightsRenderObjects object;

    public void update(Vec3 camerapos) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer data = Std140Builder.onStack(stack, UBO_SIZE)
                    .putInt(RealLightsClient.getLights().size())
                    .get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.uniformBuffer.slice(), data);
        }

        this.texelBuffer.rotate();

        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.texelBuffer.currentBuffer(), false, true)) {
            this.buildLightingData(view.data(), camerapos);
        }
    }

    private void buildLightingData(ByteBuffer faceBuffer, Vec3 camerapos) {
        // Ensure Data can be interpreted
        faceBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (RealLight light : RealLightsClient.getLights()) {
            encodeLightingData(faceBuffer, camerapos, light);
        }
    }

    // TODO: use double, otherwise it will break at the world border
    private void encodeLightingData(ByteBuffer buf, Vec3 camerapos, RealLight light) {
        Vector3f position = light.position().subtract(camerapos).toVector3f();
        float attenuation = light.properties().attenuation();
        Color color = light.properties().color();
        float intensity = light.properties().intensity();

        // Texel 0
        buf.putFloat(position.x);
        buf.putFloat(position.y);
        buf.putFloat(position.z);
        buf.putFloat(attenuation);

        // Texel 1
        buf.putFloat(color.getRed() / 255.f);
        buf.putFloat(color.getGreen() / 255.f);
        buf.putFloat(color.getBlue() / 255.f);
        buf.putFloat(intensity / 15.f);
    }

    public void bind(RenderPass renderPass) {
        renderPass.setUniform("RealLightsUniform", uniformBuffer);
        renderPass.setUniform("RealLightsTexelBuffer", texelBuffer.currentBuffer());
    }

    @Override
    public void close() {
        this.uniformBuffer.close();
        this.texelBuffer.close();
    }
}
