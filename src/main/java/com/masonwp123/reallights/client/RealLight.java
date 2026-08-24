package com.masonwp123.reallights.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public record RealLight(
        @Nonnull Entity source,
        @Nullable Identifier identifier,
        @Nonnull Properties properties) {

    public RealLight(@Nonnull Identifier identifier, @Nonnull Entity source) {
        this(source, identifier, ClientConfig.getIdentifierProperties(identifier));
    }

    public RealLight(@Nonnull Entity source, float attenuation, Color color, float intensity) {
        this(source, null, new Properties(attenuation, color, intensity));
    }

    // Constructor to 'update' light properties
    public RealLight(@Nonnull RealLight light) {
        this(light.source, light.identifier, ClientConfig.getIdentifierProperties(light.identifier));
    }

    public Vec3 position() {
        return this.source.position();
    }

    public record Properties(float attenuation, Color color, float intensity) { }
}
