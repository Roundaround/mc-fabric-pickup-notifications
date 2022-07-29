package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;

public class PickupNotificationsConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT;
  public final PositionConfigOption GUI_OFFSET;
  public final FloatConfigOption GUI_SCALE;
  public final IntConfigOption MAX_NOTIFICATIONS;
  public final OptionListConfigOption<IconAlignment> ICON_ALIGNMENT;
  public final BooleanConfigOption SHOW_UNIQUE_INFO;
  public final BooleanConfigOption RENDER_BACKGROUND;
  public final FloatConfigOption BACKGROUND_OPACITY;
  public final BooleanConfigOption RENDER_SHADOW;

  public PickupNotificationsConfig() {
    super(PickupNotificationsMod.MOD_ID, ModConfig.options(PickupNotificationsMod.MOD_ID).setConfigVersion(2));

    MOD_ENABLED = registerConfigOption(
        BooleanConfigOption
            .builder("modEnabled", "pickupnotifications.mod_enabled.label")
            .setComment("Simple toggle for the mod! Set to false to disable.")
            .build());

    GUI_ALIGNMENT = registerConfigOption(
        OptionListConfigOption
            .builder("guiAlignment", "pickupnotifications.gui_alignment.label", GuiAlignment.TOP_LEFT)
            .setComment("Where to position the notifications. Can be one of",
                "'top_left', 'top_right', 'bottom_left', or 'bottom_right'.")
            .build());

    GUI_OFFSET = registerConfigOption(
        PositionConfigOption
            .builder("guiOffset", "pickupnotifications.gui_offset.label", new Position(4, 4))
            .setComment("The amount to offset the notifications from the edge",
                "of the screen.")
            .build());

    GUI_SCALE = registerConfigOption(
        FloatConfigOption
            .sliderBuilder("guiScale", "pickupnotifications.gui_scale.label")
            .setDefaultValue(1)
            .setMinValue(0.1f)
            .setMaxValue(3f)
            .setStep(30)
            .setComment("Scale to render notifications at.")
            .build());

    MAX_NOTIFICATIONS = registerConfigOption(
        IntConfigOption
            .builder("maxNotifications", "pickupnotifications.max_notifications.label")
            .setDefaultValue(4)
            .setComment("How many notifications can be on the screen at a time.",
                "Additional notifications will be queued up and shown once",
                "there is room.")
            .build());

    ICON_ALIGNMENT = registerConfigOption(
        OptionListConfigOption
            .builder("iconAlignment", "pickupnotifications.icon_alignment.label", IconAlignment.getDefault())
            .setComment("Whether the item icons should appear on the 'left' or",
                "'right' of notifications, always on the 'outside' (left for",
                "left-aligned, right for right-aligned), or always on the",
                "'inside' of notifications.")
            .build());

    SHOW_UNIQUE_INFO = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("showUniqueInfo", "pickupnotifications.show_unique_info.label")
            .setComment("Whether to show custom names, rarity, and enchantments",
                "in the notifications.")
            .build());

    RENDER_BACKGROUND = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("renderBackground", "pickupnotifications.render_background.label")
            .setComment("Whether to render the background behind notifications.")
            .build());

    BACKGROUND_OPACITY = registerConfigOption(
        FloatConfigOption
            .sliderBuilder("backgroundOpacity", "pickupnotifications.background_opacity.label")
            .setDefaultValue(0.5f)
            .setMinValue(0f)
            .setMaxValue(1f)
            .setStep(20)
            .setComment("Opacity of the notification background color.")
            .build());

    RENDER_SHADOW = registerConfigOption(
        BooleanConfigOption
            .yesNoBuilder("renderShadow", "pickupnotifications.render_shadow.label")
            .setDefaultValue(false)
            .setComment("Whether to render text shadow in notifications.")
            .build());
  }

  @Override
  protected boolean updateConfigVersion(int version, Config config) {
    if (version == 1) {
      config.set(
          "pickupnotifications.guiOffset",
          new Position(
              config.getIntOrElse("pickupnotifications.guiOffsetX", GUI_OFFSET.getDefault().x()),
              config.getIntOrElse("pickupnotifications.guiOffsetY", GUI_OFFSET.getDefault().y()))
              .toString());
      return true;
    }
    return false;
  }
}
