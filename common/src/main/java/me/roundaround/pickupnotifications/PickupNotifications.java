package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.network.Networking;

public final class PickupNotifications {
  private PickupNotifications() {}

  public static void init() {
    Networking.register();
  }
}
