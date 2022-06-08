package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.pickupnotifications.event.ItemPickupCallback;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
  private ItemStack cachedItemStack = ItemStack.EMPTY;

  @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"))
  private void onInsertStackHead(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
    cachedItemStack = stack.copy();
  }

  @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "RETURN"))
  private void onInsertStackReturn(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
    // TODO: Figure out why this is showing 2x water bottles when filling
    if (info.getReturnValue()) {
      ItemPickupCallback.EVENT.invoker().interact(null, cachedItemStack);
    }
  }
}
