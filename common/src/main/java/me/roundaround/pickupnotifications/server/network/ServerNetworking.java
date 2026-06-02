package me.roundaround.pickupnotifications.server.network;

import me.roundaround.pickupnotifications.network.Networking;
import me.roundaround.trove.network.TroveNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void sendExperiencePickup(ServerPlayer player, int amount) {
    if (!TroveNetworking.canSend(player, Networking.ExperiencePickupS2C.ID)) {
      return;
    }
    TroveNetworking.sendToClient(player, new Networking.ExperiencePickupS2C(amount));
  }

  public static void sendItemPickup(ServerPlayer player, ItemStack stack) {
    if (!TroveNetworking.canSend(player, Networking.ItemPickupS2C.ID)) {
      return;
    }
    TroveNetworking.sendToClient(player, new Networking.ItemPickupS2C(stack));
  }
}
