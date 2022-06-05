package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;

public class PickupNotificationsConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT;
  public final IntConfigOption GUI_OFFSET_X;
  public final IntConfigOption GUI_OFFSET_Y;
  public final IntConfigOption MAX_NOTIFICATIONS;

  public PickupNotificationsConfig() {
    super(PickupNotificationsMod.MOD_ID);

    MOD_ENABLED = registerConfigOption(
        BooleanConfigOption
            .builder("modEnabled", "pickupnotifications.mod_enabled.label")
            .setComment("Simple toggle for the mod! Set to false to disable.")
            .build());

    GUI_ALIGNMENT = registerConfigOption(
        OptionListConfigOption
            .builder("guiAlignment", "pickupnotifications.gui_alignment.label", GuiAlignment.TOP_LEFT)
            .setComment("Where to position the notifications. Can be one of\n"
                + " 'top_left', 'top_right', 'bottom_left', or 'bottom_right'.")
            .build());

    GUI_OFFSET_X = registerConfigOption(
        IntConfigOption
            .builder("guiOffsetX", "pickupnotifications.gui_offset_x.label")
            .setDefaultValue(4)
            .setComment("The amount to offset the notifications from the side\n"
                + " of the screen.")
            .build());

    GUI_OFFSET_Y = registerConfigOption(
        IntConfigOption
            .builder("guiOffsetY", "pickupnotifications.gui_offset_y.label")
            .setDefaultValue(4)
            .setComment("The amount to offset the notifications from the top\n"
                + " or bottom of the screen.")
            .build());

    MAX_NOTIFICATIONS = registerConfigOption(
        IntConfigOption
            .builder("maxNotifications", "pickupnotifications.max_notifications.label")
            .setDefaultValue(4)
            .setComment("How many notifications can be on the screen at a time.\n"
                + " Additional notifications will be queued up and shown once\n"
                + " there is room.")
            .build());
  }
}
