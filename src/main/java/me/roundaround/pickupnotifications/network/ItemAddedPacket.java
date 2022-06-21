package me.roundaround.pickupnotifications.network;

import io.netty.buffer.Unpooled;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemAddedPacket {
  private static final Identifier ITEM_ADDED_PACKET = new Identifier(
      PickupNotificationsMod.MOD_ID,
      "item_added_packet");

  public static void registerReceive() {
    ClientPlayNetworking.registerGlobalReceiver(ITEM_ADDED_PACKET, ((client, handler, buffer, responseSender) -> {
      ItemStack itemStack = buffer.readItemStack();
      PickupNotificationsMod.LOGGER.debug(String.format("Pickup notification packet received: %s", itemStack));
      ItemPickupCallback.EVENT.invoker().interact(itemStack);
    }));
  }

  public static void sendToPlayer(ServerPlayerEntity player, ItemStack itemStack) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeItemStack(itemStack);
    ServerPlayNetworking.send(player, ITEM_ADDED_PACKET, buf);
  }
}
