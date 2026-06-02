package me.roundaround.pickupnotifications.client;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.compat.trove.ConfigControlRegister;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;

public final class PickupNotificationsClient {
  private PickupNotificationsClient() {}

  public static void initClient() {
    PickupNotificationsConfig.getInstance().init();
    ConfigControlRegister.init();
    PickupNotificationsHud.init();
  }
}
