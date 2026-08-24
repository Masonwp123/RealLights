package com.masonwp123.reallights.client;

import com.masonwp123.reallights.RealLights;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public final class ClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS;

    private static final HashMap<Identifier, RealLight.Properties> lightConfig = new HashMap<>();

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("identifier=intensity, color, attenuation");

        ITEMS = builder.defineList(
                "items",
                List.of(
                        "minecraft:torch=14.0, #ffd29b, 14.0",
                        "minecraft:lantern=15.0, #f9af73, 15.0"
                ),
                () -> "",
                value -> value instanceof String
        );

        SPEC = builder.build();
    }

    public static boolean containsIdentifier(Identifier identifier) {
        return lightConfig.containsKey(identifier);
    }

    public static @Nonnull RealLight.Properties getIdentifierProperties(Identifier identifier) {
        assert containsIdentifier(identifier);
        return lightConfig.get(identifier);
    }

    public static void buildConfig(ModContainer container) {
        container.registerConfig(
                ModConfig.Type.CLIENT,
                ClientConfig.SPEC
        );
    }

    @SubscribeEvent
    private static void onConfigLoad(ModConfigEvent.Loading event) {
        updateProperties();
    }

    @SubscribeEvent
    private static void onConfigChanged(ModConfigEvent.Reloading event) {
        updateProperties();
    }

    private static void updateProperties() {
        lightConfig.clear();
        for (var str : ITEMS.get()) {
            // Remove parenthesis and whitespace and split by equals sign
            String[] sanitizedString = str
                    .replace(" ", "")
                    .split("=");

            String identifierStr = sanitizedString[0];


            String[] propertiesStr = sanitizedString[1].split(",");

            float intensity = Float.parseFloat(propertiesStr[0]);
            Color color = new Color(Integer.parseInt(propertiesStr[1].substring(1), 16));
            float attenuation = Float.parseFloat(propertiesStr[2]);

            lightConfig.put(Identifier.parse(identifierStr), new RealLight.Properties(attenuation, color, intensity));
        }

        RealLightsClient.update();
    }
}