package me.roundaround.pickupnotifications.forge.client;

import me.roundaround.pickupnotifications.client.PickupNotificationsClient;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ForgeClient {
  public static void init(FMLJavaModLoadingContext context) {
    AddGuiOverlayLayersEvent.BUS.addListener(event -> event.getLayeredDraw()
        .add(PickupNotificationsHud.LAYER_ID, PickupNotificationsHud::renderHud));

    FMLClientSetupEvent.getBus(context.getModBusGroup()).addListener(_ -> PickupNotificationsClient.initClient());

    context.getContainer().registerExtensionPoint(
        ConfigScreenHandler.ConfigScreenFactory.class,
        () -> new ConfigScreenHandler.ConfigScreenFactory((_, parent) -> new ConfigScreen(
            parent,
            Constants.MOD_ID,
            PickupNotificationsConfig.getInstance()
        ))
    );
  }

  private ForgeClient() {
  }
}
