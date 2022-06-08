package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
  private int attemptedAddAmount = 0;

  @Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At(value = "HEAD"))
  private void addStackHead(int slot, ItemStack stack, CallbackInfoReturnable<Integer> info) {
    // attemptedAddAmount = stack.getCount();
  }

  @Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At(value = "RETURN"))
  private void addStackReturn(int slot, ItemStack stack, CallbackInfoReturnable<Integer> info) {
    // int leftOverAmount = info.getReturnValue();
    // int successfullyAdded = attemptedAddAmount - leftOverAmount;

    // if (successfullyAdded <= 0) {
    //   return;
    // }

    // ItemStack pickedUp = stack.copy();
    // pickedUp.setCount(successfullyAdded);

    // ItemPickupCallback.EVENT.invoker().interact(pickedUp);
  }
}
