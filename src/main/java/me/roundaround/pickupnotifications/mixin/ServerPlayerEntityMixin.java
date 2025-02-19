package me.roundaround.pickupnotifications.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
  @Inject(method = "onScreenHandlerOpened", at = @At(value = "HEAD"))
  public void onScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo info) {
    screenHandler.pickupnotifications$setPlayer(this.self());
  }

  @Unique
  private ServerPlayerEntity self() {
    return (ServerPlayerEntity) (Object) this;
  }
}
