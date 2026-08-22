package com.masonwp123.reallights.client.sources;

import com.masonwp123.reallights.RealLights;
import com.masonwp123.reallights.client.ClientConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class HeldItemSource extends EntitySource {

    private final EquipmentSlot slot;
    private final Item item;

    protected HeldItemSource(EquipmentSlot slot, Item item, LivingEntity entity) {
        super(getId(item), entity);
        this.slot = slot;
        this.item = item;
    }

    public static void onEntityEquipmentChange(LivingEntity entity, List<Pair<EquipmentSlot, ItemStack>> slots) {
        // Find if the slot with the HeldItemSource represents has been changed
        // If so, delete it, changed items will be re-added afterward
        Stream<EntitySource> sources = getSources(entity);
        ArrayList<HeldItemSource> toRemove = new ArrayList<>();
        sources.filter(source -> source instanceof HeldItemSource)
            .forEach(source -> {
                HeldItemSource heldItemSource = (HeldItemSource) source;
                var heldSlot = slots.stream().filter(slot -> slot.getFirst().equals(heldItemSource.slot)).findFirst();
                if (heldSlot.isPresent()) {
                    Item item = heldSlot.get().getSecond().getItem();
                    if (!item.equals(heldItemSource.item)) {
                        toRemove.add(heldItemSource);
                    }
                }
            });

        for (var source : toRemove) {
            removeEntity(source);
        }



        // Check each changed slot if one is a light source
        for (var slot : slots) {
            Item item = slot.getSecond().getItem();
            Identifier identifier = BuiltInRegistries.ITEM.getKey(item);
            addEntity(identifier, () -> new HeldItemSource(slot.getFirst(), item, entity));
        }
    }

    protected static Identifier getId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
