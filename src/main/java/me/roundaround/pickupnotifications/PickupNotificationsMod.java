package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PickupNotificationsMod implements ClientModInitializer {
    public static final String MOD_ID = "me.roundaround.pickupnotifications";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final PickupNotificationsConfig CONFIG = new PickupNotificationsConfig();

    private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

    @Override
    public void onInitializeClient() {
        pickupNotificationsHud.init();

        CONFIG.saveToFile();
    }
}
