package me.roundaround.pickupnotifications.forge.client;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;

/**
 * Registers Pickup Notifications' own HUD layer above the vanilla HUD on Forge. Isolated in a
 * client-only class (called under a {@code Dist.CLIENT} guard) so a dedicated server never links the
 * client-only {@code AddGuiOverlayLayersEvent}. The listener is added at {@code @Mod}-construction
 * time, before that event fires during early Gui setup.
 */
public final class ForgeHudLayer {
  private ForgeHudLayer() {
  }

  public static void register() {
    AddGuiOverlayLayersEvent.BUS.addListener(
        event -> event.getLayeredDraw().add(PickupNotificationsHud.LAYER_ID, PickupNotificationsHud::renderHud));
  }
}
