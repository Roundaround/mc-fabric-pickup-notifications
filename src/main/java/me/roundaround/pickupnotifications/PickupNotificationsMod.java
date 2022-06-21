package me.roundaround.pickupnotifications;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.network.ItemAddedPacket;
import net.fabricmc.api.ClientModInitializer;

public final class PickupNotificationsMod implements ClientModInitializer {
  public static final String MOD_ID = "pickupnotifications";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final PickupNotificationsConfig CONFIG = new PickupNotificationsConfig();

  private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

  @Override
  public void onInitializeClient() {
    CONFIG.init();
    pickupNotificationsHud.init();

    ItemAddedPacket.registerReceive();
  }
}
