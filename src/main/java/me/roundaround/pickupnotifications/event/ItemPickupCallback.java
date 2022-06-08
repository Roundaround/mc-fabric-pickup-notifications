package me.roundaround.pickupnotifications.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

public interface ItemPickupCallback {
  Event<ItemPickupCallback> EVENT = EventFactory.createArrayBacked(ItemPickupCallback.class,
      (listeners) -> (itemStack) -> {
        for (ItemPickupCallback listener : listeners) {
          listener.interact(itemStack);
        }
      });

  void interact(ItemStack itemStack);
}
