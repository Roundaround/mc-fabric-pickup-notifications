package me.roundaround.pickupnotifications.compat.roundalib;

import me.roundaround.pickupnotifications.client.gui.screen.GuiOffsetPositionEditScreen;
import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.pickupnotifications.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.pickupnotifications.roundalib.config.option.PositionConfigOption;
import me.roundaround.pickupnotifications.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;

public class ConfigControlRegister {
  private static final MinecraftClient client = MinecraftClient.getInstance();

  private ConfigControlRegister() {
  }

  public static void init() {
    try {
      ControlRegistry.registerOptionList(IconAlignment.class);
      ControlRegistry.register(
          PickupNotificationsConfig.getInstance().guiOffset.getId(),
          ConfigControlRegister::guiOffsetEditScreenControlFactory
      );
    } catch (ControlRegistry.RegistrationException e) {
      // Deal with this later xD
    }
  }

  private static SubScreenControl<Position, PositionConfigOption> guiOffsetEditScreenControlFactory(
      MinecraftClient client,
      PositionConfigOption option,
      int width,
      int height
  ) {
    return new SubScreenControl<>(
        client,
        option,
        width,
        height,
        SubScreenControl.getValueDisplayMessageFactory(),
        GuiOffsetPositionEditScreen.getSubScreenFactory()
    );
  }
}
