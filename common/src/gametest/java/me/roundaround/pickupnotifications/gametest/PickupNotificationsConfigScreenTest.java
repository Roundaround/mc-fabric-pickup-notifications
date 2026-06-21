package me.roundaround.pickupnotifications.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;

/**
 * Opens the mod's Trove config screen from the title screen and asserts it renders.
 * Exercises the 26.2 GUI-render path on a screen that previews the HUD notifications
 * standalone (see GuiOffsetPositionEditScreen / MockInGameHud) — a render-thread crash
 * there would kill the client and fail the test.
 */
@ClientGameTest
public class PickupNotificationsConfigScreenTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    context.setScreen(() -> new ConfigScreen(null, Constants.MOD_ID, PickupNotificationsConfig.getInstance()));
    context.assertScreen(ConfigScreen.class);
    context.waitTicks(2);
    context.returnToTitle();
  }
}
