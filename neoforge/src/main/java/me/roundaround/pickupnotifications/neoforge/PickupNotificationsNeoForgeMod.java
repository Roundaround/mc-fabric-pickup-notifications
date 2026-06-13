package me.roundaround.pickupnotifications.neoforge;

import me.roundaround.pickupnotifications.neoforge.client.NeoForgeClient;
import me.roundaround.pickupnotifications.network.Networking;
import me.roundaround.trove.neoforge.TroveNeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("pickupnotifications")
public final class PickupNotificationsNeoForgeMod {
  public PickupNotificationsNeoForgeMod(IEventBus modBus, ModContainer container) {
    TroveNeoForge.bootstrap(modBus, container);
    Networking.register();

    if (FMLEnvironment.getDist() == Dist.CLIENT) {
      NeoForgeClient.init(modBus, container);
    }
  }
}
