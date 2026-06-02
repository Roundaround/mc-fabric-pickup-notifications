package me.roundaround.pickupnotifications.event;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ItemPickup {
  List<ItemPickup> LISTENERS = new CopyOnWriteArrayList<>();

  static void register(ItemPickup listener) {
    LISTENERS.add(listener);
  }

  static void emit(ItemStack stack) {
    for (ItemPickup listener : LISTENERS) {
      listener.interact(stack);
    }
  }

  void interact(ItemStack stack);
}
