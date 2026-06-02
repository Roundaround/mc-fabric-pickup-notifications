package me.roundaround.pickupnotifications;

import me.roundaround.allay.api.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint(Entrypoint.MAIN)
public class PickupNotificationsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    PickupNotifications.init();
  }
}
