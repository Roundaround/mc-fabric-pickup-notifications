package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.GuiPosition;

public class PickupNotificationsConfig extends ModConfig {
    public PickupNotificationsConfig() {
        super(PickupNotificationsMod.MOD_ID, PickupNotificationsMod.VERSION);

        registerConfigOption(new OptionListConfigOption("alignment", GuiPosition.TOP_LEFT));
    }
}
