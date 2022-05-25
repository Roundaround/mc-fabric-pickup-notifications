package me.roundaround.pickupnotifications.client.gui.hud;

import com.google.common.collect.Queues;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Queue;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class PickupNotificationsHud extends DrawableHelper {
  private final Queue<PickupNotificationLine> CURRENTLY_SHOWN_NOTIFICATIONS = Queues.newArrayDeque();
  private final Queue<PickupNotificationLine> NOTIFICATION_QUEUE = Queues.newArrayDeque();

  private MinecraftClient minecraft;

  public void init() {
    minecraft = MinecraftClient.getInstance();

    HudRenderCallback.EVENT.register(this::render);
    ItemPickupCallback.EVENT.register(this::handleItemPickedUp);
  }

  private void render(MatrixStack matrixStack, float tickDelta) {
    if (CURRENTLY_SHOWN_NOTIFICATIONS.isEmpty()) {
      return;
    }

    if (!minecraft.isPaused()) {
      tick(tickDelta);
    }

    if (!MinecraftClient.isHudEnabled()) {
      return;
    }

    int i = 0;
    for (PickupNotificationLine notification : CURRENTLY_SHOWN_NOTIFICATIONS) {
      notification.render(matrixStack, i++);
    }
  }

  private void tick(float tickDelta) {
    CURRENTLY_SHOWN_NOTIFICATIONS.forEach(notification -> notification.tick(tickDelta));
    CURRENTLY_SHOWN_NOTIFICATIONS.stream()
        .filter(PickupNotificationLine::isExpired)
        .collect(Collectors.toList())
        .forEach(CURRENTLY_SHOWN_NOTIFICATIONS::remove);

    while (CURRENTLY_SHOWN_NOTIFICATIONS.size() < PickupNotificationsConfig.MAX_NOTIFICATIONS.getValue()
        && !NOTIFICATION_QUEUE.isEmpty()) {
      CURRENTLY_SHOWN_NOTIFICATIONS.add(NOTIFICATION_QUEUE.poll());
    }
  }

  private void handleItemPickedUp(PlayerEntity player, ItemStack itemStack) {
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
      if (CURRENTLY_SHOWN_NOTIFICATIONS.size() < PickupNotificationsConfig.MAX_NOTIFICATIONS.getValue()
          && NOTIFICATION_QUEUE.isEmpty()) {
        notification.pop();
        CURRENTLY_SHOWN_NOTIFICATIONS.add(notification);
      } else {
        NOTIFICATION_QUEUE.add(notification);
      }
    }
  }
}
