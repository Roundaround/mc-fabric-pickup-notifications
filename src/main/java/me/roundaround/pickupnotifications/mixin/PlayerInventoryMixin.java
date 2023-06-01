package me.roundaround.pickupnotifications.mixin;

import me.roundaround.pickupnotifications.util.CanRegisterScreenCloseItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
  @Shadow
  PlayerEntity player;

  @Inject(method = "offerOrDrop", at = @At(value = "HEAD"))
  public void offerOrDrop(ItemStack stack, CallbackInfo info) {
    if (!(player instanceof ServerPlayerEntity)) {
      return;
    }

    ScreenHandler screenHandler = player.playerScreenHandler;

    if (!(screenHandler instanceof CanRegisterScreenCloseItems)) {
      return;
    }

    ((CanRegisterScreenCloseItems) screenHandler).registerScreenCloseReturns(stack.copy());
  }
}
