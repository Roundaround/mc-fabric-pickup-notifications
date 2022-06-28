package me.roundaround.pickupnotifications.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

public class InventorySnapshot implements Iterable<ItemStack> {
  private final HashMap<String, ArrayList<ItemStack>> internal = new HashMap<>();

  public void add(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }

    String key = stack.getItem().getTranslationKey();

    if (!internal.containsKey(key)) {
      internal.put(key, new ArrayList<>());
    }

    boolean merged = false;
    for (ItemStack existing : internal.get(key)) {
      if (areItemStacksMergeable(existing, stack)) {
        existing.increment(stack.getCount());
        merged = true;
      }
    }

    if (!merged) {
      internal.get(key).add(stack.copy());
    }
  }

  public void addAll(Iterable<ItemStack> stacks) {
    for (ItemStack stack : stacks) {
      add(stack.copy());
    }
  }

  public boolean isEmpty() {
    return internal.isEmpty();
  }

  public void clear() {
    internal.clear();
  }

  public int size() {
    return internal.values().stream().mapToInt(ArrayList::size).sum();
  }

  public int getCount(ItemStack source) {
    if (source.isEmpty()) {
      return 0;
    }

    String key = source.getItem().getTranslationKey();

    if (!internal.containsKey(key)) {
      return 0;
    }

    for (ItemStack stack : internal.get(key)) {
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

    if (!internal.containsKey(key)) {
      return 0;
    }

    ArrayList<ItemStack> stacks = internal.get(key);
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
  public Iterator<ItemStack> iterator() {
    return internal.values()
        .stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList())
        .iterator();
  }

  public static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.canCombine(a, b);
  }
}
