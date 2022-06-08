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
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
  Set<Integer> handledItemEntities = new HashSet<>();

  @Shadow
  private ClientWorld world;

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

    ItemStack itemStack = ((ItemEntity) itemEntity).getStack().copy();
    itemStack.setCount(packet.getStackAmount());
    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }
}
