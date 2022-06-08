package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;

@Mixin(ServerPlayerEntity.class)
@SuppressWarnings("unchecked")
public abstract class ServerPlayerEntityMixin {
  @Inject(method = "increaseStat(Lnet/minecraft/stat/Stat;I)V", at = @At(value = "HEAD"))
  public void increaseStat(Stat<?> stat, int amount, CallbackInfo callbackInfo) {
    if (!stat.getType().equals(Stats.PICKED_UP)) {
      return;
    }

    Stat<Item> pickedUpStat = (Stat<Item>) stat;
    Item item = pickedUpStat.getValue();
    ItemStack itemStack = new ItemStack(item, amount);

    PickupNotificationsMod.LOGGER.info("=======================");
    PickupNotificationsMod.LOGGER.info("increaseStat");
    PickupNotificationsMod.LOGGER.info(item);
    PickupNotificationsMod.LOGGER.info(amount);
    PickupNotificationsMod.LOGGER.info(itemStack);
    PickupNotificationsMod.LOGGER.info("=======================");

    ItemPickupCallback.EVENT.invoker().interact(itemStack);
  }
}
