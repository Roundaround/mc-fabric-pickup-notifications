package me.roundaround.pickupnotifications.mixin;

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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
  @Shadow
  MinecraftClient client;

  @Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onSlotUpdate(Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.BEFORE), cancellable = true)
  public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo info) {
    if (packet.getSyncId() == -10) {
      ItemStack stack = packet.getItemStack();
      int index = packet.getSlot();

      if (stack.isEmpty()) {
        return;
      }

      ClientPlayerEntity player = client.player;
      Slot slot = player.playerScreenHandler.getSlot(index);

      if (!(slot.inventory instanceof PlayerInventory)) {
        return;
      }

      ItemStack currentlyHeld = slot.getStack();
      boolean areMergeable = currentlyHeld.isEmpty() || ItemStack.canCombine(stack, currentlyHeld);

      if (!areMergeable) {
        return;
      }

      int difference = stack.getCount() - currentlyHeld.getCount();

      if (difference <= 0) {
        return;
      }

      ItemStack itemStack = stack.copy();
      itemStack.setCount(difference);

      PickupNotificationsMod.LOGGER.debug(String.format("Pickup notification from setStackInSlot: %s", itemStack));
      ItemPickupCallback.EVENT.invoker().interact(itemStack);

      info.cancel();
    }
  }
}
