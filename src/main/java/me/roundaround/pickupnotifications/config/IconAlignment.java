package me.roundaround.pickupnotifications.config;

import me.roundaround.pickupnotifications.roundalib.config.value.EnumValue;

import java.util.Arrays;

public enum IconAlignment implements EnumValue<IconAlignment> {
  OUTSIDE("outside"), INSIDE("inside"), LEFT("left"), RIGHT("right");

  private final String id;

  IconAlignment(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return "pickupnotifications.iconAlignment." + this.id;
  }

  @Override
  public IconAlignment getFromId(String id) {
    return fromId(id);
  }

  @Override
  public IconAlignment getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public IconAlignment getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static IconAlignment getDefault() {
    return OUTSIDE;
  }

  public static IconAlignment fromId(String id) {
    return Arrays.stream(IconAlignment.values())
        .filter(alignment -> alignment.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }
}
