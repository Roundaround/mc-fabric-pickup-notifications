package me.roundaround.pickupnotifications.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.PickupNotificationsMod;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

  @Shadow
  private DefaultedList<ItemStack> trackedStacks;

  @Inject(method = "updateTrackedSlot", at = @At(value = "HEAD"))
  private void onScreenHandlerSlotUpdateHead(
      int slot,
      ItemStack stack,
      Supplier<ItemStack> copySupplier,
      CallbackInfo info) {
    // ItemStack tracked = trackedStacks.get(slot);
    // if (!ItemStack.areEqual(tracked, stack)) {
    //   PickupNotificationsMod.LOGGER.info("updateTrackedSlot");
    // }
  }
}
