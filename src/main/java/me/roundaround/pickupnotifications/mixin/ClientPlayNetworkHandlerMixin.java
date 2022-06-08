package me.roundaround.pickupnotifications.mixin;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
  Set<Integer> handledItemEntities = new HashSet<>();

  @Shadow
  private ClientWorld world;

  @Shadow
  private MinecraftClient client;

  @Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "HEAD"))
  private void onScreenHandlerSlotUpdateHead(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo info) {
    // PickupNotificationsMod.LOGGER.info("onScreenHandlerSlotUpdate");
  }

  @Inject(method = "onItemPickupAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread", shift = At.Shift.AFTER))
  private void onItemPickupAnimationHead(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
    Entity entity = world.getEntityById(packet.getCollectorEntityId());
    if (!(entity instanceof ClientPlayerEntity)) {
      return;
    }

    Entity itemEntity = world.getEntityById(packet.getEntityId());
    if (!(itemEntity instanceof ItemEntity)) {
      return;
    }

    PickupNotificationsMod.LOGGER.info("=======================");
    PickupNotificationsMod.LOGGER.info("onItemPickupAnimation");
    PickupNotificationsMod.LOGGER.info(((ItemEntity) itemEntity).getStack().getItem());
    PickupNotificationsMod.LOGGER.info(packet.getStackAmount());
    PickupNotificationsMod.LOGGER.info("=======================");

    ItemStack itemStack = ((ItemEntity) itemEntity).getStack().copy();
    itemStack.setCount(packet.getStackAmount());
    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }
}
