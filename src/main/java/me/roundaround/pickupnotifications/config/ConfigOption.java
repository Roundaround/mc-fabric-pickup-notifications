package me.roundaround.pickupnotifications.config;

public class ConfigOption<T> {
    public final String id;
    public final T defaultValue;
    private T value;

    public ConfigOption(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }
}
