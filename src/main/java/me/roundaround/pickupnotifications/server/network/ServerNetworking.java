package me.roundaround.pickupnotifications.server.network;

import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void sendExperiencePickup(ServerPlayerEntity player, int amount) {
    if (!ServerPlayNetworking.canSend(player, Networking.ExperiencePickupS2C.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, new Networking.ExperiencePickupS2C(amount));
  }

  public static void sendItemPickup(ServerPlayerEntity player, ItemStack stack) {
    if (!ServerPlayNetworking.canSend(player, Networking.ItemPickupS2C.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, new Networking.ItemPickupS2C(stack));
  }
}
