package me.roundaround.pickupnotifications.util;

import java.util.HashSet;

import me.roundaround.pickupnotifications.network.ItemAddedPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

public abstract class CheckForNewItems {
  public static void run(
      ScreenHandler screenHandler,
      DefaultedList<ItemStack> previousTrackedStacks,
      ItemStack previousCursorStack,
      InventorySnapshot extraItemsForPrevious,
      ServerPlayerEntity player) {

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

      previous.add(prevStack);
      current.add(currStack);

      if (inventory instanceof PlayerInventory && !currStack.isEmpty()) {
        playerSlotsWithChanges.add(i);
      }
    }

    if (playerSlotsWithChanges.isEmpty()) {
      return;
    }

    if (!previousCursorStack.isEmpty()) {
      previous.add(previousCursorStack.copy());
    }

    if (!screenHandler.getCursorStack().isEmpty()) {
      current.add(screenHandler.getCursorStack());
    }

    if (!extraItemsForPrevious.isEmpty()) {
      previous.addAll(extraItemsForPrevious);
    }

    InventorySnapshot diff = current.diff(previous);

    for (int i : playerSlotsWithChanges) {
      ItemStack stackChange = screenHandler.getSlot(i).getStack().copy();
      if (stackChange.isEmpty()) {
        continue;
      }

      int changed = diff.takeFor(stackChange);
      if (changed > 0) {
        stackChange.setCount(changed);
        ItemAddedPacket.sendToPlayer(player, stackChange);
      }
    }
  }

  private static boolean areItemStacksEqualIgnoreDamage(ItemStack a, ItemStack b) {
    ItemStack aCopy = a.copy();
    a.setDamage(0);

    ItemStack bCopy = b.copy();
    b.setDamage(0);

    return ItemStack.areEqual(aCopy, bCopy);
  }
}
