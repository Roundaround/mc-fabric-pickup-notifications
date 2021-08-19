package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PickupNotificationsMod implements ClientModInitializer {
    public static final String MOD_ID = "me.roundaround.pickupnotifications";
    public static final String VERSION = "1.0.0";

    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, VERSION, 1, "me.roundaround.pickupnotifications.modname", "me.roundaround.pickupnotifications.config.title");

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final PickupNotificationsConfig CONFIG = new PickupNotificationsConfig();

    private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

    @Override
    public void onInitializeClient() {
        pickupNotificationsHud.init();
        CONFIG.init();
    }
}
