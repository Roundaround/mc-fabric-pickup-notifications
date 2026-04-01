package me.roundaround.pickupnotifications.compat.roundalib;

import me.roundaround.pickupnotifications.client.gui.screen.GuiOffsetPositionEditScreen;
import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.Minecraft;

public final class ConfigControlRegister {
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
      Minecraft minecraft,
      PositionConfigOption option,
      int width,
      int height
  ) {
    return new SubScreenControl<>(
        minecraft,
        option,
        width,
        height,
        SubScreenControl.getValueDisplayMessageFactory(),
        GuiOffsetPositionEditScreen.getSubScreenFactory()
    );
  }

  private ConfigControlRegister() {
  }
}
