package me.roundaround.pickupnotifications.client;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.client.network.ClientNetworking;
import me.roundaround.pickupnotifications.compat.roundalib.ConfigControlRegister;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.fabricmc.api.ClientModInitializer;

public final class PickupNotificationsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    PickupNotificationsConfig.getInstance().init();
    ConfigControlRegister.init();
    PickupNotificationsHud.init();
    ClientNetworking.registerReceivers();
  }
}
