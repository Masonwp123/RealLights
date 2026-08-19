package com.masonwp123.reallights.client;

import com.masonwp123.reallights.RealLights;
import com.masonwp123.reallights.client.rendering.RealLightingUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.ArrayList;

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

        public static final ArrayList<ItemEntity> lights = new ArrayList<>();

        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post event) {
            assert RealLightingUniform.uniform != null;
            if (lights.size() >= RealLights.MAX_LIGHTS) return;

            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null) {
                for (Entity entity : clientLevel.entitiesForRendering()) {
                    if (lights.size() >= RealLights.MAX_LIGHTS) break;
                    if (entity instanceof ItemEntity itemEntity) {
                        if (itemEntity.getItem().getItem().equals(Items.TORCH) && !lights.contains(itemEntity)) {
                            lights.add(itemEntity);
                        }
                    }
                }
            }
        }
    }
}
