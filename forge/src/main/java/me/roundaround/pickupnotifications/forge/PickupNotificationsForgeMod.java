package me.roundaround.pickupnotifications.forge;

import me.roundaround.pickupnotifications.forge.client.ForgeClient;
import me.roundaround.pickupnotifications.network.Networking;
import me.roundaround.trove.forge.TroveForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("pickupnotifications")
public final class PickupNotificationsForgeMod {
  public PickupNotificationsForgeMod(FMLJavaModLoadingContext context) {
    TroveForge.bootstrap(context);
    Networking.register();

    if (FMLEnvironment.dist == Dist.CLIENT) {
      ForgeClient.init(context);
    }
  }
}
