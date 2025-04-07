package me.roundaround.pickupnotifications.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.pickupnotifications.server.network.ServerNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
  @Inject(
      method = "onPlayerCollision", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/stat/Stat;I)V"
  )
  )
  public void onPlayerPickup(PlayerEntity player, CallbackInfo ci, @Local ItemStack stack) {
    if (player instanceof ServerPlayerEntity serverPlayer && !stack.isEmpty()) {
      ServerNetworking.sendItemAdded(serverPlayer, stack);
    }
  }
}
