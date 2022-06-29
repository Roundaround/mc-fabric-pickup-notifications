package me.roundaround.pickupnotifications.compat.roundalib;

import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry.RegistrationException;
import me.roundaround.roundalib.config.gui.control.OptionListControl;

public class ConfigControlRegister {
  private ConfigControlRegister() {
  }

  public static void init() {
    try {
      ControlFactoryRegistry.registerOptionList(IconAlignment.class, OptionListControl::new);
    } catch (RegistrationException e) {
      // Deal with this later xD
    }
  }
}
