package me.roundaround.pickupnotifications.client.network;

import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(Networking.ItemAddedS2C.ID, ClientNetworking::handleItemAdded);
  }

  private static void handleItemAdded(Networking.ItemAddedS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ItemPickupCallback.EVENT.invoker().interact(payload.stack()));
  }
}
