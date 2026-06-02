package me.roundaround.pickupnotifications.neoforge;

import me.roundaround.pickupnotifications.PickupNotifications;
import me.roundaround.pickupnotifications.client.PickupNotificationsClient;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.neoforge.TroveNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("pickupnotifications")
public final class PickupNotificationsNeoForgeMod {
  public PickupNotificationsNeoForgeMod(IEventBus modBus, ModContainer container) {
    TroveNeoForge.bootstrap(modBus, container);
    PickupNotifications.init();

    modBus.addListener(FMLClientSetupEvent.class, event -> PickupNotificationsClient.initClient());

    container.registerExtensionPoint(IConfigScreenFactory.class,
        (modContainer, parent) -> new ConfigScreen(parent, Constants.MOD_ID, PickupNotificationsConfig.getInstance()));
  }
}
