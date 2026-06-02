package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.event.ExperiencePickup;
import me.roundaround.pickupnotifications.event.ItemPickup;
import me.roundaround.trove.event.ClientLifecycle;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class PickupNotificationsHud {
  private static final PickupNotificationsHud INSTANCE = new PickupNotificationsHud();

  private final CopyOnWriteArrayList<PickupNotification<?>> currentlyShownNotifications = new CopyOnWriteArrayList<>();
  private final ConcurrentLinkedDeque<PickupNotification<?>> notificationQueue = new ConcurrentLinkedDeque<>();

  public static void init() {
    ClientLifecycle.onTick(INSTANCE::tick);
    ExperiencePickup.register(INSTANCE::handleExperiencePickedUp);
    ItemPickup.register(INSTANCE::handleItemPickedUp);
  }

  public static void renderHud(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
    INSTANCE.render(context, tickCounter);
  }

  private void tick() {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (this.currentlyShownNotifications.isEmpty()) {
      return;
    }

    for (PickupNotification<?> notification : this.currentlyShownNotifications) {
      notification.tick();
      if (notification.isExpired()) {
        this.currentlyShownNotifications.remove(notification);
      }
    }

    while (this.hasRoomForNewNotification()) {
      PickupNotification<?> notification = this.notificationQueue.poll();

      // If the queue is empty, notification will be null.
      if (notification == null) {
        break;
      }

      notification.pop();
      this.currentlyShownNotifications.add(notification);
    }
  }

  private void render(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
    if (!PickupNotificationsConfig.getInstance().modEnabled.getValue()) {
      return;
    }

    if (this.currentlyShownNotifications.isEmpty()) {
      return;
    }

    if (!Minecraft.renderNames()) {
      return;
    }

    int i = 0;
    for (PickupNotification<?> notification : this.currentlyShownNotifications) {
      notification.render(context, tickCounter, i++);
    }
  }

  private void handleExperiencePickedUp(int amount) {
    if (!PickupNotificationsConfig.getInstance().trackExperience.getValue()) {
      return;
    }

    this.handlePickup(amount, ExperiencePickupNotification::new);
  }

  private void handleItemPickedUp(ItemStack stack) {
    ItemStack pickedUp = PickupNotificationsConfig.getInstance().showUniqueInfo.getValue() ?
        stack.copy() :
        new ItemStack(stack.getItem(), stack.getCount());
    this.handlePickup(pickedUp, ItemPickupNotification::new);
  }

  private <T> void handlePickup(T addition, Function<T, PickupNotification<T>> factory) {
    boolean mergedIntoExisting = false;

    for (PickupNotification<?> notification : this.currentlyShownNotifications) {
      if (notification.attemptAdd(addition)) {
        mergedIntoExisting = true;
        notification.extendTime();
        break;
      }
    }

    if (!mergedIntoExisting) {
      for (PickupNotification<?> notification : this.notificationQueue) {
        if (notification.attemptAdd(addition)) {
          mergedIntoExisting = true;
          break;
        }
      }
    }

    if (!mergedIntoExisting) {
      PickupNotification<T> notification = factory.apply(addition);
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
