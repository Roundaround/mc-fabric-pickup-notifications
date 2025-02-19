package me.roundaround.pickupnotifications.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class InventorySnapshot implements Iterable<ItemStack> {
  private final HashMap<String, ArrayList<ItemStack>> internal = new HashMap<>();

  public void add(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }

    String key = stack.getItem().getTranslationKey();

    if (!this.internal.containsKey(key)) {
      this.internal.put(key, new ArrayList<>());
    }

    boolean merged = false;
    for (ItemStack existing : this.internal.get(key)) {
      if (areItemStacksMergeable(existing, stack)) {
        existing.increment(stack.getCount());
        merged = true;
      }
    }

    if (!merged) {
      this.internal.get(key).add(stack.copy());
    }
  }

  public void addAll(Iterable<ItemStack> stacks) {
    for (ItemStack stack : stacks) {
      this.add(stack.copy());
    }
  }

  public boolean isEmpty() {
    return this.internal.isEmpty();
  }

  public void clear() {
    this.internal.clear();
  }

  public int size() {
    return this.internal.values().stream().mapToInt(ArrayList::size).sum();
  }

  public int getCount(ItemStack source) {
    if (source.isEmpty()) {
      return 0;
    }

    String key = source.getItem().getTranslationKey();

    if (!this.internal.containsKey(key)) {
      return 0;
    }

    for (ItemStack stack : this.internal.get(key)) {
      if (areItemStacksMergeable(stack, source)) {
        return stack.getCount();
      }
    }

    return 0;
  }

  public int takeFor(ItemStack source) {
    if (source.isEmpty()) {
      return 0;
    }

    String key = source.getItem().getTranslationKey();

    if (!this.internal.containsKey(key)) {
      return 0;
    }

    ArrayList<ItemStack> stacks = this.internal.get(key);
    int index = -1;
    int count = 0;

    for (int i = 0; i < stacks.size(); i++) {
      ItemStack stack = stacks.get(i);
      if (areItemStacksMergeable(source, stack)) {
        index = i;
        count = stack.getCount();
        break;
      }
    }

    if (index > -1) {
      stacks.remove(index);
    }

    return count;
  }

  public InventorySnapshot diff(InventorySnapshot other) {
    InventorySnapshot result = new InventorySnapshot();

    for (ItemStack stack : InventorySnapshot.this) {
      ItemStack diff = stack.copy();
      diff.decrement(other.takeFor(diff));
      result.add(diff);
    }

    for (ItemStack stack : other) {
      ItemStack diff = stack.copy();
      diff.setCount(-diff.getCount());
      result.add(diff);
    }

    return result;
  }

  @Override
  public @NotNull Iterator<ItemStack> iterator() {
    return this.internal.values().stream().flatMap(Collection::stream).toList().iterator();
  }

  public static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.areItemsAndComponentsEqual(a, b);
  }
}
