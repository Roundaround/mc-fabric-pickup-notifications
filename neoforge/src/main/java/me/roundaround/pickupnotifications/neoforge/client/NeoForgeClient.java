package me.roundaround.pickupnotifications.neoforge.client;

import me.roundaround.pickupnotifications.client.PickupNotificationsClient;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class NeoForgeClient {
  public static void init(IEventBus modBus, ModContainer container) {
    modBus.addListener(
        RegisterGuiLayersEvent.class,
        event -> event.registerAboveAll(PickupNotificationsHud.LAYER_ID, PickupNotificationsHud::renderHud)
    );

    modBus.addListener(FMLClientSetupEvent.class, event -> PickupNotificationsClient.initClient());

    container.registerExtensionPoint(
        IConfigScreenFactory.class,
        (_, parent) -> new ConfigScreen(parent, Constants.MOD_ID, PickupNotificationsConfig.getInstance())
    );
  }

  private NeoForgeClient() {
  }
}
