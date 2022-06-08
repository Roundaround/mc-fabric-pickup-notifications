package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;

@Mixin(ClientPlayNetworkHandlerMixin.class)
public abstract class ClientPlayNetworkHandlerMixin {
  @Shadow
  private ClientWorld world;

  @Shadow
  private MinecraftClient client;

  @Inject(method = "onItemPickupAnimation", at = @At(value = "HEAD"))
  private void onItemPickupAnimationHead(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
    Entity item = world.getEntityById(packet.getEntityId());
    if (item == null || !(item instanceof ItemEntity)) {
      return;
    }

    Entity collector = world.getEntityById(packet.getCollectorEntityId());
    if (collector != null && collector != client.player) {
      return;
    }
  }
}
