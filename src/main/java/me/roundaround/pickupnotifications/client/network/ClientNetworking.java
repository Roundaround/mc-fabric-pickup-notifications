package me.roundaround.pickupnotifications.client.network;

import me.roundaround.pickupnotifications.event.ExperiencePickup;
import me.roundaround.pickupnotifications.event.ItemPickup;
import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(
        Networking.ExperiencePickupS2C.ID,
        ClientNetworking::handleExperiencePickup
    );
    ClientPlayNetworking.registerGlobalReceiver(Networking.ItemPickupS2C.ID, ClientNetworking::handleItemPickup);
  }

  private static void handleExperiencePickup(
      Networking.ExperiencePickupS2C payload,
      ClientPlayNetworking.Context context
  ) {
    context.client().execute(() -> ExperiencePickup.emit(payload.amount()));
  }

  private static void handleItemPickup(Networking.ItemPickupS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ItemPickup.emit(payload.stack()));
  }
}
