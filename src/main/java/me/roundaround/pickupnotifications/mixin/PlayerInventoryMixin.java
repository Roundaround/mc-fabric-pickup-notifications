package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
  private ItemStack cachedItemStack = ItemStack.EMPTY;

  @Shadow
  @Final
  private PlayerEntity player;

  @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"))
  private void onInsertStackHead(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
    // TODO: Only work for ServerPlayerEntity on single player
    // TODO: Only work for ClientPlayerEntity on multi player
    if (player instanceof ServerPlayerEntity) {
      cachedItemStack = stack.copy();
    }
  }

  @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "RETURN"))
  private void onInsertStackReturn(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
    if (player instanceof ServerPlayerEntity && info.getReturnValue()) {
      ItemPickupCallback.EVENT.invoker().interact(cachedItemStack);
    }
  }
}
