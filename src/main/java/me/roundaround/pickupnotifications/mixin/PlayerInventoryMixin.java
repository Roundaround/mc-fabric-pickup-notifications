package me.roundaround.pickupnotifications.mixin;

import me.roundaround.pickupnotifications.util.CanRegisterScreenCloseItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
  @Final
  @Shadow
  public PlayerEntity player;

  @Inject(method = "offerOrDrop", at = @At(value = "HEAD"))
  public void offerOrDrop(ItemStack stack, CallbackInfo info) {
    if (!(this.player instanceof ServerPlayerEntity)) {
      return;
    }

    if (this.player.playerScreenHandler instanceof CanRegisterScreenCloseItems handler) {
      handler.pickupnotifications$registerScreenCloseReturns(stack.copy());
    }
  }
}
