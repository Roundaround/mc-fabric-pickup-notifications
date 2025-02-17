package me.roundaround.pickupnotifications.client;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.client.networking.ClientNetworking;
import me.roundaround.pickupnotifications.compat.roundalib.ConfigControlRegister;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PickupNotificationsClientMod implements ClientModInitializer {
  public static final String MOD_ID = "pickupnotifications";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitializeClient() {
    PickupNotificationsConfig.getInstance().init();
    ConfigControlRegister.init();
    PickupNotificationsHud.init();
    ClientNetworking.registerReceivers();
  }
}
