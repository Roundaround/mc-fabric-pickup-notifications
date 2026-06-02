package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * A display-only notification used by the GUI offset preview screen. Unlike {@link
 * ItemPickupNotification}, it renders a flat item texture and a static name instead of going
 * through an {@link net.minecraft.world.item.ItemStack}. ItemStacks eagerly read their item's data
 * components, which are only bound during a world load; the preview screen is reachable from the
 * title screen (via Mod Menu) where they are still unbound, so a real stack would throw
 * "Components not bound yet" (see {@link MockInGameHud}). The item is referenced only for its
 * (component-free) description id to build the display name.
 */
public class MockItemPickupNotification extends PickupNotification<Void> {
  private static final int ICON_SIZE = 16;

  private final Identifier sprite;
  private final Component name;
  private final int count;

  public MockItemPickupNotification(Item item, Identifier sprite, int count) {
    super(true);
    this.sprite = sprite;
    this.name = Component.translatable(item.getDescriptionId());
    this.count = count;
  }

  @Override
  protected void renderIcon(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
    context.blit(RenderPipelines.GUI_TEXTURED, this.sprite, 0, 0, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
  }

  @Override
  protected Component getFormattedDisplayString(PickupNotificationsConfig config) {
    return Component.literal(this.count + "x ").append(this.name);
  }

  @Override
  protected boolean canAdd(Object value) {
    return false;
  }

  @Override
  protected void add(Void value) {
  }
}
