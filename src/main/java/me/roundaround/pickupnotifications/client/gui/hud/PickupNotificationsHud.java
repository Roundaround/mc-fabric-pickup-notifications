package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import me.roundaround.pickupnotifications.generated.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class PickupNotificationsHud {
  private static final PickupNotificationsHud INSTANCE = new PickupNotificationsHud();

  private final CopyOnWriteArrayList<PickupNotificationLine> currentlyShownNotifications = new CopyOnWriteArrayList<>();
  private final ConcurrentLinkedDeque<PickupNotificationLine> notificationQueue = new ConcurrentLinkedDeque<>();

  public static void init() {
    ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::tick);
    HudLayerRegistrationCallback.EVENT.register((layeredDrawer) -> layeredDrawer.attachLayerAfter(
        IdentifiedLayer.EXPERIENCE_LEVEL,
        Identifier.of(Constants.MOD_ID, Constants.MOD_ID),
        INSTANCE::render
    ));
    ItemPickupCallback.EVENT.register(INSTANCE::handleItemPickedUp);
  }

  private void tick(final MinecraftClient client) {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (this.currentlyShownNotifications.isEmpty()) {
      return;
    }

    for (PickupNotificationLine notification : this.currentlyShownNotifications) {
      notification.tick();
      if (notification.isExpired()) {
        this.currentlyShownNotifications.remove(notification);
      }
    }

    while (this.hasRoomForNewNotification()) {
      PickupNotificationLine notification = this.notificationQueue.poll();

      // If NOTIFICATION_QUEUE is empty, notification will be null.
      if (notification == null) {
        break;
      }

      notification.pop();
      this.currentlyShownNotifications.add(notification);
    }
  }

  private void render(DrawContext context, RenderTickCounter tickCounter) {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (this.currentlyShownNotifications.isEmpty()) {
      return;
    }

    if (!MinecraftClient.isHudEnabled()) {
      return;
    }

    int i = 0;
    for (PickupNotificationLine notification : this.currentlyShownNotifications) {
      notification.render(context, i++);
    }
  }

  private void handleItemPickedUp(ItemStack stack) {
    boolean mergedIntoExisting = false;
    ItemStack pickedUp = PickupNotificationsConfig.getInstance().showUniqueInfo.getValue() ?
        stack.copy() :
        new ItemStack(stack.getItem(), stack.getCount());

    for (PickupNotificationLine notification : this.currentlyShownNotifications) {
      if (notification.attemptAdd(pickedUp)) {
        mergedIntoExisting = true;
        notification.pop();
        break;
      }
    }

    if (!mergedIntoExisting) {
      for (PickupNotificationLine notification : this.notificationQueue) {
        if (notification.attemptAdd(pickedUp)) {
          mergedIntoExisting = true;
          break;
        }
      }
    }

    if (!mergedIntoExisting) {
      PickupNotificationLine notification = new PickupNotificationLine(pickedUp);
      if (this.notificationQueue.isEmpty() && this.hasRoomForNewNotification()) {
        notification.pop();
        this.currentlyShownNotifications.add(notification);
      } else {
        this.notificationQueue.add(notification);
      }
    }
  }

  private boolean hasRoomForNewNotification() {
    return this.currentlyShownNotifications.size() <
           PickupNotificationsConfig.getInstance().maxNotifications.getValue();
  }
}
