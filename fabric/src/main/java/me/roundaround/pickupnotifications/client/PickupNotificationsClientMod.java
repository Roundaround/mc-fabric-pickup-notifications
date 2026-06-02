package me.roundaround.pickupnotifications.client;

import me.roundaround.allay.api.Entrypoint;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

@Entrypoint(Entrypoint.CLIENT)
public final class PickupNotificationsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Register this mod's own HUD layer above the vanilla HUD (addLast = above all vanilla elements).
    HudElementRegistry.addLast(PickupNotificationsHud.LAYER_ID, PickupNotificationsHud::renderHud);
    PickupNotificationsClient.initClient();
  }
}
