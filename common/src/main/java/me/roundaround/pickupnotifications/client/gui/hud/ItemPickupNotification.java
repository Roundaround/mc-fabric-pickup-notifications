package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ItemPickupNotification extends PickupNotification<ItemStack> {
  private final ItemStack stack;

  public ItemPickupNotification(ItemStack initialItems) {
    this(initialItems, false);
  }

  public ItemPickupNotification(ItemStack initialItems, boolean timeless) {
    super(timeless);
    this.stack = initialItems.copy();
  }

  @Override
  protected void renderIcon(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
    context.fakeItem(this.stack, 0, 0);
  }

  @Override
  protected Component getFormattedDisplayString(PickupNotificationsConfig config) {
    MutableComponent name = Component.empty().append(this.stack.getHoverName());
    if (config.showUniqueInfo.getPendingValue()) {
      name.withStyle(this.stack.getRarity().color());
      if (this.stack.get(DataComponents.CUSTOM_NAME) != null) {
        name.withStyle(ChatFormatting.ITALIC);
      }
    }
    return Component.literal(this.stack.getCount() + "x ").append(name);
  }

  @Override
  protected boolean canAdd(Object value) {
    return value instanceof ItemStack addition && areItemStacksMergeable(this.stack, addition);
  }

  @Override
  protected void add(ItemStack addition) {
    this.stack.grow(addition.getCount());
  }

  private static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.isSameItemSameComponents(a, b);
  }
}
