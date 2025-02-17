package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.client.PickupNotificationsClientMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.GameScopedFileStore;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.nightconfig.core.Config;

import java.util.Arrays;

public class PickupNotificationsConfig extends ModConfigImpl implements GameScopedFileStore {
  private static PickupNotificationsConfig instance;

  public BooleanConfigOption modEnabled;
  public EnumConfigOption<GuiAlignment> guiAlignment;
  public PositionConfigOption guiOffset;
  public FloatConfigOption guiScale;
  public IntConfigOption maxNotifications;
  public EnumConfigOption<IconAlignment> iconAlignment;
  public BooleanConfigOption showUniqueInfo;
  public BooleanConfigOption renderBackground;
  public FloatConfigOption backgroundOpacity;
  public BooleanConfigOption renderShadow;

  public PickupNotificationsConfig() {
    super(PickupNotificationsClientMod.MOD_ID, 2);
  }

  public static PickupNotificationsConfig getInstance() {
    if (instance == null) {
      instance = new PickupNotificationsConfig();
    }
    return instance;
  }

  @Override
  protected void registerOptions() {
    modEnabled = this.register(BooleanConfigOption.builder(ConfigPath.of("modEnabled"))
        .setDefaultValue(true)
        .setComment("Simple toggle for the mod! Set to false to disable.")
        .build());

    guiAlignment = this.register(EnumConfigOption.builder(ConfigPath.of("guiAlignment"),
            Arrays.asList(GuiAlignment.values())
        )
        .setDefaultValue(GuiAlignment.TOP_LEFT)
        .setComment(
            "Where to position the notifications. Can be one of",
            "'top_left', 'top_right', 'bottom_left', or 'bottom_right'."
        )
        .build());

    guiOffset = this.register(PositionConfigOption.builder(ConfigPath.of("guiOffset"))
        .setDefaultValue(new Position(4, 4))
        .setComment("The amount to offset the notifications from the edge", "of the screen.")
        .build());

    guiScale = this.register(FloatConfigOption.sliderBuilder(ConfigPath.of("guiScale"))
        .setDefaultValue(1)
        .setMinValue(0.1f)
        .setMaxValue(3f)
        .setStep(30)
        .setComment("Scale to render notifications at.")
        .build());

    maxNotifications = this.register(IntConfigOption.builder(ConfigPath.of("maxNotifications"))
        .setDefaultValue(4)
        .setComment(
            "How many notifications can be on the screen at a time.",
            "Additional notifications will be queued up and shown once",
            "there is room."
        )
        .build());

    iconAlignment = this.register(EnumConfigOption.builder(ConfigPath.of("iconAlignment"),
            Arrays.asList(IconAlignment.values())
        )
        .setDefaultValue(IconAlignment.getDefault())
        .setComment(
            "Whether the item icons should appear on the 'left' or",
            "'right' of notifications, always on the 'outside' (left for",
            "left-aligned, right for right-aligned), or always on the",
            "'inside' of notifications."
        )
        .build());

    showUniqueInfo = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("showUniqueInfo"))
        .setDefaultValue(true)
        .setComment("Whether to show custom names, rarity, and enchantments", "in the notifications.")
        .build());

    renderBackground = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("renderBackground"))
        .setDefaultValue(true)
        .setComment("Whether to render the background behind notifications.")
        .build());

    backgroundOpacity = this.register(FloatConfigOption.sliderBuilder(ConfigPath.of("backgroundOpacity"))
        .setDefaultValue(0.5f)
        .setMinValue(0f)
        .setMaxValue(1f)
        .setStep(20)
        .setComment("Opacity of the notification background color.")
        .build());

    renderShadow = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("renderShadow"))
        .setDefaultValue(false)
        .setComment("Whether to render text shadow in notifications.")
        .build());
  }

  @Override
  public boolean performConfigUpdate(int version, Config config) {
    if (version == 1) {
      config.set("pickupnotifications.guiOffset",
          new Position(config.getIntOrElse("pickupnotifications.guiOffsetX", this.guiOffset.getDefaultValue().x()),
              config.getIntOrElse("pickupnotifications.guiOffsetY", this.guiOffset.getDefaultValue().y())
          ).toString()
      );
      return true;
    }
    return false;
  }
}
