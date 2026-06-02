package me.roundaround.pickupnotifications.neoforge.client;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

/**
 * Registers Pickup Notifications' own HUD layer above the vanilla HUD on NeoForge. Isolated in a
 * client-only class (called under a {@code Dist.CLIENT} guard) so a dedicated server never links the
 * client-only {@code RegisterGuiLayersEvent}.
 */
public final class NeoForgeHudLayer {
  private NeoForgeHudLayer() {
  }

  public static void register(IEventBus modBus) {
    modBus.addListener(RegisterGuiLayersEvent.class,
        event -> event.registerAboveAll(PickupNotificationsHud.LAYER_ID, PickupNotificationsHud::renderHud));
  }
}
