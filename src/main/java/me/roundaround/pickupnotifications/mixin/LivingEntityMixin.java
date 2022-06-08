package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
  @Inject(method = "sendPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;sendToOtherNearbyPlayers", shift = At.Shift.BEFORE))
  public void sendPickup(Entity entity, int count, CallbackInfo callbackInfo) {
    // if (!(entity instanceof ItemEntity)) {
    //   return;
    // }

    // ItemEntity itemEntity = ((ItemEntity) entity).copy();
    // PickupNotificationsMod.LOGGER.info("=======================");
    // PickupNotificationsMod.LOGGER.info("sendPickup");
    // PickupNotificationsMod.LOGGER.info(itemEntity);
    // PickupNotificationsMod.LOGGER.info(itemEntity.getStack());
    // PickupNotificationsMod.LOGGER.info(itemEntity.getStack().getCount());
    // PickupNotificationsMod.LOGGER.info(count);
    // PickupNotificationsMod.LOGGER.info("=======================");
  }
}
