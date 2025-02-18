package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.network.Networking;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PickupNotificationsMod implements ModInitializer {
  public static final String MOD_ID = "pickupnotifications";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    Networking.registerS2CPayloads();
  }
}
