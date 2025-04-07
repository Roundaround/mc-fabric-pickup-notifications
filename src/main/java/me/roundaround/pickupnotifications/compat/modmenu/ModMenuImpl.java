package me.roundaround.pickupnotifications.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.pickupnotifications.roundalib.client.gui.screen.ConfigScreen;

@Entrypoint(Entrypoint.MOD_MENU)
public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, Constants.MOD_ID, PickupNotificationsConfig.getInstance());
  }
}
