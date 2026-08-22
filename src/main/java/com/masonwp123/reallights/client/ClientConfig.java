package com.masonwp123.reallights.client;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;

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
        return ITEMS.get().stream().anyMatch(str -> {
            String[] items = str.split(",");
            return items[0].equals(identifier.toString());
        });
    }

    public static @Nonnull RealLight.Properties getIdentifierProperties(Identifier identifier) {

        assert containsIdentifier(identifier);

        Optional<RealLight.Properties> properties = Optional.empty();

        for (var str : ITEMS.get()) {
            String[] items = str.split(",");
            String id = items[0];

            if (id.equals(identifier.toString())) {
                float attenuation = Float.parseFloat(items[1]);
                Color color = new Color(Integer.parseInt(items[2].substring(1), 16));
                float intensity = Float.parseFloat(items[3]);

                properties = Optional.of(new RealLight.Properties(attenuation, color, intensity));
            }
        }

        assert properties.isPresent();
        return properties.get();
    }
}