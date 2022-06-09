package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
  @Shadow
  abstract Slot getSlot(int index);

  @Inject(method = "setStackInSlot", at = @At(value = "HEAD"))
  private void setStackInSlot(int index, int revision, ItemStack stack, CallbackInfo info) {
    if (stack.isEmpty()) {
      return;
    }

    Slot slot = getSlot(index);
    if (!(slot.inventory instanceof PlayerInventory)) {
      return;
    }

    ItemStack currentlyHeld = slot.getStack();
    int difference = stack.getCount() - currentlyHeld.getCount();

    if (difference <= 0) {
      return;
    }

    ItemStack itemStack = stack.copy();
    itemStack.setCount(difference);

    PickupNotificationsMod.LOGGER.debug(String.format("Pickup notification from setStackInSlot: %s", itemStack));
    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }
}
