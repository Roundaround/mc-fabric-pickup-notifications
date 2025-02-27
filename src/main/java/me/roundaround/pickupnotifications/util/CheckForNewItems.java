package me.roundaround.pickupnotifications.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class CheckForNewItems {
  public static ArrayList<ItemStack> run(
      ScreenHandler screenHandler,
      DefaultedList<ItemStack> previousTrackedStacks,
      ItemStack previousCursorStack,
      InventorySnapshot extraItemsForPrevious
  ) {
    InventorySnapshot previous = new InventorySnapshot();
    InventorySnapshot current = new InventorySnapshot();
    HashSet<Integer> playerSlotsWithChanges = new HashSet<>();

    for (int i = 0; i < screenHandler.slots.size(); ++i) {
      Inventory inventory = screenHandler.getSlot(i).inventory;
      if (inventory instanceof CraftingResultInventory) {
        continue;
      }

      ItemStack prevStack = previousTrackedStacks.get(i);
      ItemStack currStack = screenHandler.getSlot(i).getStack();

      if (prevStack.isEmpty() && currStack.isEmpty()) {
        continue;
      }

      if (areItemStacksEqualIgnoreDamage(prevStack, currStack)) {
        continue;
      }

      previous.add(prevStack.copy());
      current.add(currStack.copy());

      if (inventory instanceof PlayerInventory && !currStack.isEmpty()) {
        playerSlotsWithChanges.add(i);
      }
    }

    if (playerSlotsWithChanges.isEmpty()) {
      return new ArrayList<>(0);
    }

    if (!previousCursorStack.isEmpty()) {
      previous.add(previousCursorStack.copy());
    }

    if (!screenHandler.getCursorStack().isEmpty()) {
      current.add(screenHandler.getCursorStack().copy());
    }

    if (!extraItemsForPrevious.isEmpty()) {
      previous.addAll(extraItemsForPrevious);
    }

    InventorySnapshot diff = current.diff(previous);
    ArrayList<ItemStack> newItems = new ArrayList<>();

    for (int i : playerSlotsWithChanges) {
      ItemStack stackChange = screenHandler.getSlot(i).getStack().copy();
      if (stackChange.isEmpty()) {
        continue;
      }

      int changed = diff.takeFor(stackChange);
      if (changed > 0) {
        stackChange.setCount(changed);
        newItems.add(stackChange);
      }
    }

    return newItems;
  }

  private static boolean areItemStacksEqualIgnoreDamage(ItemStack a, ItemStack b) {
    ItemStack aCopy = a.copy();
    aCopy.setDamage(0);

    ItemStack bCopy = b.copy();
    bCopy.setDamage(0);

    return ItemStack.areItemsAndComponentsEqual(aCopy, bCopy);
  }
}
