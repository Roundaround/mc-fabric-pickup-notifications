package me.roundaround.pickupnotifications.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.gui.screen.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, PickupNotificationsMod.CONFIG);
  }
}
