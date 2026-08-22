package com.masonwp123.reallights.client.sources;

import com.masonwp123.reallights.RealLights;
import com.masonwp123.reallights.client.ClientConfig;
import com.masonwp123.reallights.client.RealLight;
import com.masonwp123.reallights.client.RealLightSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.awt.*;
import java.util.ArrayList;

@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public class EntitySource extends RealLightSource {

    private static final ArrayList<EntitySource> entities = new ArrayList<>();

    @SubscribeEvent
    private static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (ClientConfig.containsIdentifier(getId(entity))) {
            entities.add(new EntitySource(entity));
        }
    }

    @SubscribeEvent
    private static void onEntityRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        entities.removeIf(entitySource -> {
            assert entitySource.light.source() != null;
            if (entitySource.light.source().equals(entity)) {
                entitySource.close();
                return true;
            }
            return false;
        });
    }

    public EntitySource(Entity entity) {
        super(new RealLight(getId(entity), entity));
    }

    private static Identifier getId(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            Item item = itemEntity.getItem().getItem();
            return BuiltInRegistries.ITEM.getKey(item);
        }

        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }
}
