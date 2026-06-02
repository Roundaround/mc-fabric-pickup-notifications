package me.roundaround.pickupnotifications.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.pickupnotifications.server.network.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
  @Inject(
      method = "playerTouch", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;I)V"
  )
  )
  // NeoForge/Forge add a second ItemStack local (a copy used for their item-pickup events) that is
  // in scope at awardStat, so an implicit @Local is ambiguous there. ordinal = 0 selects the
  // lowest-slot ItemStack — the working stack being picked up — on all three loaders (Fabric only
  // has that one).
  public void onPlayerTouch(Player player, CallbackInfo ci, @Local(ordinal = 0) ItemStack stack) {
    if (player instanceof ServerPlayer serverPlayer && !stack.isEmpty()) {
      ServerNetworking.sendItemPickup(serverPlayer, stack);
    }
  }
}
