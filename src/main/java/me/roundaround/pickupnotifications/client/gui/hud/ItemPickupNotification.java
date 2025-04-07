package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
  protected void renderIcon(DrawContext context, RenderTickCounter tickCounter) {
    context.drawItemWithoutEntity(this.stack, 0, 0);
  }

  @Override
  protected Text getFormattedDisplayString(PickupNotificationsConfig config) {
    MutableText name = Text.empty().append(this.stack.getName());
    if (config.showUniqueInfo.getPendingValue()) {
      name.formatted(this.stack.getRarity().getFormatting());
      if (this.stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
        name.formatted(Formatting.ITALIC);
      }
    }
    return Text.literal(this.stack.getCount() + "x ").append(name);
  }

  @Override
  protected boolean canAdd(Object value) {
    return value instanceof ItemStack addition && areItemStacksMergeable(this.stack, addition);
  }

  @Override
  protected void add(ItemStack addition) {
    this.stack.increment(addition.getCount());
  }

  private static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.areItemsAndComponentsEqual(a, b);
  }
}
