package com.masonwp123.reallights.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public record RealLight(
        @Nullable Entity source,
        @Nullable Identifier identifier,
        @Nullable Vec3 position,

        Properties properties) {

    public RealLight(@Nonnull Identifier identifier, @Nonnull Entity source) {
        this(source, identifier, null, ClientConfig.getIdentifierProperties(identifier));
    }

    public RealLight(@Nonnull Entity source, float attenuation, Color color, float intensity) {
        this(source, null, null, new Properties(attenuation, color, intensity));
    }

    public RealLight(@Nonnull Vec3 position, float attenuation, Color color, float intensity) {
        this(null, null, position, new Properties(attenuation, color, intensity));
    }

    @Override
    public Vec3 position() {
        if (this.source != null) {
            return this.source.position();
        }
        return this.position;
    }

    public record Properties(float attenuation, Color color, float intensity) { }
}
