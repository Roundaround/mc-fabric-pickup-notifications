package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ClientModInitializer;

public final class PickupNotificationsMod implements ClientModInitializer {
    public static final ModInfo MOD_INFO = new ModInfo("pickupnotifications", "0.0.1", 1);
    public static final PickupNotificationsConfig CONFIG = new PickupNotificationsConfig();

    private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

    @Override
    public void onInitializeClient() {
        pickupNotificationsHud.init();
        CONFIG.init();
    }
}
