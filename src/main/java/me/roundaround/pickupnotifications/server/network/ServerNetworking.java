package me.roundaround.pickupnotifications.server.network;

import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void sendExperiencePickup(ServerPlayer player, int amount) {
    if (!ServerPlayNetworking.canSend(player, Networking.ExperiencePickupS2C.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, new Networking.ExperiencePickupS2C(amount));
  }

  public static void sendItemPickup(ServerPlayer player, ItemStack stack) {
    if (!ServerPlayNetworking.canSend(player, Networking.ItemPickupS2C.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, new Networking.ItemPickupS2C(stack));
  }
}
