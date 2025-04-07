package me.roundaround.pickupnotifications.network;

import me.roundaround.pickupnotifications.generated.Constants;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class Networking {
  private Networking() {
  }

  public static final Identifier EXPERIENCE_PICKUP_S2C = Identifier.of(Constants.MOD_ID, "experience_pickup_s2c");
  public static final Identifier ITEM_PICKUP_S2C = Identifier.of(Constants.MOD_ID, "item_pickup_s2c");

  public static void registerS2CPayloads() {
    PayloadTypeRegistry.playS2C().register(ExperiencePickupS2C.ID, ExperiencePickupS2C.CODEC);
    PayloadTypeRegistry.playS2C().register(ItemPickupS2C.ID, ItemPickupS2C.CODEC);
  }

  public record ExperiencePickupS2C(int amount) implements CustomPayload {
    public static final CustomPayload.Id<ExperiencePickupS2C> ID = new CustomPayload.Id<>(EXPERIENCE_PICKUP_S2C);
    public static final PacketCodec<RegistryByteBuf, ExperiencePickupS2C> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER,
        ExperiencePickupS2C::amount,
        ExperiencePickupS2C::new
    );

    @Override
    public Id<ExperiencePickupS2C> getId() {
      return ID;
    }
  }

  public record ItemPickupS2C(ItemStack stack) implements CustomPayload {
    public static final CustomPayload.Id<ItemPickupS2C> ID = new CustomPayload.Id<>(ITEM_PICKUP_S2C);
    public static final PacketCodec<RegistryByteBuf, ItemPickupS2C> CODEC = PacketCodec.tuple(
        ItemStack.PACKET_CODEC,
        ItemPickupS2C::stack,
        ItemPickupS2C::new
    );

    @Override
    public Id<ItemPickupS2C> getId() {
      return ID;
    }
  }
}
