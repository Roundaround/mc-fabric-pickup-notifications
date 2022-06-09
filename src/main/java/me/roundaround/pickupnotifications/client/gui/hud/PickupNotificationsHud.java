package me.roundaround.pickupnotifications.client.gui.hud;

import java.util.Queue;
import java.util.stream.Collectors;

import com.google.common.collect.Queues;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class PickupNotificationsHud extends DrawableHelper {
  private final Queue<PickupNotificationLine> CURRENTLY_SHOWN_NOTIFICATIONS = Queues.newArrayDeque();
  private final Queue<PickupNotificationLine> NOTIFICATION_QUEUE = Queues.newArrayDeque();

  public void init() {
    ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    HudRenderCallback.EVENT.register(this::render);
    ItemPickupCallback.EVENT.register(this::handleItemPickedUp);
  }

  private void tick(final MinecraftClient client) {
    if (!PickupNotificationsMod.CONFIG.MOD_ENABLED.getValue()) {
      return;
    }

    if (CURRENTLY_SHOWN_NOTIFICATIONS.isEmpty()) {
      return;
    }

    CURRENTLY_SHOWN_NOTIFICATIONS.forEach(notification -> notification.tick());
    CURRENTLY_SHOWN_NOTIFICATIONS.stream()
        .filter(PickupNotificationLine::isExpired)
        .collect(Collectors.toList())
        .forEach(CURRENTLY_SHOWN_NOTIFICATIONS::remove);

    while (CURRENTLY_SHOWN_NOTIFICATIONS.size() < PickupNotificationsMod.CONFIG.MAX_NOTIFICATIONS.getValue()
        && !NOTIFICATION_QUEUE.isEmpty()) {
      CURRENTLY_SHOWN_NOTIFICATIONS.add(NOTIFICATION_QUEUE.poll());
    }
  }

  private void render(MatrixStack matrixStack, float tickDelta) {
    if (!PickupNotificationsMod.CONFIG.MOD_ENABLED.getValue()) {
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
      notification.render(matrixStack, i++);
    }
  }

  private void handleItemPickedUp(ItemStack itemStack) {
    boolean mergedIntoExisting = false;
    ItemStack pickedUp = itemStack.copy();
    pickedUp.removeSubNbt("Enchantments");
    pickedUp.setDamage(pickedUp.getMaxDamage());

    for (PickupNotificationLine notification : CURRENTLY_SHOWN_NOTIFICATIONS) {
      if (mergedIntoExisting = notification.attemptAdd(pickedUp)) {
        notification.pop();
        break;
      }
    }

    if (!mergedIntoExisting) {
      for (PickupNotificationLine notification : NOTIFICATION_QUEUE) {
        if (mergedIntoExisting = notification.attemptAdd(pickedUp)) {
          break;
        }
      }
    }

    if (!mergedIntoExisting) {
      PickupNotificationLine notification = new PickupNotificationLine(pickedUp);
      if (CURRENTLY_SHOWN_NOTIFICATIONS.size() < PickupNotificationsMod.CONFIG.MAX_NOTIFICATIONS.getValue()
          && NOTIFICATION_QUEUE.isEmpty()) {
        notification.pop();
        CURRENTLY_SHOWN_NOTIFICATIONS.add(notification);
      } else {
        NOTIFICATION_QUEUE.add(notification);
      }
    }
  }
}
