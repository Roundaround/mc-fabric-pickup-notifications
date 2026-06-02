package me.roundaround.pickupnotifications.forge;

import me.roundaround.pickupnotifications.PickupNotifications;
import me.roundaround.pickupnotifications.client.PickupNotificationsClient;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.forge.TroveForge;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("pickupnotifications")
public final class PickupNotificationsForgeMod {
  public PickupNotificationsForgeMod(FMLJavaModLoadingContext context) {
    TroveForge.bootstrap(context);
    PickupNotifications.init();

    FMLClientSetupEvent.getBus(context.getModBusGroup())
        .addListener(event -> PickupNotificationsClient.initClient());

    context.getContainer().registerExtensionPoint(
        ConfigScreenHandler.ConfigScreenFactory.class,
        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
            new ConfigScreen(parent, Constants.MOD_ID, PickupNotificationsConfig.getInstance())));
  }
}
