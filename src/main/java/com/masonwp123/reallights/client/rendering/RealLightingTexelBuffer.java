package com.masonwp123.reallights.client.rendering;

import com.masonwp123.reallights.client.RealLightsClient;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RealLightingTexelBuffer implements AutoCloseable {
    @Unique
    private static final int MAX_LIGHTS = 1024;
    @Unique
    private static final int LIGHT_INFO_SIZE = 32; //Info can fit in two Vec4s (16 each)

    @Unique
    private static final int UTB_SIZE = MAX_LIGHTS * LIGHT_INFO_SIZE;
    @Unique
    public final MappableRingBuffer buffer = new MappableRingBuffer(() -> "Real Lighting UTB", GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER | GpuBuffer.USAGE_MAP_WRITE, UTB_SIZE);

    @Nullable
    public static RealLightingTexelBuffer texelBuffer;

    public void update() {
        this.buffer.rotate();

        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer.currentBuffer(), false, true)) {
            this.buildLightingData(view.data());
        }
    }

    private void buildLightingData(ByteBuffer faceBuffer) {
        // Ensure Data can be interpreted
        faceBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (ItemEntity entity : RealLightsClient.ModBusEvents.lights) {
            encodeLightingData(faceBuffer,
                    entity.position().toVector3f(),
                    14.f,
                    new Vector3f(1.f, 210.f / 255.f, 155.f / 255.f),
                    1.f
            );
        }

        assert RealLightingUniform.uniform != null;
        RealLightingUniform.uniform.num_lights = RealLightsClient.ModBusEvents.lights.size();
    }

    // TODO: use double, otherwise it will break at the world border
    private void encodeLightingData(ByteBuffer buf, Vector3f position, float attenuation, Vector3f color, float intensity) {
        // Texel 0
        buf.putFloat(position.x);
        buf.putFloat(position.y);
        buf.putFloat(position.z);
        buf.putFloat(attenuation);

        // Texel 1
        buf.putFloat(color.x);
        buf.putFloat(color.y);
        buf.putFloat(color.z);
        buf.putFloat(intensity);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}
