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

@Mod(value = RealLights.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public class RealLightsClient {
    @EventBusSubscriber(modid = RealLights.MODID)
    public static class ModBusEvents {
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
    }
}
