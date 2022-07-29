package me.roundaround.pickupnotifications.compat.roundalib;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.client.gui.config.GuiOffsetPositionEditScreen;
import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry.RegistrationException;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import net.minecraft.text.Text;
import me.roundaround.roundalib.config.gui.control.OptionListControl;
import me.roundaround.roundalib.config.gui.control.SubScreenControl;

public class ConfigControlRegister {
  private ConfigControlRegister() {
  }

  public static void init() {
    try {
      ControlFactoryRegistry.registerOptionList(IconAlignment.class, OptionListControl::new);
      ControlFactoryRegistry.register(PickupNotificationsMod.CONFIG.GUI_OFFSET.getId(),
          (PositionConfigOption configOption,
              OptionRowWidget parent,
              int top,
              int left,
              int height,
              int width) -> new SubScreenControl<>(
                  GuiOffsetPositionEditScreen.getSubScreenFactory(),
                  configOption,
                  parent,
                  top,
                  left,
                  height,
                  width) {
                @Override
                protected Text getCurrentText() {
                  return Text.literal(configOption.getValue().toString());
                }
              });
    } catch (RegistrationException e) {
      // Deal with this later xD
    }
  }
}
