package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.fabricmc.api.ClientModInitializer;

public final class PickupNotificationsMod implements ClientModInitializer {
  public static final String MOD_ID = "pickupnotifications";
  public static final PickupNotificationsConfig CONFIG = new PickupNotificationsConfig();

  private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

  @Override
  public void onInitializeClient() {
    CONFIG.init();
    pickupNotificationsHud.init();
  }
}
