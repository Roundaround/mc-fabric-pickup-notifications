package me.roundaround.pickupnotifications.network;

import me.roundaround.pickupnotifications.generated.Constants;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Networking {
  private Networking() {
  }

  public static final Identifier EXPERIENCE_PICKUP_S2C = Identifier.fromNamespaceAndPath(
      Constants.MOD_ID,
      "experience_pickup_s2c"
  );
  public static final Identifier ITEM_PICKUP_S2C = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_pickup_s2c");

  public static void registerS2CPayloads() {
    PayloadTypeRegistry.clientboundPlay().register(ExperiencePickupS2C.ID, ExperiencePickupS2C.CODEC);
    PayloadTypeRegistry.clientboundPlay().register(ItemPickupS2C.ID, ItemPickupS2C.CODEC);
  }

  public record ExperiencePickupS2C(int amount) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ExperiencePickupS2C> ID = new CustomPacketPayload.Type<>(
        EXPERIENCE_PICKUP_S2C);
    public static final StreamCodec<RegistryFriendlyByteBuf, ExperiencePickupS2C> CODEC =
        StreamCodec.composite(ByteBufCodecs.INT,
        ExperiencePickupS2C::amount,
        ExperiencePickupS2C::new
    );

    @Override
    @NotNull
    public Type<ExperiencePickupS2C> type() {
      return ID;
    }
  }

  public record ItemPickupS2C(ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ItemPickupS2C> ID = new CustomPacketPayload.Type<>(ITEM_PICKUP_S2C);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemPickupS2C> CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC,
        ItemPickupS2C::stack,
        ItemPickupS2C::new
    );

    @Override
    @NotNull
    public Type<ItemPickupS2C> type() {
      return ID;
    }
  }
}
