package com.masonwp123.reallights.client;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ITEMS = builder.defineList(
                "items",
                List.of(
                        "minecraft:torch,14,#ffd29b,14",
                        "minecraft:lantern,15,#f9af73,15"
                ),
                () -> "",
                value -> value instanceof String
        );

        SPEC = builder.build();
    }

    public static boolean containsIdentifier(Identifier identifier) {
        AtomicBoolean contains = new AtomicBoolean(false);

        ITEMS.get().forEach(str -> {
            String[] items = str.split(",");
            String id = items[0];

            if (!contains.get() && id.equals(identifier.toString())) {
                contains.set(true);
            }
        });

        return contains.get();
    }

    public static @Nonnull RealLight.Properties getIdentifierProperties(Identifier identifier) {

        AtomicReference<RealLight.Properties> properties = new AtomicReference<>();

        ITEMS.get().forEach(str -> {
            String[] items = str.split(",");
            String id = items[0];

            if (id.equals(identifier.toString())) {
                float attenuation = Float.parseFloat(items[1]);
                Color color = new Color(Integer.parseInt(items[2].substring(1), 16));
                float intensity = Float.parseFloat(items[3]);

                properties.set(new RealLight.Properties(attenuation, color, intensity));
            }
        });

        assert properties.get() != null;
        return properties.get();
    }
}