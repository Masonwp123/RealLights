package com.masonwp123.reallights.mixin;

import com.masonwp123.reallights.client.sources.ItemEquipmentSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void onEquipItem(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, CallbackInfo ci) {
        ItemEquipmentSource.onEntityEquipmentChange((LivingEntity)(Object)this, slot, stack);
    }
}
