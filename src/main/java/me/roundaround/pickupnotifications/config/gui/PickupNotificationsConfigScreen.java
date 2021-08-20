package me.roundaround.pickupnotifications.config.gui;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.gui.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

public class PickupNotificationsConfigScreen extends ConfigScreen {
    public PickupNotificationsConfigScreen(@Nullable Screen parent) {
        super(parent, PickupNotificationsMod.CONFIG);
    }

    @Override
    protected void init() {
    }
}
