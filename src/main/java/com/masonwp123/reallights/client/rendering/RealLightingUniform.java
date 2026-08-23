package com.masonwp123.reallights.client.rendering;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RealLightingUniform implements AutoCloseable {
    @Unique
    private static final int UBO_SIZE = new Std140SizeCalculator().putInt().get();
    @Unique
    public final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Real Lighting UBO", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, UBO_SIZE);

    public int num_lights = 0;

    @Nullable
    public static RealLightingUniform uniform;

    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer data = Std140Builder.onStack(stack, UBO_SIZE)
                    .putInt(num_lights)
                    .get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), data);
        }
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}
