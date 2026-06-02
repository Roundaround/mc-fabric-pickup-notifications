package me.roundaround.pickupnotifications.mixin;

import me.roundaround.pickupnotifications.server.network.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
  @Shadow
  public abstract int getValue();

  @Inject(
      method = "playerTouch", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"
  )
  )
  private void onPlayerTouch(Player player, CallbackInfo ci) {
    if (player instanceof ServerPlayer serverPlayer) {
      ServerNetworking.sendExperiencePickup(serverPlayer, this.getValue());
    }
  }
}
