package com.masonwp123.reallights.client.sources;

import com.masonwp123.reallights.RealLights;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

@EventBusSubscriber(modid = RealLights.MODID, value = Dist.CLIENT)
public class ItemEquipmentSource extends EntitySource {

    private final EquipmentSlot slot;
    private final Item item;

    private static final HashMap<EquipmentSlot, ItemStack> playerItems = new HashMap<>(EquipmentSlot.values().length);

    // Handle player client items
    // There isn't a good way to handle this with an event, but doing it on tick should not be that slow.
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            for (var slot : EquipmentSlot.values()) {
                ItemStack itemStack = player.getItemBySlot(slot);
                if (!playerItems.containsKey(slot) || !playerItems.get(slot).equals(itemStack)) {
                    playerItems.put(slot, itemStack);
                    onEntityEquipmentChange(player, slot, itemStack);
                }
            }
        }
    }

    protected ItemEquipmentSource(EquipmentSlot slot, Item item, LivingEntity entity) {
        super(getId(item), entity);
        this.slot = slot;
        this.item = item;
    }

    public static void onEntityEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        // Find if the slot was associated with a light source, if so, remove it
        Stream<EntitySource> sources = getSources(entity);
        ArrayList<ItemEquipmentSource> toRemove = new ArrayList<>();
        sources.filter(source -> source instanceof ItemEquipmentSource)
                .forEach(source -> {
                    ItemEquipmentSource itemEquipmentSource = (ItemEquipmentSource) source;
                    if (slot.equals(itemEquipmentSource.slot)) {
                        toRemove.add(itemEquipmentSource);
                    }
                });
        for (var source : toRemove) {
            removeEntity(source);
        }

        // Add New light source for slot
        Item item = stack.getItem();
        addEntity(getId(item), () -> new ItemEquipmentSource(slot, item, entity));
    }
}
