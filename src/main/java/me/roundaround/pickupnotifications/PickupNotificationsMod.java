package me.roundaround.pickupnotifications;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import net.fabricmc.api.ClientModInitializer;

public class PickupNotificationsMod implements ClientModInitializer {
    private final PickupNotificationsHud pickupNotificationsHud = new PickupNotificationsHud();

    @Override
    public void onInitializeClient() {
        pickupNotificationsHud.init();
    }
}
