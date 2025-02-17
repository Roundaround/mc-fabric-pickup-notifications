package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class PickupNotificationsHud {
  private static final PickupNotificationsHud INSTANCE = new PickupNotificationsHud();

  private final CopyOnWriteArrayList<PickupNotificationLine> CURRENTLY_SHOWN_NOTIFICATIONS =
      new CopyOnWriteArrayList<>();
  private final ConcurrentLinkedDeque<PickupNotificationLine> NOTIFICATION_QUEUE = new ConcurrentLinkedDeque<>();

  public static void init() {
    ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::tick);
    HudRenderCallback.EVENT.register(INSTANCE::render);
    ItemPickupCallback.EVENT.register(INSTANCE::handleItemPickedUp);
  }

  private void tick(final MinecraftClient client) {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (CURRENTLY_SHOWN_NOTIFICATIONS.isEmpty()) {
      return;
    }

    for (PickupNotificationLine notification : CURRENTLY_SHOWN_NOTIFICATIONS) {
      notification.tick();
      if (notification.isExpired()) {
        CURRENTLY_SHOWN_NOTIFICATIONS.remove(notification);
      }
    }

    while (hasRoomForNewNotification()) {
      PickupNotificationLine notification = NOTIFICATION_QUEUE.poll();

      // If NOTIFICATION_QUEUE is empty, notification will be null.
      if (notification == null) {
        break;
      }

      notification.pop();
      CURRENTLY_SHOWN_NOTIFICATIONS.add(notification);
    }
  }

  private void render(DrawContext drawContext, float tickDelta) {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (CURRENTLY_SHOWN_NOTIFICATIONS.isEmpty()) {
      return;
    }

    if (!MinecraftClient.isHudEnabled()) {
      return;
    }

    int i = 0;
    for (PickupNotificationLine notification : CURRENTLY_SHOWN_NOTIFICATIONS) {
      notification.render(drawContext, i++);
    }
  }

  private void handleItemPickedUp(ItemStack stack) {
    boolean mergedIntoExisting = false;
    ItemStack pickedUp = PickupNotificationsConfig.getInstance().showUniqueInfo.getValue() ?
        stack.copy() :
        new ItemStack(stack.getItem(), stack.getCount());

    for (PickupNotificationLine notification : CURRENTLY_SHOWN_NOTIFICATIONS) {
      if (notification.attemptAdd(pickedUp)) {
        mergedIntoExisting = true;
        notification.pop();
        break;
      }
    }

    if (!mergedIntoExisting) {
      for (PickupNotificationLine notification : NOTIFICATION_QUEUE) {
        if (notification.attemptAdd(pickedUp)) {
          mergedIntoExisting = true;
          break;
        }
      }
    }

    if (!mergedIntoExisting) {
      PickupNotificationLine notification = new PickupNotificationLine(pickedUp);
      if (NOTIFICATION_QUEUE.isEmpty() && hasRoomForNewNotification()) {
        notification.pop();
        CURRENTLY_SHOWN_NOTIFICATIONS.add(notification);
      } else {
        NOTIFICATION_QUEUE.add(notification);
      }
    }
  }

  private boolean hasRoomForNewNotification() {
    return CURRENTLY_SHOWN_NOTIFICATIONS.size() < PickupNotificationsConfig.getInstance().maxNotifications.getValue();
  }
}
