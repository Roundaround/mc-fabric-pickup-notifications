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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
  @Shadow
  private MinecraftClient client;

  @Shadow
  private ClientWorld world;

  @Inject(method = "onItemPickupAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread", shift = At.Shift.AFTER))
  private void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info) {
    // If any screen is open, onScreenHandlerSlotUpdate isn't fired.
    if (client.player.currentScreenHandler.equals(client.player.playerScreenHandler)) {
      return;
    }

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

    PickupNotificationsMod.LOGGER.debug("===================");
    PickupNotificationsMod.LOGGER.debug("onItemPickupAnimation");
    PickupNotificationsMod.LOGGER.debug(itemStack);
    PickupNotificationsMod.LOGGER.debug("===================");

    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }

  @Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime"))
  private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo info) {
    ClientPlayerEntity playerEntity = client.player;
    ItemStack currentlyHeld = playerEntity.playerScreenHandler.getSlot(packet.getSlot()).getStack();
    int difference = packet.getItemStack().getCount() - currentlyHeld.getCount();

    ItemStack itemStack = packet.getItemStack().copy();
    itemStack.setCount(difference);

    PickupNotificationsMod.LOGGER.debug("===================");
    PickupNotificationsMod.LOGGER.debug("onScreenHandlerSlotUpdate");
    PickupNotificationsMod.LOGGER.debug(itemStack);
    PickupNotificationsMod.LOGGER.debug("===================");

    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }
}
