package com.masonwp123.reallights.client.sources;

import com.masonwp123.reallights.RealLights;
import com.masonwp123.reallights.client.ClientConfig;
import com.masonwp123.reallights.client.RealLight;
import com.masonwp123.reallights.client.RealLightSource;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.neoforged.neoforge.event.level.LevelEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public class EntitySource extends RealLightSource {

    private static final ArrayList<EntitySource> entities = new ArrayList<>();

    // When the player leaves a level, remove all sources associated.
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel) {
            for (var source : entities) {
                source.close();
            }
            entities.clear();
        }
    }

    @SubscribeEvent
    private static void onEntityAdded(EntityJoinLevelEvent event) {
        // Don't waste time trying to add an item entity when it's data hasn't yet been syncronized
        if (event.getEntity() instanceof ItemEntity) {
            return;
        }

        addEntity(event.getEntity());
    }

    // If an item entity is updated or changed, delete and readd it
    public static void onItemEntityUpdated(ItemEntity entity) {
        removeEntity(entity);
        if (getSources(entity).findFirst().isEmpty()) {
            addEntity(entity);
        }
    }

    @SubscribeEvent
    private static void onEntityRemoved(EntityLeaveLevelEvent event) {
        removeEntity(event.getEntity());
    }

    protected EntitySource(Identifier identifier, Entity entity) {
        super(new RealLight(identifier, entity));
    }

    protected EntitySource(Entity entity) {
        this(getId(entity), entity);
    }

    protected static Stream<EntitySource> getSources(Entity entity) {
        return entities.stream().filter(entitySource -> {
            assert entitySource.light.source() != null;
            return entitySource.light.source().equals(entity);
        });
    }

    protected static void addEntity(Identifier identifier, Supplier<EntitySource> entitySource) {
        if (ClientConfig.containsIdentifier(identifier)) {
            entities.add(entitySource.get());
        }
    }

    protected static void addEntity(Entity entity) {
        addEntity(getId(entity), () -> new EntitySource(entity));
    }

    protected static boolean removeEntity(EntitySource entity) {
        if (!entities.contains(entity)) return false;
        entity.close();
        return entities.remove(entity);
    }

    protected static boolean removeEntity(Entity entity) {
        return entities.removeIf(entitySource -> {
            assert entitySource.light.source() != null;
            if (entitySource.light.source().equals(entity)) {
                entitySource.close();
                return true;
            }
            return false;
        });
    }

    protected static Identifier getId(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            Item item = itemEntity.getItem().getItem();
            return BuiltInRegistries.ITEM.getKey(item);
        }

        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }
}
