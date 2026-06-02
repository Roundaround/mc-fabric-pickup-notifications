package me.roundaround.pickupnotifications.client;

import me.roundaround.allay.api.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public final class PickupNotificationsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    PickupNotificationsClient.initClient();
  }
}
