package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.sources.EntitySource;
import com.masonwp123.reallights.client.sources.HeldItemSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleSetEquipment", at = @At("TAIL"))
    private void onEntityEquip(ClientboundSetEquipmentPacket packet, CallbackInfo ci) {
        if (this.level.getEntity(packet.getEntity()) instanceof LivingEntity livingEntity) {
            HeldItemSource.onEntityEquipmentChange(livingEntity, packet.getSlots());
        }
    }

    @Inject(method = "handleSetEntityData", at = @At("TAIL"))
    private void onSetEntityData(ClientboundSetEntityDataPacket packet, CallbackInfo ci) {
        if (this.level.getEntity(packet.id()) instanceof ItemEntity itemEntity) {
            EntitySource.onItemEntityUpdated(itemEntity);
        }
    }
}
