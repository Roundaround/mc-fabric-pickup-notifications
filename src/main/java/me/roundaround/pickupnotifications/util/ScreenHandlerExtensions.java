package me.roundaround.pickupnotifications.util;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ScreenHandlerExtensions {
  default void pickupnotifications$setPlayer(ServerPlayerEntity player) {
  }

  default void pickupnotifications$registerScreenCloseReturns(ItemStack stack) {
  }
}
