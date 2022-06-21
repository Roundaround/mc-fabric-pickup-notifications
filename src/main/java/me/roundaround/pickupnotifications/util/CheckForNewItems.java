package me.roundaround.pickupnotifications.util;

import java.util.HashSet;

import me.roundaround.pickupnotifications.network.ItemAddedPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

public abstract class CheckForNewItems {
  public static void run(
      ScreenHandler screenHandler,
      DefaultedList<ItemStack> previousTrackedStacks,
      ItemStack previousCursorStack,
      ServerPlayerEntity player) {

    InventorySnapshot previous = new InventorySnapshot();
    InventorySnapshot current = new InventorySnapshot();
    HashSet<Integer> playerSlotsWithChanges = new HashSet<>();

    for (int i = 0; i < screenHandler.slots.size(); ++i) {
      ItemStack prevStack = previousTrackedStacks.get(i);
      ItemStack currStack = screenHandler.getSlot(i).getStack();

      if (prevStack.isEmpty() && currStack.isEmpty()) {
        continue;
      }

      if (ItemStack.areEqual(prevStack, currStack)) {
        continue;
      }

      previous.add(prevStack);
      current.add(currStack);

      playerSlotsWithChanges.add(i);
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

    // TODO: When closing a crafting screen, the unused items are returned to
    // player's inventory, causing notifications.

    // TODO: When multicrafting, the results cause notifications.

    InventorySnapshot diff = current.diff(previous);

    for (int i : playerSlotsWithChanges) {
      ItemStack stackChange = screenHandler.getSlot(i).getStack().copy();
      int changed = diff.takeFor(stackChange);
      if (changed > 0) {
        stackChange.setCount(changed);
        ItemAddedPacket.sendToPlayer(player, stackChange);
      }
    }
  }
}
