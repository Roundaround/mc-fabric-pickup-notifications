package me.roundaround.pickupnotifications.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class ConfigOption<T> {
    private final String id;
    private final T defaultValue;

    private T value;
    private T lastSavedValue;

    public ConfigOption(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return this.id;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }

    public void markValueAsSaved() {
        this.lastSavedValue = this.value;
    }

    public boolean isDirty() {
        return !this.value.equals(this.lastSavedValue);
    }

    public boolean isModified() {
        return !this.value.equals(this.defaultValue);
    }

    public void readFromJsonRoot(JsonObject root) {
        this.setValue(this.deserializeFromJson(root.get(this.id)));
    }

    public void writeToJsonRoot(JsonObject root) {
        root.add(this.id, this.serializeToJson());
        this.markValueAsSaved();
    }

    public abstract T deserializeFromJson(JsonElement data);
    public abstract JsonElement serializeToJson();
}
