package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry;
import me.roundaround.roundalib.config.gui.control.OptionListControl;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry.RegistrationException;
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
  public final OptionListConfigOption<IconAlignment> ICON_ALIGNMENT;
  public final BooleanConfigOption SHOW_UNIQUE_INFO;
  public final BooleanConfigOption RENDER_BACKGROUND;

  public PickupNotificationsConfig() {
    super(PickupNotificationsMod.MOD_ID);

    try {
      ControlFactoryRegistry.registerOptionList(IconAlignment.class, OptionListControl::new);
    } catch (RegistrationException e) {
      // Deal with this later xD
    }

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

    ICON_ALIGNMENT = registerConfigOption(
        OptionListConfigOption
            .builder("iconAlignment", "pickupnotifications.icon_alignment.label", IconAlignment.getDefault())
            .setComment("Whether the item icons should appear on the 'left' or\n"
                + " 'right' of notifications, always on the 'outside' (left for\n"
                + " left-aligned, right for right-aligned), or always on the\n"
                + " 'inside' of notifications.")
            .build());

    SHOW_UNIQUE_INFO = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("showUniqueInfo", "pickupnotifications.show_unique_info.label")
            .setComment("Whether to show custom names, rarity, and enchantments\n"
                + " in the notifications.")
            .build());

    RENDER_BACKGROUND = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("renderBackground", "pickupnotifications.render_background.label")
            .setComment("Whether to render the background behind notifications.")
            .build());
  }
}
