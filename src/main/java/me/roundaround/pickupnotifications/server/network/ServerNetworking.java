package me.roundaround.pickupnotifications.server.network;

import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void sendItemAdded(ServerPlayerEntity player, ItemStack stack) {
    if (!ServerPlayNetworking.canSend(player, Networking.ItemAddedS2C.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, new Networking.ItemAddedS2C(stack));
  }
}
