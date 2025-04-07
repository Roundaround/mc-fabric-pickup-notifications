package me.roundaround.pickupnotifications.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

public interface ItemPickup {
  Event<ItemPickup> EVENT = EventFactory.createArrayBacked(
      ItemPickup.class, (listeners) -> (itemStack) -> {
        for (ItemPickup listener : listeners) {
          listener.interact(itemStack);
        }
      }
  );

  static void emit(ItemStack stack) {
    EVENT.invoker().interact(stack);
  }

  void interact(ItemStack stack);
}
