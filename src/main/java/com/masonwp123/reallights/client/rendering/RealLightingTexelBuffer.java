package com.masonwp123.reallights.client.rendering;

import com.masonwp123.reallights.RealLights;
import com.masonwp123.reallights.client.RealLight;
import com.masonwp123.reallights.client.RealLightsClient;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RealLightingTexelBuffer implements AutoCloseable {
    @Unique
    private static final int LIGHT_INFO_SIZE = 32; //Info can fit in two Vec4s (16 each)

    @Unique
    private static final int UTB_SIZE = RealLights.MAX_LIGHTS * LIGHT_INFO_SIZE;
    @Unique
    public final MappableRingBuffer buffer = new MappableRingBuffer(() -> "Real Lighting UTB", GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER | GpuBuffer.USAGE_MAP_WRITE, UTB_SIZE);

    @Nullable
    public static RealLightingTexelBuffer texelBuffer;

    public void update(Vec3 camerapos) {
        this.buffer.rotate();

        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer.currentBuffer(), false, true)) {
            this.buildLightingData(view.data(), camerapos);
        }
    }

    private void buildLightingData(ByteBuffer faceBuffer, Vec3 camerapos) {
        // Ensure Data can be interpreted
        faceBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (RealLight light : RealLightsClient.getLights()) {
            encodeLightingData(faceBuffer, camerapos, light);
        }

        assert RealLightingUniform.uniform != null;
        RealLightingUniform.uniform.num_lights = RealLightsClient.getLights().size();
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

    @Override
    public void close() {
        this.buffer.close();
    }
}
