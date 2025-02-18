package me.roundaround.pickupnotifications.network;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class Networking {
  private Networking() {
  }

  public static final Identifier ITEM_ADDED_S2C = Identifier.of(PickupNotificationsMod.MOD_ID, "item_added_s2c");

  public static void registerS2CPayloads() {
    PayloadTypeRegistry.playS2C().register(ItemAddedS2C.ID, ItemAddedS2C.CODEC);
  }

  public record ItemAddedS2C(ItemStack stack) implements CustomPayload {
    public static final CustomPayload.Id<ItemAddedS2C> ID = new CustomPayload.Id<>(ITEM_ADDED_S2C);
    public static final PacketCodec<RegistryByteBuf, ItemAddedS2C> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC,
        ItemAddedS2C::stack,
        ItemAddedS2C::new
    );

    @Override
    public Id<ItemAddedS2C> getId() {
      return ID;
    }
  }
}
