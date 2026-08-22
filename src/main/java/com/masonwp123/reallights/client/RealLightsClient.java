package com.masonwp123.reallights.client;

import com.masonwp123.reallights.RealLights;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(value = RealLights.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public class RealLightsClient {
    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(
                Identifier.fromNamespaceAndPath(RealLights.MODID, "resourcepacks/reallights"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Real Lights Resources"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }

    private static final ArrayList<RealLight> lights = new ArrayList<>();

    public static List<RealLight> getLights() { return List.copyOf(lights); }

    public static boolean addLight(RealLight light) {
        if (lights.size() >= RealLights.MAX_LIGHTS) return false;
        lights.add(light);
        //TODO: update lighting buf
        return true;
    }

    public static void removeLight(RealLight light) {
        lights.remove(light);
    }
}
