package me.roundaround.pickupnotifications.util;

import net.minecraft.server.network.ServerPlayerEntity;

public interface HasServerPlayer {
  void pickupnotifications$setPlayer(ServerPlayerEntity player);
}
