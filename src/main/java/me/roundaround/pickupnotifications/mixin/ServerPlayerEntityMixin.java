package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.util.HasServerPlayer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
  @Inject(method = "onScreenHandlerOpened", at = @At(value = "HEAD"))
  public void onScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo info) {
    if (screenHandler instanceof HasServerPlayer) {
      ((HasServerPlayer) screenHandler).setPlayer((ServerPlayerEntity) (Object) this);
    }
  }
}
