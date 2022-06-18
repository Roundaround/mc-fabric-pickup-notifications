package me.roundaround.pickupnotifications.util;

import net.minecraft.item.ItemStack;

public record SlotUpdate(int slot, ItemStack prev, ItemStack curr) {
  public int getSlot() {
    return slot;
  }

  public ItemStack getPrevious() {
    return prev;
  }

  public ItemStack getCurrent() {
    return curr;
  }
}
