package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.api.ModInitializer;

public class PickupNotificationsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    Networking.registerS2CPayloads();
  }
}
