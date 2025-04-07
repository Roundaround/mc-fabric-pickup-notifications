package me.roundaround.pickupnotifications.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ExperiencePickup {
  Event<ExperiencePickup> EVENT = EventFactory.createArrayBacked(
      ExperiencePickup.class, (listeners) -> (amount) -> {
        for (ExperiencePickup listener : listeners) {
          listener.interact(amount);
        }
      }
  );

  static void emit(int amount) {
    EVENT.invoker().interact(amount);
  }

  void interact(int amount);
}
