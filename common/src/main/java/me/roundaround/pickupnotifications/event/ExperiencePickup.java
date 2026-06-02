package me.roundaround.pickupnotifications.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ExperiencePickup {
  List<ExperiencePickup> LISTENERS = new CopyOnWriteArrayList<>();

  static void register(ExperiencePickup listener) {
    LISTENERS.add(listener);
  }

  static void emit(int amount) {
    for (ExperiencePickup listener : LISTENERS) {
      listener.interact(amount);
    }
  }

  void interact(int amount);
}
